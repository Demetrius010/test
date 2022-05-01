package com.bignerdranch.android.moxytest.views

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import moxy.viewstate.strategy.alias.AddToEnd

@StateStrategyType(value = AddToEndStrategy::class)//OneExecutionStateStrategy//стратегия ко всем методам интерфейса
interface MainActivityView: MvpView {
    fun startSending()
    fun endSending()
    fun showError(message: String)
    //@StateStrategyType(value = AddToEndStrategy::class)//переопределяем стратегию для конкретного метода
    fun showError(message: Int)
    //@AddToEnd//переопределяем стратегию для конкретного метода
    fun showSuccess()
}