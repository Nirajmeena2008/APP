# Liquid Alarm - Fish Shell Functions
# Source this file: source /path/to/liquid-alarm.fish
# Then use: la-build, la-install, etc.

set -gx LIQUID_ALARM_DIR "/home/animer/Web Development/New Folder/LiquidAlarm"
set -gx LIQUID_ALARM_KEYSTORE "$HOME/liquid-alarm-release.keystore"
set -gx LIQUID_ALARM_STORE_PASS "YOUR_STORE_PASSWORD"
set -gx LIQUID_ALARM_KEY_PASS "YOUR_KEY_PASSWORD"

function la-cd
    cd $LIQUID_ALARM_DIR
    echo "📁 Now in $LIQUID_ALARM_DIR"
end

function la-font
    la-cd
    mkdir -p app/src/main/res/font
    wget "https://github.com/google/fonts/raw/main/ofl/inter/Inter%5Bopsz,wght%5D.ttf" \
        -O "app/src/main/res/font/Inter-VariableFont_opsz,wght.ttf"
    echo "✅ Inter font downloaded"
end

function la-wrapper
    la-cd
    if not test -f ./gradlew
        gradle wrapper
        chmod +x ./gradlew
        echo "✅ Gradle wrapper generated"
    else
        echo "✅ Gradle wrapper already exists"
    end
end

function la-debug
    la-cd
    la-wrapper
    ./gradlew assembleDebug
    if test $status -eq 0
        set APK (find app/build/outputs/apk/debug -name "*.apk" | head -1)
        echo "✅ Debug APK: $APK"
        echo "Install: adb install -r $APK"
    else
        echo "❌ Build failed"
        return 1
    end
end

function la-release
    la-cd
    la-wrapper
    ./gradlew assembleRelease
    if test $status -ne 0
        echo "❌ Build failed"
        return 1
    end

    set UNSIGNED (find app/build/outputs/apk/release -name "*unsigned.apk" | head -1)
    if test -z "$UNSIGNED"
        echo "❌ Unsigned APK not found"
        return 1
    end

    if not test -f $LIQUID_ALARM_KEYSTORE
        echo "❌ Keystore not found: $LIQUID_ALARM_KEYSTORE"
        echo "Create with: la-keystore"
        return 1
    end

    set OUTPUT "$HOME/liquid-alarm-v1.0.apk"
    apksigner sign \
        --ks $LIQUID_ALARM_KEYSTORE \
        --ks-key-alias liquid-alarm \
        --ks-pass pass:$LIQUID_ALARM_STORE_PASS \
        --key-pass pass:$LIQUID_ALARM_KEY_PASS \
        --out $OUTPUT \
        $UNSIGNED

    if test $status -eq 0
        echo "✅ Signed APK: $OUTPUT"
        apksigner verify --print-certs $OUTPUT
    else
        echo "❌ Signing failed"
        return 1
    end
end

function la-install-debug
    la-debug
    if test $status -eq 0
        set APK (find app/build/outputs/apk/debug -name "*.apk" | head -1)
        adb install -r $APK
        if test $status -eq 0
            echo "✅ Installed on device"
        else
            echo "❌ Install failed - check device connection"
        end
    end
end

function la-install-release
    la-release
    if test $status -eq 0
        set OUTPUT "$HOME/liquid-alarm-v1.0.apk"
        adb install -r $OUTPUT
        if test $status -eq 0
            echo "✅ Release APK installed on device"
        else
            echo "❌ Install failed"
        end
    end
end

function la-keystore
    keytool -genkey -v \
        -keystore $LIQUID_ALARM_KEYSTORE \
        -alias liquid-alarm \
        -keyalg RSA \
        -keysize 2048 \
        -validity 10000 \
        -storepass $LIQUID_ALARM_STORE_PASS \
        -keypass $LIQUID_ALARM_KEY_PASS \
        -dname "CN=Liquid Alarm, OU=Personal, O=Personal, L=City, S=State, C=US"
    echo "✅ Keystore created at $LIQUID_ALARM_KEYSTORE"
    echo "⚠️  Backup this file and passwords!"
end

function la-clean
    la-cd
    ./gradlew clean
    echo "✅ Clean complete"
end

function la-help
    echo "Liquid Alarm - Fish Shell Commands"
    echo ""
    echo "Setup:"
    echo "  la-cd          Change to project directory"
    echo "  la-font        Download Inter variable font"
    echo "  la-wrapper     Generate Gradle wrapper"
    echo "  la-keystore    Create signing keystore"
    echo ""
    echo "Build:"
    echo "  la-debug       Build debug APK"
    echo "  la-release     Build and sign release APK"
    echo "  la-clean       Clean build artifacts"
    echo ""
    echo "Install:"
    echo "  la-install-debug    Build & install debug via ADB"
    echo "  la-install-release  Build, sign & install release via ADB"
    echo ""
    echo "Variables (edit in this file):"
    echo "  LIQUID_ALARM_DIR       Project directory"
    echo "  LIQUID_ALARM_KEYSTORE  Keystore path"
    echo "  LIQUID_ALARM_STORE_PASS  Keystore password"
    echo "  LIQUID_ALARM_KEY_PASS    Key password"
end

# Auto-complete
complete -c la-cd -f
complete -c la-font -f
complete -c la-wrapper -f
complete -c la-debug -f
complete -c la-release -f
complete -c la-install-debug -f
complete -c la-install-release -f
complete -c la-keystore -f
complete -c la-clean -f
complete -c la-help -f

echo "🐟 Liquid Alarm fish functions loaded. Run 'la-help' for commands."
