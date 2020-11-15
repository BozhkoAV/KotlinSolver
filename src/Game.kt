class Game {
    var gameBoard : Board
    private lateinit var state: GameState

    constructor(cols: Int, rows: Int, bombs: Int) {
        gameBoard = Board(cols, rows, bombs)
    }

    fun isContinue() = state == GameState.PLAYED

    fun start() {
        gameBoard.start()
        state = GameState.PLAYED
    }

    fun openCellsAround(coordinate: Pair<Int, Int>) {
        gameBoard.setOpenedToCell(coordinate)
        for (around in gameBoard.getCoordinatesAround(coordinate)) openCell(around)
    }

    fun openBombs(bombed: Pair<Int, Int>) {
        state = GameState.BOMBED
        gameBoard.set(bombed,0, Cell.bombed)
        for (coordinate in gameBoard.getAllCoordinates())
            if (gameBoard.get(coordinate, 1) == Cell.bomb) {
                gameBoard.setOpenedToClosedCell(coordinate)
            } else {
                gameBoard.setNobombToFlagedCell(coordinate)
            }
    }

    fun openCell(coordinate: Pair<Int, Int>) {
        when (gameBoard.get(coordinate, 0)) {
            Cell.flaged -> return
            Cell.closed -> when (gameBoard.get(coordinate, 1)) {
                Cell.zero -> openCellsAround (coordinate)
                Cell.bomb -> openBombs (coordinate)
                else -> gameBoard.setOpenedToCell(coordinate)
            }
        }
    }

    fun checkWinner () {
        if ((state == GameState.PLAYED) && (gameBoard.getNumberOfClosedCells() == gameBoard.getTotalBombs()))
            state = GameState.WINNER
    }

    fun gameOver() = state != GameState.PLAYED

    fun pressLeftButton (coordinate: Pair<Int, Int>) {
        if (gameOver ()) return
        openCell (coordinate)
        checkWinner()
    }

    fun pressRightButton(coordinate: Pair<Int, Int>) {
        if (gameOver ()) return
        gameBoard.setFlagedToCell (coordinate)
    }
}