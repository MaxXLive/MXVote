package de.ermackov.mxvote.util

import org.bukkit.entity.Player

object Format {

    fun formatPlayerNames(players: List<Player>): String {
        if (players.isEmpty()) return "No players"

        val names = players.map { it.name }

        return when (names.size) {
            1 -> names[0]
            2 -> "${names[0]} and ${names[1]}"
            else -> {
                val first = names[0]
                val second = names[1]
                val remaining = names.size - 2
                "$first, $second and $remaining more"
            }
        }
    }
}

