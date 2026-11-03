import re

with open("app/src/main/java/com/yourname/liquidalarm/data/PreferencesManager.kt", "r") as f:
    code = f.read()

code = code.replace("import androidx.datastore.rxjava3.RxDataStore", "import androidx.datastore.core.DataStore\nimport androidx.datastore.preferences.preferencesDataStore\nimport kotlinx.coroutines.flow.map\nimport kotlinx.coroutines.rx3.asFlowable")
code = code.replace("private val dataStore: RxDataStore<Preferences> = context.createDataStore(\"liquid_alarm_prefs\")", "private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(\"liquid_alarm_prefs\")\n    private val dataStore = context.dataStore")

code = re.sub(r'val (\w+): Flowable<([^>]+)> = dataStore\.data\.map \{ it\[([^\]]+)\] \?\: ([^\}]+) \}',
              r'val \1: Flowable<\2> = dataStore.data.map { it[\3] ?: \4 }.asFlowable()', code)

code = re.sub(r'suspend fun (\w+)\(value: ([^\)]+)\) = dataStore\.updateData \{ it\[([^\]]+)\] = value \}',
              r'suspend fun \1(value: \2) { dataStore.edit { it[\3] = value } }', code)

code = code.replace("suspend fun setFirstLaunchComplete() = dataStore.updateData { it[KEY_FIRST_LAUNCH] = false }", "suspend fun setFirstLaunchComplete() { dataStore.edit { it[KEY_FIRST_LAUNCH] = false } }")
code = code.replace("fun themeSingle(): Single<Int> = theme.firstElement()", "fun themeSingle(): Single<Int> = theme.firstOrError()")
code = code.replace("fun timeFormat24hSingle(): Single<Boolean> = timeFormat24h.firstElement()", "fun timeFormat24hSingle(): Single<Boolean> = timeFormat24h.firstOrError()")
code = code.replace("fun defaultSnoozeMinutesSingle(): Single<Int> = defaultSnoozeMinutes.firstElement()", "fun defaultSnoozeMinutesSingle(): Single<Int> = defaultSnoozeMinutes.firstOrError()")

with open("app/src/main/java/com/yourname/liquidalarm/data/PreferencesManager.kt", "w") as f:
    f.write(code)

with open("app/src/main/java/com/yourname/liquidalarm/data/AlarmRepository.kt", "r") as f:
    repo = f.read()
repo = repo.replace("import androidx.datastore.rxjava3.RxDataStore", "import androidx.datastore.core.DataStore\nimport androidx.datastore.preferences.preferencesDataStore\nimport kotlinx.coroutines.flow.map\nimport kotlinx.coroutines.rx3.asFlowable\nimport kotlinx.coroutines.flow.firstOrNull\nimport kotlinx.coroutines.flow.first")
repo = repo.replace("private val dataStore: RxDataStore<Preferences> = context.createDataStore(\"liquid_alarm_alarms\")", "private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(\"liquid_alarm_alarms\")\n    private val dataStore = context.dataStore")

repo = repo.replace("suspend fun getAlarms(): List<AlarmEntity> = alarmsFlow.firstElement().blockingGet()", "suspend fun getAlarms(): List<AlarmEntity> = dataStore.data.map { prefs -> AlarmSerializer.deserialize(prefs[ALARMS_KEY] ?: \"[]\") }.firstOrNull() ?: emptyList()")
repo = repo.replace("dataStore.updateData { it[ALARMS_KEY] = jsonString }.await()", "dataStore.edit { it[ALARMS_KEY] = jsonString }")

repo = repo.replace("import androidx.datastore.preferences.core.preferencesKey", "")
repo = repo.replace("import androidx.datastore.preferences.rxjava3.preferences", "")

repo = repo.replace("val alarmsFlow: Flowable<List<AlarmEntity>> = dataStore.data\\n        .map { prefs ->\\n            val jsonString = prefs[ALARMS_KEY] ?: \\\"[]\\\"\\n            AlarmSerializer.deserialize(jsonString)\\n        }\\n        .onErrorReturn { emptyList() }\\n        .subscribeOn(Schedulers.io())", "val alarmsFlow: Flowable<List<AlarmEntity>> = dataStore.data.map { prefs -> AlarmSerializer.deserialize(prefs[ALARMS_KEY] ?: \"[]\") }.asFlowable().onErrorReturn { emptyList() }")
with open("app/src/main/java/com/yourname/liquidalarm/data/AlarmRepository.kt", "w") as f:
    f.write(repo)
