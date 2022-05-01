package com.bignerdranch.android.daggertest

import dagger.Module
import dagger.Provides

@Module
object AppModule{

    @Provides
    fun provideComputer(processor: Processor,
                        ram: RAM,
                        motherboard: Motherboard):Computer{
        return Computer(
            processor = processor,
            ram = ram,
            motherboard = motherboard
        )
    }

    @Provides
    fun provideProcessor(): Processor{
        return Processor()
    }

//    @Provides инжектим в конструктор поэтому это нам не нужно
//    fun provideRam(): RAM {
//        return RAM()
//    }

    @Provides
    fun provideMotherboard(): Motherboard{
        return Motherboard()
    }
}