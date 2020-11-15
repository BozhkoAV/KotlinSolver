class Board {
    private var board = arrayOf<Array<Array<Cell>>>()
    private var totalBombs = 0
    private lateinit var boardSize: Pair<Int, Int>
    private var numberOfClosedCells = 0
    private val allCoordinates: MutableList<Pair<Int, Int>> = mutableListOf()

    private fun setSize(size: Pair<Int, Int>) {
        boardSize = size
        for (x in 0 until boardSize.first) {
            for (y in 0 until boardSize.second) {
                allCoordinates.add(Pair(x, y))
            }
        }
        board = Array(boardSize.first) { Array(boardSize.second) {Array(2) {Cell.empty} } }
    }

    private fun setNumberOfClosedCells(size: Pair<Int, Int>) {
        numberOfClosedCells = size.first * size.second
    }

    private fun setTotalBombs(_totalBombs: Int) {
        totalBombs = _totalBombs
        val maxBombs: Int = getSize().first * getSize().second
        if (totalBombs > maxBombs) totalBombs = maxBombs / 3
    }

    constructor(cols: Int, rows: Int, bombs: Int) {
        setSize(rows to cols)
        setNumberOfClosedCells(rows to cols)
        setTotalBombs(bombs)
    }

    constructor(cols: Int, rows: Int) {
        setSize(rows to cols)
        setNumberOfClosedCells(rows to cols)
    }

    fun getSize() = boardSize

    fun getAllCoordinates() = allCoordinates

    fun getNumberOfClosedCells() = numberOfClosedCells

    fun setBoard(defaultCell: Cell, layNumber: Int) {
        for (coordinate in getAllCoordinates()) board [coordinate.first] [coordinate.second] [layNumber] = defaultCell
    }

    private fun isBelongBoard(coordinate: Pair<Int, Int>) =
        coordinate.first >= 0 && coordinate.first < boardSize.first &&
                coordinate.second >= 0 && coordinate.second < boardSize.second

    fun get(coordinate: Pair<Int, Int>, layNumber: Int): Cell? {
        if (isBelongBoard(coordinate)) return board [coordinate.first] [coordinate.second] [layNumber]
        return null
    }

    fun set(coordinate: Pair<Int, Int>, layNumber: Int, cell: Cell) {
        if (isBelongBoard(coordinate)) board [coordinate.first] [coordinate.second] [layNumber] = cell
    }

    fun getTotalBombs(): Int = totalBombs

    fun getCurrentNumberOfClosed(): Int {
        var currentNumberOfClosed = 0
        for (x in 0 until boardSize.first) {
            for (y in 0 until boardSize.second) {
                if (board [x] [y] [0] == Cell.closed) currentNumberOfClosed++
            }
        }
        return currentNumberOfClosed
    }

    private fun getRandomCoordinate() = (0 until boardSize.first).random() to (0 until boardSize.second).random()

    private fun placeBomb() {
        var coordinate = getRandomCoordinate()
        while (get(coordinate, 1) == Cell.bomb) {
            coordinate = getRandomCoordinate()
        }
        set(coordinate, 1, Cell.bomb)
        setNumbersAroundBomb(coordinate)
    }

    private fun setNumbersAroundBomb(coordinate: Pair<Int, Int>) {
        for (around in getCoordinatesAround(coordinate)) {
            if (get(around, 1) != Cell.bomb) {
                set(around, 1, get(around, 1)!!.setNextNumberCell())
            }
        }
    }

    fun getCoordinatesAround(coordinate: Pair<Int, Int>): MutableList<Pair<Int, Int>> {
        var around: Pair<Int, Int>
        val list = mutableListOf<Pair<Int, Int>>()
        var y = coordinate.second
        for (x in coordinate.first - 2..coordinate.first + 2) {
            around = Pair(x, y)
            if ((isBelongBoard(around)) && (around != coordinate)) list.add(around)
        }
        y = if (coordinate.first % 2 == 0) y - 1 else y + 1
        for (x in coordinate.first - 1..coordinate.first + 1 step 2) {
            around = Pair(x, y)
            if (isBelongBoard(around)) list.add(around)
        }
        return list
    }

    fun setOpenedToCell(coordinate: Pair<Int, Int>) {
        set(coordinate, 0, Cell.opened)
        numberOfClosedCells--
    }

    // при желании убрать
    fun setFlagedToCell(coordinate: Pair<Int, Int>) {
        when(get(coordinate, 0)) {
            Cell.flaged -> set(coordinate, 0, Cell.closed)
            Cell.closed -> set(coordinate, 0, Cell.flaged)
            else -> return
        }
    }

    fun setOpenedToClosedCell(coordinate: Pair<Int, Int>) {
        if (get(coordinate,0) == Cell.closed) set(coordinate,0, Cell.opened)
    }

    // при желании убрать
    fun setNobombToFlagedCell(coordinate: Pair<Int, Int>) {
        if (get(coordinate,0) == Cell.flaged) set(coordinate,0, Cell.nobomb)
    }

    fun start() {
        setBoard(Cell.closed, 0)
        setBoard(Cell.zero, 1)
        for (i in 0 until totalBombs) placeBomb()
    }

    ////////////////////// для тестов ////////////////////////////////////////
    fun setBoard(lay: Array<Array<Cell>>, layNumber: Int) {
        for (coordinate in getAllCoordinates()) {
            board [coordinate.first] [coordinate.second] [layNumber] = lay [coordinate.first] [coordinate.second]
        }
    }

    ////////////////////// для тестов ////////////////////////////////////////
    fun getBoard(layNumber: Int): Array<Array<Cell>> {
        val lay = Array(boardSize.first) { Array(boardSize.second) {Cell.empty} }
        for (coordinate in getAllCoordinates()) {
            lay [coordinate.first] [coordinate.second] = board [coordinate.first] [coordinate.second] [layNumber]
        }
        return lay
    }

    ////////////////////// для тестов ////////////////////////////////////////
    fun getStateOfCoordinatesAround(coordinatesAround: MutableList<Pair<Int, Int>>): MutableMap<Pair<Int, Int>, Cell> {
        val map = mutableMapOf<Pair<Int, Int>, Cell>()
        for (current in coordinatesAround) {
            if ((board [current.first] [current.second] [1] != Cell.empty) &&
                (board [current.first] [current.second] [1] != Cell.zero))
                map[current] = board [current.first] [current.second] [1]
        }
        return map
    }

    ////////////////////// для тестов ////////////////////////////////////////
    fun cellOpener(gameBoard: Board) {
        var gameBoardDownLay: Array<Array<Cell>>
        for (coordinate in getAllCoordinates()) {
            gameBoardDownLay = gameBoard.getBoard(1)
            if (board [coordinate.first] [coordinate.second] [0] == Cell.opened)
                board [coordinate.first] [coordinate.second] [1] =
                    gameBoardDownLay [coordinate.first] [coordinate.second]
        }
    }

    ////////////////////// для тестов ////////////////////////////////////////
    fun getNumberOfClosedAround(coordinate: Pair<Int, Int>): Int {
        var numberOfClosedAround = 0
        for (current in getCoordinatesAround(coordinate)) {
            if (board [current.first] [current.second] [0] == Cell.closed) numberOfClosedAround++
        }
        return numberOfClosedAround
    }

    ////////////////////// для тестов ////////////////////////////////////////
    fun isBombed(): Boolean {
        for (x in 0 until getSize().first) {
            for (y in 0 until getSize().second) {
                if (board [x] [y] [0] == Cell.bombed) return true
            }
        }
        return false
    }

    ////////////////////// для тестов ////////////////////////////////////////
    fun print() {
        val x = getSize().first
        val y = getSize().second

        val str = Array(x) { Array(y) {Array(2) {""} } }

        for (coordinate in getAllCoordinates()) {
            str [coordinate.first] [coordinate.second] [0] = get(coordinate, 0)!!.name
            str [coordinate.first] [coordinate.second] [1] = get(coordinate, 1)!!.name
        }

        for (i in 0 until x) {
            for (j in 0 until y) {
                if (i % 2 != 0 && j == 0) print("     ")
                print(str [i] [j] [0] + "     ")
            }
            println()
        }
        println()
        for (i in 0 until x) {
            for (j in 0 until y) {
                if (i % 2 != 0 && j == 0) print("     ")
                print(str [i] [j] [1] + "     ")
            }
            println()
        }
    }
}