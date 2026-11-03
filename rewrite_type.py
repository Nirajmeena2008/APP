import re

with open("app/src/main/java/com/yourname/liquidalarm/ui/theme/Type.kt", "r") as f:
    code = f.read()

code = code.replace("package com.yourname.liquidalarm.ui.theme\n", "package com.yourname.liquidalarm.ui.theme\n\nimport com.yourname.liquidalarm.R\n")
code = re.sub(r'androidx\.compose\.ui\.unit\.sp\(([-]?\d+(\.\d+)?)\)', r'\1.sp', code)

with open("app/src/main/java/com/yourname/liquidalarm/ui/theme/Type.kt", "w") as f:
    f.write(code)
