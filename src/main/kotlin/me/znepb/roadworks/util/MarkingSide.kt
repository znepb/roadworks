package me.znepb.roadworks.util

enum class Side {
    RIGHT, LEFT;

    fun opposite(): Side {
        return if(this == RIGHT) LEFT else RIGHT
    }
}