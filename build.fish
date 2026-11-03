#!/usr/bin/env fish
# Liquid Alarm - Build & Install Script for Fish Shell

set PROJECT_DIR "/home/animer/Web Development/New Folder/LiquidAlarm"
set KEYSTORE_PATH "$HOME/liquid-alarm-release.keystore"
set APK_OUTPUT "$HOME/liquid-alarm-v1.0.apk"
set STORE_PASS "YOUR_STORE_PASSWORD"
set KEY_PASS "YOUR_KEY_PASSWORD"

cd $PROJECT_DIR

# Function to print colored output
function info
    echo -e "\033[1;34m[INFO]\033[0m $argv"
end

function success
    echo -e "\033[1;32m[SUCCESS]\033[0m $argv"
end

function error
    echo -e "\033[1;31m[ERROR]\033[0m $argv"
end

function warn
    echo -e "\033[1;33m[WARN]\033[0m $argv"
end

# Check if gradle wrapper exists
if not test -f ./gradlew
    info "Generating Gradle wrapper..."
    gradle wrapper
    if test $status -ne 0
        error "Failed to generate Gradle wrapper. Install gradle first: sudo apt install gradle"
        exit 1
    end
end

# Make wrapper executable
chmod +x ./gradlew

# Build type selection
if test (count $argv) -eq 0
    set BUILD_TYPE "debug"
else
    set BUILD_TYPE $argv[1]
end

switch $BUILD_TYPE
    case debug
        info "Building DEBUG APK..."
        ./gradlew assembleDebug
        if test $status -eq 0
            set APK_PATH (find app/build/outputs/apk/debug -name "*.apk" | head -1)
            success "Debug APK built: $APK_PATH"
            echo "Install with: adb install -r $APK_PATH"
        else
            error "Debug build failed"
            exit 1
        end

    case release
        info "Building RELEASE APK..."
        ./gradlew assembleRelease
        if test $status -ne 0
            error "Release build failed"
            exit 1
        end

        set UNSIGNED_APK (find app/build/outputs/apk/release -name "*unsigned.apk" | head -1)
        if test -z "$UNSIGNED_APK"
            error "Unsigned APK not found"
            exit 1
        end

        # Check keystore
        if not test -f $KEYSTORE_PATH
            warn "Keystore not found at $KEYSTORE_PATH"
            echo "Create it with:"
            echo "  keytool -genkey -v -keystore $KEYSTORE_PATH -alias liquid-alarm -keyalg RSA -keysize 2048 -validity 10000 -storepass $STORE_PASS -keypass $KEY_PASS -dname \"CN=Liquid Alarm, OU=Personal, O=Personal, L=City, S=State, C=US\""
            exit 1
        end

        info "Signing APK..."
        apksigner sign \
            --ks $KEYSTORE_PATH \
            --ks-key-alias liquid-alarm \
            --ks-pass pass:$STORE_PASS \
            --key-pass pass:$KEY_PASS \
            --out $APK_OUTPUT \
            $UNSIGNED_APK

        if test $status -eq 0
            success "Signed APK: $APK_OUTPUT"
            apksigner verify --print-certs $APK_OUTPUT
            echo ""
            echo "Install on phone:"
            echo "  adb install -r $APK_OUTPUT"
            echo "  OR copy to phone and install via Files app"
        else
            error "APK signing failed"
            exit 1
        end

    case install-debug
        info "Building and installing DEBUG APK..."
        ./gradlew assembleDebug
        if test $status -eq 0
            set APK_PATH (find app/build/outputs/apk/debug -name "*.apk" | head -1)
            adb install -r $APK_PATH
            if test $status -eq 0
                success "Debug APK installed on device"
            else
                error "Install failed. Check USB debugging and device connection."
            end
        else
            error "Build failed"
        end

    case install-release
        info "Building, signing, and installing RELEASE APK..."
        ./gradlew assembleRelease
        if test $status -eq 0
            set UNSIGNED_APK (find app/build/outputs/apk/release -name "*unsigned.apk" | head -1)
            if test -f $KEYSTORE_PATH
                apksigner sign \
                    --ks $KEYSTORE_PATH \
                    --ks-key-alias liquid-alarm \
                    --ks-pass pass:$STORE_PASS \
                    --key-pass pass:$KEY_PASS \
                    --out $APK_OUTPUT \
                    $UNSIGNED_APK
                if test $status -eq 0
                    adb install -r $APK_OUTPUT
                    if test $status -eq 0
                        success "Release APK installed on device"
                    else
                        error "Install failed"
                    end
                else
                    error "Signing failed"
                end
            else
                error "Keystore not found at $KEYSTORE_PATH"
            end
        else
            error "Build failed"
        end

    case clean
        info "Cleaning project..."
        ./gradlew clean
        success "Clean complete"

    case font
        info "Downloading Inter variable font..."
        mkdir -p app/src/main/res/font
        cd app/src/main/res/font
        wget "https://github.com/google/fonts/raw/main/ofl/inter/Inter%5Bopsz,wght%5D.ttf" \
            -O "Inter-VariableFont_opsz,wght.ttf"
        success "Font downloaded to app/src/main/res/font/"

    case '*'
        echo "Usage: ./build.fish [command]"
        echo ""
        echo "Commands:"
        echo "  debug         Build debug APK"
        echo "  release       Build and sign release APK"
        echo "  install-debug Build and install debug APK via ADB"
        echo "  install-release Build, sign, and install release APK via ADB"
        echo "  clean         Clean build artifacts"
        echo "  font          Download Inter variable font"
        echo ""
        echo "Examples:"
        echo "  ./build.fish debug"
        echo "  ./build.fish release"
        echo "  ./build.fish install-debug"
end
