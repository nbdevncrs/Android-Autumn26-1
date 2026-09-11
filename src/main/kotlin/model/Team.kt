package model

data class Team(
    val name: String,
    val city: String,
) {
    private var roster: List<Player>? = null

    val players: List<Player>
        get() = roster ?: error("Players are not attached to team $name")

    val totalTransferCost: Long by lazy {
        players.sumOf { it.transferCost }
    }

    val averageRedCards: Double by lazy {
        players.map { it.redCards }.average()
    }

    internal fun attachPlayers(players: List<Player>) {
        roster?.let { attachedPlayers ->
            check(attachedPlayers == players) { "Different players are already attached to team $name" }
        }

        roster = players
    }
}
