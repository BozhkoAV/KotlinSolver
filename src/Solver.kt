class Solver {
    private var probabilityMap = mutableMapOf<Pair<Int, Int>, Float>()
    private var game: Game
    var gameResult = "DNP"

    constructor(cols: Int, rows: Int, bombs: Int) {
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                probabilityMap.put(i to j, bombs.toFloat() / (cols * rows).toFloat())
            }
        }

        game = Game(cols, rows, bombs)
        game.start()

        val solveBoard = Board(cols, rows)
        solveBoard.setBoard(Cell.closed, 0)
        solveBoard.setBoard(Cell.empty, 1)
        while (game.isContinue()) {
            game.pressLeftButton(listOfMinimalProbability(probabilityMap).random())
            solveBoard.setBoard(game.gameBoard.getBoard(0), 0)
            solveBoard.cellOpener(game.gameBoard)
            //solveBoard.print()
            if (solveBoard.isBombed()) {
                //println("Поражение")
                gameResult = "Поражение"
                break
            }
            probabilityMap = probabilityRecount(probabilityMap, solveBoard, cols, rows, bombs)
            if (isWin(probabilityMap)) {
                //println("Победа")
                gameResult = "Победа"
                break
            }
        }
    }

    fun probabilityRecount(probabilityMap: MutableMap<Pair<Int, Int>, Float>, solveBoard: Board,
                           cols: Int, rows: Int, bombs: Int): MutableMap<Pair<Int, Int>, Float> {
        val foundBombs = numberOfFoundBombs(probabilityMap)
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                val current = i to j
                if (solveBoard.getBoard(0) [current.first] [current.second] != Cell.opened) {
                    val valueAround = solveBoard.getStateOfCoordinatesAround(solveBoard.getCoordinatesAround(current))
                    if (valueAround.isEmpty()) {
                        if (solveBoard.getNumberOfClosedCells() != 0) {
                            probabilityMap[current] = (bombs - foundBombs).toFloat() /
                                    (solveBoard.getCurrentNumberOfClosed() - foundBombs).toFloat()
                        }
                    } else {
                        val threat = mutableListOf<Pair<Int, Int>>()
                        valueAround.forEach { (coord, value) ->
                            var number = 0
                            if (solveBoard.getNumberOfClosedAround(coord) != 0) {
                                when (value) {
                                    Cell.num1 -> number = 1
                                    Cell.num2 -> number = 2
                                    Cell.num3 -> number = 3
                                    Cell.num4 -> number = 4
                                    Cell.num5 -> number = 5
                                    Cell.num6 -> number = 6
                                }
                            }
                            threat.add(number to solveBoard.getNumberOfClosedAround(coord))
                        }
                        var result = 1.0F
                        for (value in threat) {
                            result *= (1.0F - value.first.toFloat() / value.second.toFloat())
                        }
                        probabilityMap [current] = result * (-1) + 1;
                    }
                } else {
                    probabilityMap [current] = 0.0F
                }
            }
        }
        //probabilityMap.forEach{ (k, v) -> println("$k = $v")}
        return probabilityMap
    }

    fun numberOfFoundBombs(probabilityMap: MutableMap<Pair<Int, Int>, Float>): Int {
        var result = 0
        probabilityMap.forEach{ (_, v) -> if (v==1.0F) result++ }
        return result
    }

    fun listOfMinimalProbability(probabilityMap: MutableMap<Pair<Int, Int>, Float>): MutableList<Pair<Int, Int>> {
        val list = mutableListOf<Pair<Int, Int>>()
        var min = 1.0F
        probabilityMap.forEach{ (_, v) -> if (v < min && v > 0.0F) min = v }
        probabilityMap.forEach{ (k, v) -> if (v == min) list.add(k) }
        return list
    }

    fun isWin(probabilityMap: MutableMap<Pair<Int, Int>, Float>): Boolean {
        probabilityMap.forEach{ (_, v) -> if (v != 1.0F && v != 0.0F) return false}
        return true
    }
}