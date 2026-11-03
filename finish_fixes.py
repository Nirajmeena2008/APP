import re
import os

def replace(path, old, new):
    if not os.path.exists(path): return
    with open(path, "r") as f: content = f.read()
    content = content.replace(old, new)
    with open(path, "w") as f: f.write(content)

# AlarmForegroundService
with open("app/src/main/java/com/yourname/liquidalarm/alarm/AlarmForegroundService.kt", "r") as f:
    afs = f.read()
afs = afs.replace("currentAlarm.autoDismiss.enabled", "currentAlarm?.autoDismiss?.enabled == true")
afs = afs.replace("currentAlarm.autoDismiss.timeoutMinutes", "(currentAlarm?.autoDismiss?.timeoutMinutes ?: 10)")
afs = afs.replace("if (alarm != null)", "val currentAlarm = alarm\n            if (currentAlarm != null)")
afs = afs.replace("alarm!!.autoDismiss.enabled", "currentAlarm.autoDismiss.enabled")
afs = afs.replace("alarm!!.autoDismiss.timeoutMinutes", "currentAlarm.autoDismiss.timeoutMinutes")
afs = afs.replace("mediaPlayerManager?.playAlarm(alarm!!,", "mediaPlayerManager?.playAlarm(currentAlarm,")
with open("app/src/main/java/com/yourname/liquidalarm/alarm/AlarmForegroundService.kt", "w") as f: f.write(afs)

# AlarmEntity
with open("app/src/main/java/com/yourname/liquidalarm/data/AlarmEntity.kt", "r") as f: ae = f.read()
ae = ae.replace("import com.yourname.liquidalarm.data.MonthDay", "import kotlinx.datetime.Month\nimport com.yourname.liquidalarm.data.MonthDay")
ae = ae.replace("candidate.toEpochMilliseconds() <= now.toEpochMilliseconds()", "candidate <= now")
ae = ae.replace("candidate.toEpochMilliseconds() > now.toEpochMilliseconds()", "candidate > now")
ae = ae.replace("next.toEpochMilliseconds() <= now.toEpochMilliseconds()", "next <= now")
ae = ae.replace("candidate.toEpochMilliseconds() <= now.toEpochMilliseconds()", "candidate <= now")
ae = ae.replace("var candidate = kotlinx.datetime.LocalDateTime(zdt.year, zdt.month, zdt.dayOfMonth, targetTime.hour, targetTime.minute).let { kotlinx.datetime.toInstant(it, zone) }", "var candidate = kotlinx.datetime.LocalDateTime(zdt.year, zdt.month, zdt.dayOfMonth, targetTime.hour, targetTime.minute).let { kotlinx.datetime.toInstant(it, zone) }")
with open("app/src/main/java/com/yourname/liquidalarm/data/AlarmEntity.kt", "w") as f: f.write(ae)

# Type.kt
with open("app/src/main/java/com/yourname/liquidalarm/ui/theme/Type.kt", "r") as f: t = f.read()
t = t.replace("import com.yourname.liquidalarm.R\nimport com.yourname.liquidalarm.R", "import com.yourname.liquidalarm.R")
t = t.replace("import com.yourname.liquidalarm.R\n\nimport com.yourname.liquidalarm.R", "import com.yourname.liquidalarm.R")
with open("app/src/main/java/com/yourname/liquidalarm/ui/theme/Type.kt", "w") as f: f.write(t)

# WorldClockScreen
with open("app/src/main/java/com/yourname/liquidalarm/ui/screens/WorldClockScreen.kt", "r") as f: wcs = f.read()
wcs = wcs.replace("Modifier.size", "androidx.compose.foundation.layout.size")
wcs = wcs.replace("Modifier.padding", "androidx.compose.foundation.layout.padding")
wcs = wcs.replace("Modifier.background", "androidx.compose.foundation.background")
with open("app/src/main/java/com/yourname/liquidalarm/ui/screens/WorldClockScreen.kt", "w") as f: f.write(wcs)

# TimerScreen and StopwatchScreen missing imports
for fpath in ["app/src/main/java/com/yourname/liquidalarm/ui/screens/TimerScreen.kt", "app/src/main/java/com/yourname/liquidalarm/ui/screens/StopwatchScreen.kt", "app/src/main/java/com/yourname/liquidalarm/ui/screens/ActiveAlarmScreen.kt", "app/src/main/java/com/yourname/liquidalarm/ui/screens/AlarmListScreen.kt", "app/src/main/java/com/yourname/liquidalarm/ui/screens/CreateAlarmScreen.kt", "app/src/main/java/com/yourname/liquidalarm/ui/screens/SettingsScreen.kt"]:
    if not os.path.exists(fpath): continue
    with open(fpath, "r") as f: code = f.read()
    code = code.replace("import androidx.compose.ui.Modifier", "import androidx.compose.ui.Modifier\nimport androidx.compose.foundation.layout.*\nimport androidx.compose.foundation.background\nimport androidx.compose.material.icons.Icons\nimport androidx.compose.material.icons.filled.*\nimport androidx.compose.material.icons.outlined.*\nimport androidx.compose.material3.Icon\nimport androidx.compose.ui.graphics.graphicsLayer\nimport kotlin.time.Duration.Companion.seconds")
    code = code.replace("Modifier.height", "Modifier.height") # Dummy
    with open(fpath, "w") as f: f.write(code)

