package de.ermackov.mxvote.config

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin

class VoteConfig(private val plugin: JavaPlugin) {

    private val config: FileConfiguration = plugin.config

    init {
        // Ensure the config file is loaded and contains defaults
        plugin.saveDefaultConfig()
    }

    // Check if the voting is enabled
    fun isVoteEnabled(): Boolean {
        return config.getBoolean("vote.enabled", true)  // Default to true if not set
    }

    // Get the vote duration in seconds
    fun getVoteDuration(): Int {
        return config.getInt("vote.duration", 30)  // Default to 30 seconds if not set
    }

    fun getMessagePrefix(): String {
        return config.getString("vote.message_prefix", "&6[MXVote] &f") ?: "&6[MXVote] &f"
    }

    fun getTimeChangeStepSize(): Int {
        return config.getInt("vote.time_change_step_size", 500)
    }
}
