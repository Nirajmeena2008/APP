import re

with open("app/src/main/java/com/yourname/liquidalarm/ui/theme/Theme.kt", "r") as f:
    code = f.read()

code = code.replace("import android.graphics.Color", "")
code = code.replace("insetsController.systemBarsAppearance = 0", "insetsController.setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)")
code = code.replace("insetsController.systemBarsAppearance = WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS", "insetsController.setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)")
code = code.replace("Color.TRANSPARENT.toArgb()", "Color.Transparent.toArgb()")

with open("app/src/main/java/com/yourname/liquidalarm/ui/theme/Theme.kt", "w") as f:
    f.write(code)
