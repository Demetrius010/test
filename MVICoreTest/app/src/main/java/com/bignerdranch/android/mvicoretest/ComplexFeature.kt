package com.bignerdranch.android.mvicoretest

import com.badoo.mvicore.element.Actor
import com.badoo.mvicore.element.Bootstrapper
import com.badoo.mvicore.element.PostProcessor
import com.badoo.mvicore.element.Reducer
import com.badoo.mvicore.feature.BaseFeature
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

class ComplexFeature : BaseFeature<ComplexFeature.Wish, ComplexFeature.Action,
        ComplexFeature.Effect, ComplexFeature.State, Nothing>(
    bootstrapper = BootstrapperImpl(),
    wishToAction = {Action.Execute(it)},
    postProcessor = PostProcessorImpl(),
    actor = ActorImpl(),
    reducer = ReducerIml(),
    initialState = State()
){
    data class State(// Define your immutable state as a Kotlin data class
        val counter: Int = 0// ТОЛЬКО val !!!  т.к.State должен быть immutable
    )

    sealed class Wish{  // Define the ways it could be affected
        object PublicWish1 : Wish()//для интентов без параметров использум синглтоны
        object PublicWish2 : Wish()
        object PublicWish3 : Wish()
    }

    sealed class Effect{
        object StartedLoading: Effect()
    }

    sealed class Action{
        data class Execute(val wish: Wish): Action()
        object InvalidateCache : Action()
        object ReloadSomething : Action()
    }

    class BootstrapperImpl : Bootstrapper<Action>{
        private val service1: Observable<Any> = TODO()
        private val service2: Observable<Any> = TODO()

        override fun invoke(): Observable<Action> = Observable.merge<Action>(
            service1.map{Action.InvalidateCache},
            service2.map { Action.ReloadSomething }
        ).observeOn(AndroidSchedulers.mainThread())
    }

    class ActorImpl : Actor<State, Action, Effect>{
        override fun invoke(state: State, action: Action): Observable<Effect> =
            when(action){
                is Action.Execute -> when(action.wish){
                    Wish.PublicWish1 -> Observable.just(Effect.StartedLoading)
                    Wish.PublicWish2 -> TODO()
                    Wish.PublicWish3 -> TODO()
                }
                Action.InvalidateCache -> TODO()
                Action.ReloadSomething -> TODO()
            }
    }

    class ReducerIml : Reducer<State, Effect>{// Define your reducer
    override fun invoke(state: State, effect: Effect): State =
        when(effect){// Leverage the power of exhaustive when over Kotlin sealed classes
            Effect.StartedLoading -> state.copy(counter = state.counter + 1)
        }
    }

    class PostProcessorImpl : PostProcessor<Action, Effect, State>{
        override fun invoke(action: Action, effect: Effect, state: State): Action? {
            if (state.counter == 3){
                return Action.InvalidateCache
            }
            return null
        }
    }
}