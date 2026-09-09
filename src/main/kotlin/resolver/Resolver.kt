package resolver

import model.Player
import model.Position
import model.Team

class Resolver(
    private val players: List<Player>,
) : IResolver {
    override fun getCountWithoutAgency(): Int =
        players.count { it.agency == null }

    override fun getBestScorerDefender(): Pair<String, Int> {
        val player = players
            .filter { it.position == Position.DEFENDER }
            .maxBy { it.goals }

        return player.name to player.goals
    }

    override fun getTheExpensiveGermanPlayerPosition(): String {
        val player = players
            .filter { it.nationality == "Germany" }
            .maxBy { it.transferCost }

        return player.position.positionName
    }

    override fun getTheRudestTeam(): Team =
        players
            .groupBy { it.team }
            .maxBy { (_, teamPlayers) -> teamPlayers.map { it.redCards }.average() }
            .key

    override fun getAverageTransferCostByPosition(): Map<Position, Double> =
        players
            .groupBy { it.position }
            .mapValues { (_, positionPlayers) -> positionPlayers.map { it.transferCost }.average() }
            .toList()
            .sortedByDescending { (_, averageCost) -> averageCost }
            .toMap()

    override fun getMostValuablePlayers(): List<Player> =
        players
            .sortedWith(
                compareByDescending<Player> { it.goals + 2 * it.assists }
                    .thenBy { it.name },
            )
            .take(3)

    override fun getMostPopularAgencyByCountry(): Map<String, String> =
        players
            .filter { it.agency != null }
            .groupBy { it.nationality }
            .mapValues { (_, countryPlayers) ->
                countryPlayers
                    .groupingBy { it.agency.orEmpty() }
                    .eachCount()
                    .toList()
                    .sortedWith(
                        compareByDescending<Pair<String, Int>> { it.second }
                            .thenBy { it.first },
                    )
                    .first()
                    .first
            }

    override fun getGoalsShareByMedianCost(): Pair<Double, Double> {
        val sortedCosts = players.map { it.transferCost }.sorted()
        val median = if (sortedCosts.size % 2 == 0) {
            (sortedCosts[sortedCosts.size / 2 - 1] + sortedCosts[sortedCosts.size / 2]) / 2.0
        } else {
            sortedCosts[sortedCosts.size / 2].toDouble()
        }

        val cheaperGoals = players
            .filter { it.transferCost <= median }
            .sumOf { it.goals }
        val expensiveGoals = players
            .filter { it.transferCost > median }
            .sumOf { it.goals }
        val totalGoals = cheaperGoals + expensiveGoals

        return cheaperGoals.toDouble() / totalGoals to expensiveGoals.toDouble() / totalGoals
    }
}
