import model.Position
import parser.CsvParser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CsvParserTest {
    @Test
    fun `empty agency is mapped to null and player is kept`() {
        val result = CsvParser.parse(
            listOf(
                HEADER,
                "Alex Smith;Nevada whales;South Carolina;FORWARD;Germany;;1000000;10;5;3;1;0",
            ),
        )

        assertEquals(1, result.players.size)
        assertEquals(0, result.skippedCount)
        assertNull(result.players.single().agency)
    }

    @Test
    fun `position ignores case and extra spaces`() {
        val result = CsvParser.parse(
            listOf(
                HEADER,
                "Alex Smith;Nevada whales;South Carolina; defender ;Germany;Best Agency;1000000;10;5;3;1;0",
            ),
        )

        assertEquals(Position.DEFENDER, result.players.single().position)
    }

    @Test
    fun `text fields are trimmed`() {
        val result = CsvParser.parse(
            listOf(
                HEADER,
                " Alex Smith ; Nevada whales ; South Carolina ;MIDFIELD; Germany ; Best Agency ;1000000;10;5;3;1;0",
            ),
        )

        val player = result.players.single()
        assertEquals("Alex Smith", player.name)
        assertEquals("Nevada whales", player.team.name)
        assertEquals("South Carolina", player.team.city)
        assertEquals("Germany", player.nationality)
        assertEquals("Best Agency", player.agency)
    }

    @Test
    fun `broken rows are skipped and counted`() {
        val result = CsvParser.parse(
            listOf(
                HEADER,
                "Alex Smith;Nevada whales;South Carolina;FORWARD;Germany;Best Agency;1000000;10;5;3;1;0",
                "No Team;;South Carolina;FORWARD;Germany;Best Agency;1000000;10;5;3;1;0",
                "No Position;Nevada whales;South Carolina;;Germany;Best Agency;1000000;10;5;3;1;0",
                "Bad Cost;Nevada whales;South Carolina;FORWARD;Germany;Best Agency;bad;10;5;3;1;0",
                "Bad Goals;Nevada whales;South Carolina;FORWARD;Germany;Best Agency;1000000;10;;3;1;0",
            ),
        )

        assertEquals(1, result.players.size)
        assertEquals(4, result.skippedCount)
    }

    private companion object {
        const val HEADER = "Name;Team;City;Position;Nationality;Agency;Transfer cost;Participations;Goals;Assists;Yellow cards;Red cards"
    }
}
