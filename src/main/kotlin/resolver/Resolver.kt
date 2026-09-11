package resolver

import filter.PlayerFilter
import model.Player
import model.Position
import model.Team

class Resolver(
    private val players: List<Player>,
) : IResolver {
    init {
        players
            .groupBy { it.team }
            .forEach { (team, teamPlayers) -> team.attachPlayers(teamPlayers) }
    }

    override fun getCountWithoutAgency(): Int =
        players.count(PlayerFilter.WithoutAgency::matches)

    override fun getBestScorerDefender(): Pair<String, Int> {
        val defenderFilter = PlayerFilter.ByPosition(Position.DEFENDER)
        val player = players
            .filter(defenderFilter::matches)
            .maxBy { it.goals }

        return player.name to player.goals
    }

    override fun getTheExpensiveGermanPlayerPosition(): String {
        val germanFilter = PlayerFilter.ByNationality("Germany")
        val player = players
            .filter(germanFilter::matches)
            .maxBy { it.transferCost }

        return player.position.positionName
    }

    override fun getTheRudestTeam(): Team =
        players
            .map { it.team }
            .distinct()
            .maxBy { it.averageRedCards }

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
