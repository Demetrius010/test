package com.bignerdranch.android.moxytest.presenters

import com.bignerdranch.android.moxytest.models.Repository
import com.bignerdranch.android.moxytest.views.MainActivityView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import moxy.InjectViewState
import moxy.MvpPresenter
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import moxy.presenterScope

//@InjectViewState// поидее надо указывать но у меня и так работает
class MainActivityPresenter(repository: Repository) : MvpPresenter<MainActivityView>(){
    //В ПРЕЗЕНТЕРЕ НЕ ДОЛЖНО БЫТЬ ССЫЛОК НА  Context/Activity/Fragment

    //Проинициализировать компонент можно в Presenter.onFirstViewAttach() и освободить в Presenter.onDestroy() — это единственные коллбэки, о которых нам нужно задумываться.
    override fun onFirstViewAttach() {//вызывается тогда, когда к конкретному экземпляру Presenter первый раз будет привязана любая View. А когда к этому Presenter будет привязана другая View, к ней уже будет применено состояние из ViewState
        //А когда к этому Presenter будет привязана другая View, к ней уже будет применено состояние из ViewState. И здесь уже не важно, эта новая View – совсем другая View, или пересозданная в результате смены конфигурации
        //isInRestoreState()// это новая View или пересозданная? В момент, когда во View пришла команда, вам может потребоваться понять, это новая команда, или это команда для восстановления состояния?

        viewState.showError("Hello")

//        To use MvpDelegateHolder.moxyPresenter and MvpPresenter.presenterScope, add this:
//        implementation "com.github.moxy-community:moxy-ktx:$moxyVersion"
        //presenterScope.launch {//Launch coroutines in presenter scope:
            // Coroutine that will be canceled when presenter is destroyed
       // }
    }

    fun login(email:String, password:String){
        viewState.startSending()
        presenterScope.launch {//Moxy обращается к View с того потока, с которого обратился к нему presenter. Поэтому нужно самостоятельно следить в presenter за тем, чтобы методы viewState вызвались из главного потока.
            delay(5000L)
            viewState.endSending()
            viewState.showSuccess()
        }
    }

    override fun onDestroy() {//вызывается при окончательном уничтожении View.Проинициализировать компонент можно в Presenter.onFirstViewAttach() и освободить в Presenter.onDestroy()
        super.onDestroy()
    }

//    методы для привязывания/отвязывания View от Presenter
//    override fun attachView(view: MainActivityView?) {к одному Presenter может быть привязано несколько View. Они будут всегда иметь актуальное состояние(за счёт ViewState)
//    override fun detachView(view: MainActivityView?) {если вы хотите, чтобы привязывание/отвязывание View проходило НЕ через стандартное поле ViewState, то можете переопределить эти методы и работать с пришедшей View как хотите.
}