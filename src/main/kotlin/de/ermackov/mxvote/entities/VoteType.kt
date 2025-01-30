package de.ermackov.mxvote.entities

enum class VoteType {
    NONE,
    DAY,
    NIGHT,
    SUNNY
}

fun formatVoteType(type: VoteType): String {
    return when (type) {
        VoteType.NONE -> ""
        VoteType.DAY -> "Day"
        VoteType.NIGHT -> "Night"
        VoteType.SUNNY -> "Sunny weather"
    }
}