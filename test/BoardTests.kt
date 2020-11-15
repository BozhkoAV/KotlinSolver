import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class BoardTests {
    @Test
    fun getSize() {
        val board = Board(3,4,3)
        assertEquals(4 to 3, board.getSize())
    }

    @Test
    fun getCoordinatesAround() {
        val board = Board(3,4)
        println(board.getCoordinatesAround(0 to 0))
    }

    @Test
    fun print() {
        val board = Board(3,4,3)
        board.start()
        board.print()
    }
}