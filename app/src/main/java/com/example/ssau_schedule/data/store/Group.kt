package com.example.ssau_schedule.data.store

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

val Context.groupStore by preferencesDataStore(name = "group")

@Serializable
data class Group(val id: Int, val name: String)

class GroupStore {
    class Keys {
        companion object {
            val CURRENT_GROUP_ID = intPreferencesKey("group_id")
            val CURRENT_GROUP_NAME = stringPreferencesKey("group_name")
        }
    }

    companion object {
        suspend fun setCurrentGroup(
            group: Group,
            context: Context,
        ) {
            context.groupStore.edit { groupStore ->
                groupStore[Keys.CURRENT_GROUP_ID] = group.id
                groupStore[Keys.CURRENT_GROUP_NAME] = group.name
            }
        }

        fun setCurrentGroup(
            group: Group,
            context: Context,
            scope: CoroutineScope,
            callback: (() -> Unit)? = null
        ) = scope.launch { setCurrentGroup(group, context) }.run { callback?.invoke() }

        suspend fun getCurrentGroup(context: Context): Group? {
            val currentGroupId = context.groupStore.data
                .map { groupStore -> groupStore[Keys.CURRENT_GROUP_ID] }.first()
            val currentGroupName = context.groupStore.data
                .map { groupStore -> groupStore[Keys.CURRENT_GROUP_NAME] }.first()
            return if(currentGroupId != null && currentGroupName != null)
                Group(id = currentGroupId,
                    name = currentGroupName)
            else null
        }

        fun getCurrentGroup(
            context: Context,
            scope: CoroutineScope,
            callback: (group: Group?) -> Unit
        ) = scope.launch { callback(getCurrentGroup(context)) }
    }


}