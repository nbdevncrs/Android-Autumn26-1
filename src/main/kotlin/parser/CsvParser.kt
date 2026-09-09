package parser

import model.Player
import model.Position
import model.Team

object CsvParser {
    fun parse(lines: Sequence<String>): ParseResult {
        val players = mutableListOf<Player>()
        var skippedCount = 0

        lines.drop(1).forEach { line ->
            val player = parsePlayerOrNull(line)

            if (player == null) {
                skippedCount++
            } else {
                players += player
            }
        }

        return ParseResult(players, skippedCount)
    }

    fun parse(lines: Iterable<String>): ParseResult = parse(lines.asSequence())

    private fun parsePlayerOrNull(line: String): Player? {
        val columns = line.split(';').map { it.trim() }
        if (columns.size != COLUMN_COUNT) return null

        val name = columns[0].takeIf { it.isNotEmpty() } ?: return null
        val teamName = columns[1].takeIf { it.isNotEmpty() } ?: return null
        val city = columns[2].takeIf { it.isNotEmpty() } ?: return null
        val position = columns[3].takeIf { it.isNotEmpty() }
            ?.let(Position::fromCsvTokenOrNull)
            ?: return null
        val nationality = columns[4].takeIf { it.isNotEmpty() } ?: return null
        val agency = columns[5].takeIf { it.isNotEmpty() }
        val transferCost = columns[6].toLongOrNull() ?: return null
        val participations = columns[7].toIntOrNull() ?: return null
        val goals = columns[8].toIntOrNull() ?: return null
        val assists = columns[9].toIntOrNull() ?: return null
        val yellowCards = columns[10].toIntOrNull() ?: return null
        val redCards = columns[11].toIntOrNull() ?: return null

        return Player(
            name = name,
            team = Team(teamName, city),
            position = position,
            nationality = nationality,
            agency = agency,
            transferCost = transferCost,
            participations = participations,
            goals = goals,
            assists = assists,
            yellowCards = yellowCards,
            redCards = redCards,
        )
    }

    private const val COLUMN_COUNT = 12
}
