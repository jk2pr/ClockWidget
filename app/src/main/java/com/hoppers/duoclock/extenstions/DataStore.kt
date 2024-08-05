import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

const val fileName = "widget_store"
val Context.dataStore: DataStore<Preferences>
    by preferencesDataStore(name = fileName)
