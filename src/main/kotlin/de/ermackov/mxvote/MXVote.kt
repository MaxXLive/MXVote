package de.ermackov.mxvote

import de.ermackov.mxvote.command.Test
import de.ermackov.mxvote.command.Vote
import de.ermackov.mxvote.command.VoteTabCompleter
import de.ermackov.mxvote.config.VoteConfig
import org.bukkit.plugin.java.JavaPlugin

class MXVote : JavaPlugin() {

    private lateinit var vote: Vote
    private lateinit var config: VoteConfig

    override fun onEnable() {
        logger.info("MXVote Enabled")
        config = VoteConfig(this)
        vote = Vote(this, config)

        getCommand("mxvote")?.setExecutor(Test(this, config))
        getCommand("vote")?.setExecutor(vote)
        getCommand("vote")?.tabCompleter = VoteTabCompleter()
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
