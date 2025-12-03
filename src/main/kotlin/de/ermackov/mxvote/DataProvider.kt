package de.ermackov.mxvote

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import de.ermackov.mxvote.entities.AutoVotes
import de.ermackov.mxvote.entities.Requests
import de.ermackov.mxvote.entities.Time
import de.ermackov.mxvote.entities.UserDataWrapper
import de.ermackov.mxvote.entities.UserEntry
import de.ermackov.mxvote.entities.Weather
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class DataProvider(val plugin: JavaPlugin) {

    val gson: Gson? = GsonBuilder().setPrettyPrinting().create()

    fun getUserFile(): File {
        val folder = plugin.dataFolder
        if (!folder.exists()) folder.mkdirs()
        return File(folder, "data.json")
    }

    fun getUserData(): UserDataWrapper {
        val file = getUserFile()

        if (!file.exists()) {
            return UserDataWrapper()
        }

        return gson?.fromJson(file.readText(), UserDataWrapper::class.java) ?: UserDataWrapper()
    }

    fun saveUserData(data: UserDataWrapper) {
        val file = getUserFile()
        println("Data to save: $data")
        file.writeText(gson?.toJson(data) ?: "")
    }


    fun getUserDataByPlayer(player: Player): UserEntry {
        val data = getUserData()
        val user = data.users.find { it.id == player.uniqueId.toString() }
        if (user == null) {
            return UserEntry(player.uniqueId.toString(), player.name, AutoVotes(Time.OFF, Weather.OFF, Requests.OFF))
        }
        return user
    }

    fun saveUser(userData: UserEntry) {
        val file = getUserData()

        // Find the index once
        val index = file.users.indexOfFirst { it.id == userData.id }

        if (index >= 0) {
            println("Updating user: ${userData.id}")
            file.users[index] = userData
        } else {
            println("Creating new user: ${userData.id}")
            file.users.add(userData)
        }

        // Save once for both cases
        saveUserData(file)
    }
}