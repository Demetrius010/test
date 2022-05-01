package com.bignerdranch.android.daggertest

import javax.inject.Inject

data class Computer (
    val processor: Processor,
    val motherboard: Motherboard,
    val ram: RAM)

class Processor{
    override fun toString() = "Processor 123"
}

class Motherboard{
    override fun toString() = "Motherboard 46"
}


class RAM @Inject constructor(){
    override fun toString() = "256 ram"
}