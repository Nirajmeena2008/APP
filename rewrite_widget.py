import re

with open("app/src/main/java/com/yourname/liquidalarm/widget/AlarmWidget.kt", "r") as f:
    code = f.read()

code = code.replace("import androidx.compose.ui.Alignment", "import androidx.glance.layout.Alignment")
code = code.replace("import androidx.compose.ui.Modifier", "import androidx.glance.GlanceModifier")
code = code.replace("Modifier.", "GlanceModifier.")
code = code.replace("import androidx.compose.ui.graphics.Color", "import androidx.glance.unit.ColorProvider\nimport androidx.compose.ui.graphics.Color")
code = code.replace("color = Color.", "color = ColorProvider(Color.")
code = code.replace("Color.White)", "Color.White))")
code = code.replace("Color.LightGray)", "Color.LightGray))")
code = code.replace("Color(0xFF", "ColorProvider(Color(0xFF")
code = code.replace(")", "))") # Too dangerous

with open("app/src/main/java/com/yourname/liquidalarm/widget/AlarmWidget.kt", "w") as f:
    pass

