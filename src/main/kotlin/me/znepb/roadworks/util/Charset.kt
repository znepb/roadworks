package me.znepb.roadworks.util

import com.mojang.serialization.*
import me.znepb.roadworks.RoadworksMain
import org.joml.Vector4d
import java.util.*

enum class Charset(val char: String, val x: Int, val y: Int, val w: Int) {
    A("A", 0, 0, 4),
    B("B", 1, 0, 4),
    C("C", 2, 0, 4),
    D("D", 3, 0, 4),
    E("E", 4, 0, 4),
    F("F", 5, 0, 4),
    G("G", 6, 0, 5),
    H("H", 7, 0, 4),
    I("I", 8, 0, 3),
    J("J", 9, 0, 5),
    K("K", 10, 0, 4),
    L("L", 11, 0, 4),
    M("M", 12, 0, 7),
    N("N", 13, 0, 5),
    O("O", 14, 0, 4),
    P("P", 15, 0, 4),
    Q("Q", 0, 1, 5),
    R("R", 1, 1, 4),
    S("S", 2, 1, 4),
    T("T", 3, 1, 5),
    U("U", 4, 1, 4),
    V("V", 5, 1, 5),
    W("W", 6, 1, 7),
    X("X", 7, 1, 5),
    Y("Y", 8, 1, 5),
    Z("Z", 9, 1, 5),
    ONE("1", 10, 1, 3),
    TWO("2", 11, 1, 4),
    THREE("3", 12, 1, 4),
    FOUR("4", 13, 1, 4),
    FIVE("5", 14, 1, 4),
    SIX("6", 15, 1, 4),
    SEVEN("7", 0, 2, 4),
    EIGHT("8", 1, 2, 4),
    NINE("9", 2, 2, 4),
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
    SPACE(" ", 15, 3, 3);

    fun getUV(): Vector4d {
        return Vector4d(
            (this.x * 16).toDouble(),
            (this.y * 16).toDouble(),
            (this.x * 16 + this.w).toDouble(),
            (this.y * 16 + 15).toDouble()
        )
    }

    override fun toString(): String = this.char

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

        fun fromStr(str: String): Charset? {
            val valid = Charset.entries.filter { it.char.uppercase() == str.uppercase() }
            return if(valid.isEmpty()) null else valid[0]
        }

        fun fromLongString(string: String): List<Charset> {
            var isCode = false
            var currentString = ""
            val contents = mutableListOf<Charset>()

            string.forEach { char ->
                val str = char.toString().uppercase(Locale.getDefault())
                if(isCode) {
                    currentString += str
                    if(str == "]") {
                        isCode = false
                        Charset.fromStr(currentString)?.let { contents.add(it) }
                    }
                } else if(str == "[") {
                    isCode = true
                    currentString = str
                } else {
                    Charset.fromStr(str)?.let { contents.add(it) }
                }
            }

            return contents
        }
    }

}