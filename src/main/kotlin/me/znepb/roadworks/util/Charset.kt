package me.znepb.roadworks.util

import com.mojang.serialization.Codec
import me.znepb.roadworks.RoadworksMain
import me.znepb.roadworks.RoadworksMain.logger
import org.joml.Vector4d
import java.util.*

enum class Charset(val symbol: String, val x: Int, val y: Int, val w: Int) {
    A("A", 0, 0, 5),
    B("B", 1, 0, 5),
    C("C", 2, 0, 5),
    D("D", 3, 0, 5),
    E("E", 4, 0, 5),
    F("F", 5, 0, 5),
    G("G", 6, 0, 5),
    H("H", 7, 0, 5),
    I("I", 8, 0, 3),
    J("J", 9, 0, 5),
    K("K", 10, 0, 5),
    L("L", 11, 0, 5),
    M("M", 12, 0, 5),
    N("N", 13, 0, 5),
    O("O", 14, 0, 5),
    P("P", 15, 0, 5),
    Q("Q", 0, 1, 5),
    R("R", 1, 1, 5),
    S("S", 2, 1, 5),
    T("T", 3, 1, 5),
    U("U", 4, 1, 5),
    V("V", 5, 1, 5),
    W("W", 6, 1, 5),
    X("X", 7, 1, 5),
    Y("Y", 8, 1, 5),
    Z("Z", 9, 1, 5),
    ONE("1", 10, 1, 4),
    TWO("2", 11, 1, 5),
    THREE("3", 12, 1, 5),
    FOUR("4", 13, 1, 5),
    FIVE("5", 14, 1, 5),
    SIX("6", 15, 1, 5),
    SEVEN("7", 0, 2, 5),
    EIGHT("8", 1, 2, 5),
    NINE("9", 2, 2, 5),
    ZERO("0", 3, 2, 5),
    ROAD("[ROAD]", 4, 2, 7),
    STREET("[STREET]", 5, 2, 6),
    AVE("[AVENUE]", 6, 2, 11),
    BLVD("[BOULEVARD]", 8, 2, 14),
    WAY("[WAY]", 10,2, 11),
    DR("[DRIVE]", 12, 2, 7),
    PK("[PIKE]", 13, 2, 7),
    HWY("[HIGHWAY]", 14, 2, 11),
    ARROW_RIGHT("[ARROW_RIGHT]", 0, 3, 7),
    ARROW_LEFT("[ARROW_LEFT]", 1, 3, 7),
    ARROW_UP("[ARROW_UP]", 2, 3, 5),
    ARROW_DOWN("[ARROW_DOWN]", 3, 3, 5),
    FORBIDDEN("[FORBIDDEN]", 4, 3, 7),
    WARNING("[WARNING]", 5, 3, 7),
    ST("[ST]", 5, 2, 6),
    ND("[ND]", 7, 3, 7),
    RD("[RD]", 4, 2, 7),
    TH("[TH]", 8, 3, 7),
    PERIOD(".", 9, 3, 1),
    DASH("-", 10, 3, 3),
    NORTH("[NORTH]", 11, 3, 4),
    EAST("[EAST]", 12, 3, 3),
    SOUTH("[SOUTH]", 13, 3, 2),
    WEST("[WEST]", 14, 3, 5),
    SMALL_SPACE("[SMALL_SPACE]", 15, 3, 1),
    SPACE(" ", 15, 3, 3),
    LOWERCASE_A("a", 0, 4, 5),
    LOWERCASE_B("b", 1, 4, 5),
    LOWERCASE_C("c", 2, 4, 5),
    LOWERCASE_D("d", 3, 4, 5),
    LOWERCASE_E("e", 4, 4, 5),
    LOWERCASE_F("f", 5, 4, 4),
    LOWERCASE_G("g", 6, 4, 5),
    LOWERCASE_H("h", 7, 4, 5),
    LOWERCASE_I("i", 8, 4, 1),
    LOWERCASE_J("j", 9, 4, 4),
    LOWERCASE_K("k", 10, 4, 4),
    LOWERCASE_L("l", 11, 4, 2),
    LOWERCASE_M("m", 12, 4, 5),
    LOWERCASE_N("n", 13, 4, 5),
    LOWERCASE_O("o", 14, 4, 5),
    LOWERCASE_P("p", 15, 4, 5),
    LOWERCASE_Q("q", 0, 5, 5),
    LOWERCASE_R("r", 1, 5, 5),
    LOWERCASE_S("s", 2, 5, 4),
    LOWERCASE_T("t", 3, 5, 3),
    LOWERCASE_U("u", 4, 5, 5),
    LOWERCASE_V("v", 5, 5, 5),
    LOWERCASE_W("w", 6, 5, 5),
    LOWERCASE_X("x", 7, 5, 5),
    LOWERCASE_Y("y", 8, 5, 5),
    LOWERCASE_Z("z", 9, 5, 5),
    ARROW_UR("[ARROW_UR]", 10, 5, 8),
    ARROW_UL("[ARROW_UL]", 11, 5, 8),
    ARROW_DR("[ARROW_DR]", 12, 5, 8),
    ARROW_DL("[ARROW_DL]", 13, 5, 8);

    fun getUV(): Vector4d {
        return Vector4d(
            (this.x * 16).toDouble(),
            (this.y * 16).toDouble(),
            (this.x * 16 + this.w).toDouble(),
            (this.y * 16 + 15).toDouble()
        )
    }

    override fun toString(): String = this.symbol

    fun toInt() = this.ordinal

    companion object {
        const val CHARSET_WIDTH = 128
        const val CHARSET_HEIGHT = 128
        val TEXTURE = RoadworksMain.ModId("textures/block/signs/charset.png")
        val ARRAY_CODEC: Codec<List<Charset>> = Codec.INT.listOf().xmap(Charset::fromArray, Charset::toArray)

        fun fromInt(int: Int) = Charset.entries[int]

        fun fromArray(ints: List<Int>) : List<Charset> {
            return ints.map {
                this.fromInt(it)
            }
        }

        fun toArray(charset: List<Charset>) : List<Int> {
            return charset.map {
                it.toInt()
            }
        }

        fun fromSymbol(str: String): Charset? {
            val valid = Charset.entries.filter { it.symbol == str }
            return if(valid.isEmpty()) null else valid[0]
        }

        fun fromLongString(string: String): List<Charset> {
            var isCode = false
            var currentString = ""
            val contents = mutableListOf<Charset>()

            string.forEach { char ->
                val str = char.toString()
                if(isCode) {
                    currentString += str
                    if(str == "]") {
                        isCode = false
                        fromSymbol(currentString.uppercase())?.let { contents.add(it) }
                    }
                } else if(str == "[") {
                    isCode = true
                    currentString = str
                } else {
                    fromSymbol(str)?.let { contents.add(it) }
                }
            }

            return contents
        }
    }

}