enum class Cell {
    zero,
    num1,
    num2,
    num3,
    num4,
    num5,
    num6,
    bomb,
    opened,
    closed,
    flaged,
    bombed,
    nobomb,
    empty;

    fun setNextNumberCell () = values() [this.ordinal + 1]
}