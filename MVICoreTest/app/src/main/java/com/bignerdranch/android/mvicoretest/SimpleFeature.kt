package com.bignerdranch.android.mvicoretest

import com.badoo.mvicore.element.Reducer
import com.badoo.mvicore.feature.ReducerFeature

class SimpleFeature : ReducerFeature<SimpleFeature.Wish, SimpleFeature.State, Nothing>(//SimpleFeature.News
    initialState = State(),
    reducer = ReducerIml()// newsPublisher = NewsPublisherImpl()
){
    data class State(// Define your immutable state as a Kotlin data class
        val counter: Int = 0// ТОЛЬКО val !!!  т.к.State должен быть immutable
    )

    sealed class Wish{  // Define the ways it could be affected
        object IncreaseCounter : Wish()//для интентов без параметров использум синглтоны
        data class MultiplyBy(val value: Int) : Wish()
    }

    class ReducerIml : Reducer<State, Wish>{// Define your reducer
        override fun invoke(state: State, wish: Wish): State =
            when(wish){// Leverage the power of exhaustive when over Kotlin sealed classes
                Wish.IncreaseCounter -> state.copy(counter = state.counter + 1) // Create the next state based on the current one
                is Wish.MultiplyBy -> state.copy(counter = state.counter * wish.value) // Create the next state based on the current one
            }

    }

//    sealed class News{
//        data class ArithmeticExceptionNews(val throwable: Throwable): News()
//    }
//
//    class  NewsPublisherImpl: SimpleNewsPublisher<Wish, State, News>() {
//        override fun invoke(wish: Wish, state: State): News?  =
//            when(wish){
//                is Wish.IncreaseCounter -> News.ArithmeticExceptionNews(Throwable("ArithmeticException: divide by zero"))
//                else -> null
//            }
//    }
}