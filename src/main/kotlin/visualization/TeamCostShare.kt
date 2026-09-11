package visualization

import model.Player
import model.Team

data class TeamCostShare(
    val team: Team,
    val totalCost: Long,
    val cumulativeShare: Double,
)

fun calculateTopTeamCostShares(players: List<Player>, limit: Int = 10): List<TeamCostShare> {
    val totalCost = players.sumOf { it.transferCost }
    if (totalCost == 0L || limit <= 0) return emptyList()

    var cumulativeCost = 0L

    return players
        .map { it.team }
        .distinct()
        .sortedWith(compareByDescending<Team> { it.totalTransferCost }.thenBy { it.name }.thenBy { it.city })
        .take(limit)
        .map { team ->
            cumulativeCost += team.totalTransferCost
            TeamCostShare(
                team = team,
                totalCost = team.totalTransferCost,
                cumulativeShare = cumulativeCost.toDouble() / totalCost,
            )
        }
}
