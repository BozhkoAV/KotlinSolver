import org.junit.jupiter.api.Test
import java.io.File

class SolverTests {
    @Test
    fun main() {
        val list = mutableListOf<Float>()
        val list2 = mutableListOf<String>()
        for (j in 0..9) {
            val outputStream = File("result/out.txt").bufferedWriter()
            for (i in 0..9) {
                val solver = Solver(10, 10, 10)
                outputStream.write(solver.gameResult)
                outputStream.newLine()
            }
            outputStream.close()
            var win = 0
            for (line in File("result/out.txt").readLines()) {
                if (line == "Победа") win++
            }
            list2.add("$win - побед, ${10 - win} - поражений")
            list.add(win / 10F)
        }
        val outputStream2 = File("result/final.txt").bufferedWriter()
        for (element in list2) {
            outputStream2.write(element)
            outputStream2.newLine()
        }
        outputStream2.write("${(list.sum() / 10) * 100} - процент побед")
        outputStream2.close()
    }
}