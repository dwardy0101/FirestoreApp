package com.example.firestore.ui.adapter

import kotlin.math.min
import kotlin.random.Random

fun main() {


    val array = arrayOf(3, 4)

    repeat(20) {

        var random = Random.nextInt(array.size) + 1
        random = min(random, array.size - 1)

        println("Random # ${array[random]}")
    }
}