package de.ermackov.mxvote.entities

import org.bukkit.entity.Player

data class Vote(val player: Player, val isInitiator: Boolean, val voteYes: Boolean)