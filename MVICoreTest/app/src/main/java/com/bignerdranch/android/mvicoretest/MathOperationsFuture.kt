package com.bignerdranch.android.mvicoretest

import android.os.Parcelable
import android.util.Log
import androidx.versionedparcelable.ParcelField
import com.badoo.mvicore.element.*
import com.badoo.mvicore.feature.ActorReducerFeature
import com.badoo.mvicore.feature.BaseFeature
import com.badoo.mvicore.feature.ReducerFeature
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.parcelize.Parcelize

import java.util.concurrent.TimeUnit

class MathOperationsFuture(timeCapsule: TimeCapsule<Parcelable>? = null) : BaseFeature<MathOperationsFuture.Wish, MathOperationsFuture.Action,
        MathOperationsFuture.Effect, MathOperationsFuture.State, MathOperationsFuture.News>(
    bootstrapper = BootstrapperImpl(),
    wishToAction = {Action.Execute(it)},
    actor = ActorImpl(),
    postProcessor = PostProcessorImpl(),
    reducer = ReducerIml(),
    newsPublisher = NewsPublisherImpl(),
    initialState = timeCapsule?.get(MathOperationsFuture::class.java) ?: State()// initial state depends on having something inside TimeCapsule, or falling back to default value:
){
    init{
        timeCapsule?.register(MathOperationsFuture::class.java){ state }//state.copy(result = myValue) // используем для сохранения State между поворотами экрана
    }

    @Parcelize
    data class State(// Define your immutable state as a Kotlin data class
        val result: Long = 0// ТОЛЬКО val !!!  т.к.State должен быть immutable
    ) : Parcelable

    sealed class Wish{  // Define the ways it could be affected
        data class Add(val left: Long, val right: Long) : Wish()//для интентов без параметров использум синглтоны (object)
        data class Minus(val left: Long, val right: Long) : Wish()
        data class Multiply(val left: Long, val right: Long) : Wish()
        data class Divide(val left: Long, val right: Long) : Wish()
    }

    sealed class Effect{
        data class FinishedWithSucces(val result: Long): Effect()
        data class FinishedWithError(val throwable: Throwable): Effect()
    }

    sealed class Action{
        data class Execute(val wish: Wish) : Action()//обертка над Wish
        object InvalidateCache : Action()
        object ReloadSomething : Action()
    }

    class BootstrapperImpl : Bootstrapper<Action>{//умеет создавать Action's
        private val service1: Observable<Any>  = Observable.empty()//TODO
        private val service2: Observable<Any> = Observable.empty()// Observable.timer(5, TimeUnit.SECONDS)// какой-то внешний источник, (например рассылка данных от сервера)

        override fun invoke(): Observable<Action> = Observable.merge<Action>(
            service1.map{ Action.InvalidateCache},
            service2.map { Action.ReloadSomething }
        ).observeOn(AndroidSchedulers.mainThread())
    }

    class ActorImpl: Actor<State, Action, Effect> {
        override fun invoke(state: State, action: Action): Observable<Effect> =
            when(action){
                is Action.Execute -> when(action.wish){
                    is Wish.Divide ->
                        if (action.wish.right == 0L) Observable.just(Effect.FinishedWithError(Throwable("ArithmeticException: divide by zero")))// NewsPublisher не использует передаваемый объявляемый здесь Throwable
                        else Observable.just(Effect.FinishedWithSucces(result = action.wish.left / action.wish.right))
                    is Wish.Add -> Observable.just(Effect.FinishedWithSucces(result = action.wish.left + action.wish.right)) // Create the next state
                    is Wish.Minus -> Observable.just(Effect.FinishedWithSucces(result = action.wish.left - action.wish.right))
                    is Wish.Multiply -> Observable.just(Effect.FinishedWithSucces(result = action.wish.left * action.wish.right))
                    //else -> Observable.empty()
                }
                Action.InvalidateCache -> Observable.just(Effect.FinishedWithError(Throwable("Greater then 1 000 000 000")))//TODO()
                Action.ReloadSomething -> Observable.just(Effect.FinishedWithError(Throwable("Timer")))//А если запустить это то получится бесконечный цикл т.к. значение из поля не изменится Observable.just(Effect.FinishedWithError(Throwable("NotImplementedException")))
            }

    }

    class ReducerIml : Reducer<State, Effect>{// Define your reducer
    override fun invoke(state: State, effect: Effect): State =
        when(effect){// Leverage the power of exhaustive when over Kotlin sealed classes
            is Effect.FinishedWithSucces -> state.copy(effect.result)
            is Effect.FinishedWithError -> state.copy(0)
        }
    }

    class PostProcessorImpl : PostProcessor<Action, Effect, State>{
        override fun invoke(action: Action, effect: Effect, state: State): Action? {
            if(state.result > 1000000000L){// если запустить это то получится бесконечный цикл т.к. значение из поля не изменится а Action.ReloadSomething будет спамится дальше
                return Action.InvalidateCache
            }
            return null
        }
    }

    sealed class News{
        data class ArithmeticExceptionNews(val throwable: Throwable): News()
    }

    class  NewsPublisherImpl: NewsPublisher<Action, Effect, State, News>{
        override fun invoke(action: Action, effect: Effect, state: State): News? =
            when (effect) {
                is Effect.FinishedWithError -> News.ArithmeticExceptionNews(effect.throwable)// Caused by: java.lang.ArithmeticException: divide by zero
                else -> null
            }
    }
}

/*
class MathOperationsFuture : ActorReducerFeature<MathOperationsFuture.Wish, MathOperationsFuture.Effect, MathOperationsFuture.State, MathOperationsFuture.News>(
    initialState = State(),
    actor = ActorImpl(),
    reducer = ReducerIml(),
    newsPublisher = NewsPublisherImpl()
){
    data class State(// Define your immutable state as a Kotlin data class
        val result: Long = 0// ТОЛЬКО val !!!  т.к.State должен быть immutable
    )

    sealed class Wish{  // Define the ways it could be affected
        data class Add(val left: Long, val right: Long) : Wish()//для интентов без параметров использум синглтоны (object)
        data class Minus(val left: Long, val right: Long) : Wish()
        data class Multiply(val left: Long, val right: Long) : Wish()
        data class Divide(val left: Long, val right: Long) : Wish()
    }

    sealed class Effect{
        data class FinishedWithSucces(val result: Long): Effect()
        data class FinishedWithError(val throwable: Throwable): Effect()
    }

    class ActorImpl: Actor<State, Wish, Effect> {
        override fun invoke(state: State, wish: Wish): Observable<Effect> =
            when(wish){
                is Wish.Divide ->
                    if (wish.right == 0L) Observable.just(Effect.FinishedWithError(Throwable("ArithmeticException: divide by zero")))
                    else Observable.just(Effect.FinishedWithSucces(result = wish.left / wish.right))
                is Wish.Add -> Observable.just(Effect.FinishedWithSucces(result = wish.left + wish.right)) // Create the next state
                is Wish.Minus -> Observable.just(Effect.FinishedWithSucces(result = wish.left - wish.right))
                is Wish.Multiply -> Observable.just(Effect.FinishedWithSucces(result = wish.left * wish.right))
                //else -> Observable.empty()
            }
    }

    class ReducerIml : Reducer<State, Effect>{// Define your reducer
    override fun invoke(state: State, effect: Effect): State =
        when(effect){// Leverage the power of exhaustive when over Kotlin sealed classes
            is Effect.FinishedWithSucces -> state.copy(effect.result)
            is Effect.FinishedWithError -> state
        }
    }

    sealed class News{
        data class ArithmeticExceptionNews(val throwable: Throwable): News()
    }

    class  NewsPublisherImpl: NewsPublisher<Wish, Effect, State, News>{
        override fun invoke(wish: Wish, effect: Effect, state: State): News?  =
            when(effect){
                is Effect.FinishedWithError -> News.ArithmeticExceptionNews(Throwable("ArithmeticException: divide by zero"))// Caused by: java.lang.ArithmeticException: divide by zero
                else -> null
            }
    }
}
 */