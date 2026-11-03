import re

with open("app/src/main/AndroidManifest.xml", "r") as f:
    code = f.read()

code = code.replace('android:icon="@mipmap/ic_launcher"', '')
code = code.replace('android:roundIcon="@mipmap/ic_launcher_round"', '')
code = code.replace('android:foregroundServiceType="alarm|mediaPlayback"', 'android:foregroundServiceType="mediaPlayback|specialUse"')

with open("app/src/main/AndroidManifest.xml", "w") as f:
    f.write(code)
