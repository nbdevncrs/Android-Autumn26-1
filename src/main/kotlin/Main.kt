import parser.CsvParser
import visualization.calculateCountryShares
import visualization.calculateTopTeamCostShares
import visualization.saveCountryShareChart
import visualization.saveTeamCostCumulativeShareChart
import java.io.File

fun main() {
    val lines = requireNotNull(Thread.currentThread().contextClassLoader.getResourceAsStream("fakePlayersDirty.csv")) {
        "Resource fakePlayersDirty.csv was not found"
    }.bufferedReader().use { it.readLines() }

    val parseResult = CsvParser.parse(lines)
    val chartsDir = File("charts")

    saveCountryShareChart(
        countryShares = calculateCountryShares(parseResult.players),
        file = chartsDir.resolve("country-share.png"),
    )
    saveTeamCostCumulativeShareChart(
        teamCostShares = calculateTopTeamCostShares(parseResult.players),
        file = chartsDir.resolve("team-cost-cumulative-share.png"),
    )

    println("Parsed players: ${parseResult.players.size}, skipped rows: ${parseResult.skippedCount}")
    println("Charts saved to ${chartsDir.absolutePath}")
}
