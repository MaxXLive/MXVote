package de.ermackov.mxvote.entities

data class UserDataWrapper(
    val users: MutableList<UserEntry> = mutableListOf()
)

sealed interface VoteType

enum class Time : VoteType { DAY, NIGHT, OFF }
enum class Weather : VoteType { SUNNY, RAIN, THUNDER, OFF }
enum class Requests : VoteType { YES, NO, OFF }

data class UserEntry(
    val id: String,
    val userName: String,
    val autovotes: AutoVotes
)

data class AutoVotes(
    val time: Time,
    val weather: Weather,
    val requests: Requests
)

fun formatVoteType(type: VoteType): String {
    return when (type) {
        Time.DAY -> "Day"
        Time.NIGHT -> "Night"
        Weather.SUNNY -> "Sunny weather"
        else -> "Off"
    }
}
