package com.bignerdranch.android.mvicoretest

import android.util.Log
import com.badoo.mvicore.element.Actor
import com.badoo.mvicore.element.NewsPublisher
import com.badoo.mvicore.element.Reducer
import com.badoo.mvicore.feature.ActorReducerFeature
import io.reactivex.Observable
import io.reactivex.Observable.empty
import io.reactivex.android.schedulers.AndroidSchedulers
import java.lang.Exception


class AsyncFeature: ActorReducerFeature<AsyncFeature.Wish, AsyncFeature.Effect, AsyncFeature.State, AsyncFeature.News>(//Nothing>(
    initialState = State(),
    actor = ActorImpl(),
    reducer = ReducerImpl(),
    newsPublisher = NewsPublisherImpl()
) {
    data class State(val isLoading: Boolean = false, val payload: String? = null)

    sealed class Wish{
        object LoadNewData : Wish()
    }

    sealed class Effect{
        object StartedLoading: Effect()
        data class FinishedWithSuccess(val payload: String?): Effect()
        data class FinishedWithError(val throwable: Throwable): Effect()

        data class ErrorLoading(val throwable: Throwable): Effect()
    }

    class ActorImpl: Actor<State, Wish, Effect>{
        private val service: Observable<String> = Observable.fromArray("a","b","c","d")//TODO()

        override fun invoke(state: State, wish: Wish): Observable<Effect> =
            when(wish){
                is Wish.LoadNewData -> {
                    if(!state.isLoading){
                        service
                            .observeOn(AndroidSchedulers.mainThread())
                            .map{Effect.FinishedWithSuccess(payload = it) as Effect}
                            .startWith(Effect.StartedLoading)
                            .onErrorReturn{Effect.FinishedWithError(it)}
                    }
                    else{
                        //Observable.just(Effect.ErrorLoading(Throwable("MyThrowable")))
                        Observable.empty()
                    }
                }
            }
    }

    class ReducerImpl: Reducer<State, Effect>{
        override fun invoke(state: State, effect: Effect): State =
            when(effect){
                is Effect.StartedLoading -> state.copy(isLoading = true)
                is Effect.FinishedWithSuccess -> state.copy(isLoading = false, payload = effect.payload)
                is Effect.FinishedWithError -> {
                    Log.d("AsyncFeature", "FinishedWithError " + effect.throwable.message ?: "Throwable")//здесь я могу обработать исключение
                    state.copy(isLoading = false)
                }

                is Effect.ErrorLoading -> {
                    Log.d("AsyncFeature", "ErrorLoading " + effect.throwable.message ?: "Throwable")//здесь я могу обработать исключение
                    state
                }
            }
    }

    sealed class News{
        data class ErrorExecutingRequest(val throwable: Throwable): News()
    }

    class  NewsPublisherImpl: NewsPublisher<Wish, Effect, State, News>{
        override fun invoke(wish: Wish, effect: Effect, state: State): News? =
            when(effect){
                is Effect.ErrorLoading -> {
                    Log.d("AsyncFeature", "NewsPublisherImpl " + effect.throwable.message ?: "Throwable")//здесь я могу обработать исключение
                    News.ErrorExecutingRequest(effect.throwable)
                }
                else -> null
            }
    }
}