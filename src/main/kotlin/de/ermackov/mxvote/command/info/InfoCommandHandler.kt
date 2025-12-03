package de.ermackov.mxvote.command.info

import com.google.gson.JsonParser
import com.vdurmont.semver4j.Semver
import de.ermackov.mxvote.config.VoteConfig
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption


class InfoCommandHandler(private val plugin: JavaPlugin, config: VoteConfig): CommandExecutor {

    private val prefix: String = ChatColor.translateAlternateColorCodes('&', config.getMessagePrefix())

    override fun onCommand(sender: CommandSender, command: Command, arg: String, args: Array<out String>): Boolean {
        if(!sender.hasPermission("mxvote.test.version")){
            sender.sendMessage("$prefix${ChatColor.RED}You don't have permission to use this command!")
            return true
        }

        when (args[0]) {
            "version", "v" -> handleVersion(sender)
            "update", "u" -> handleUpdate(sender)
            "reload", "r" -> handleReload(sender)
            else -> sender.sendMessage("$prefix${ChatColor.RED}Invalid argument: ${args[0]}")
        }
        return true
    }

    fun getLatestVersion(): String {
        val url = URL("https://api.github.com/repos/maxxlive/mxvote/releases/latest")
        val response = url.readText()
        val jsonElement = JsonParser.parseString(response)
        return jsonElement.asJsonObject.get("tag_name").asString.replace("v".toRegex(), "")
    }

    private fun downloadUpdate(latestVersion: String) {
        val pluginFilePath = plugin.javaClass.protectionDomain.codeSource.location.toURI().path
        val split = pluginFilePath.split("/")
        val fileName = split[split.size - 1]
        val isPaperServer = split[split.size - 2] == ".paper-remapped"
        println("Is paper server: $isPaperServer")
        val actualPath = split.subList(0, split.size - 2).joinToString("/") + "/" + fileName
        val usePath = if (isPaperServer) actualPath else pluginFilePath
        println("Using path: $usePath")

        val url = URL("https://github.com/maxxlive/mxvote/releases/download/v$latestVersion/mxvote-$latestVersion.jar")
        url.openStream().use { `in` ->
            Files.copy(`in`, Paths.get(usePath), StandardCopyOption.REPLACE_EXISTING)
        }
    }

    private fun handleUpdate(sender: CommandSender) {
        sender.sendMessage("$prefix${ChatColor.YELLOW}Checking for updates...")
        val latestVersion = getLatestVersion()
        var yourVersion = plugin.description.version
        yourVersion = "1.0"  // For testing purposes
        sender.sendMessage("$prefix${ChatColor.YELLOW}Your version: ${yourVersion}, Latest version: $latestVersion")

        val currentVersionSem = Semver(yourVersion, Semver.SemverType.NPM)
        val latestVersionSem = Semver(latestVersion, Semver.SemverType.NPM)

        if (currentVersionSem == latestVersionSem) {
            sender.sendMessage("$prefix${ChatColor.GREEN}You are already running the latest version!")
            return
        }

        sender.sendMessage("$prefix${ChatColor.GREEN}Update available! Downloading version $latestVersion...")
        downloadUpdate(latestVersion)
        sender.sendMessage("$prefix${ChatColor.GREEN}Download complete! Please restart the server to apply the update.")
    }

    private fun handleVersion(sender: CommandSender) {
        sender.sendMessage("You are running MXVote version ${plugin.description.version}")
    }

    private fun handleReload(sender: CommandSender) {
        plugin.saveDefaultConfig() // Only creates the file if it doesn't exist
        plugin.reloadConfig()
    }
}