package com.bignerdranch.android.moxytest

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.bignerdranch.android.moxytest.models.Repository
import com.bignerdranch.android.moxytest.presenters.MainActivityPresenter
import com.bignerdranch.android.moxytest.views.MainActivityView
import moxy.MvpAppCompatActivity
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class MainActivity : MvpAppCompatActivity(), MainActivityView {//для фрагментов MvpAppCompatFragment

    //с учётом того, что Presenter полностью отвязан от жизненного цикла Activity, вы можете спокойно создавать экземпляр конкретной Model внутри Presenter и работать с ним.Используя DI вы можете подключать нужную Model в Presenter
    val repository = Repository() //TODO() ЭТО ДОЛЖНО БЫТЬ В Presenter, а тут просто для примера как передавать параметры в Presenter//Если есть даггер то @Inject lateinit var repository: Repository

    @InjectPresenter//(presenterId = "", tag = "", type = PresenterType.GLOBAL)// type ВЫРЕЗАЛИ ИЗ Moxy 2.x.x) GLOBAL-чтобы Presenter жил не зависимо от того, кто и когда на него подписан
    lateinit var mainActivityPresenter: MainActivityPresenter//Presenter будет жить пока есть View, в которой он содержится(+ пока происходит смена конфигурации)

    @ProvidePresenter//если презентер не получает параметры то этот метод не нужен
    fun provideMainActivityPresenter(): MainActivityPresenter {//Нужно чтобы функция @ProvidePresenter гарантированно создавала новый инстанс presenter.
        return MainActivityPresenter(repository)
    }


//    To use MvpDelegateHolder.moxyPresenter and MvpPresenter.presenterScope, add this:
//    implementation "com.github.moxy-community:moxy-ktx:$moxyVersion"
//    private val mainActivityPresenter:MainActivityPresenter by moxyPresenter {MyDaggerAppComponent.get().myPresenter}

    //    Inject with Dagger2 https://habr.com/ru/post/506806/
//    @Inject
//    lateinit var presenterProvider: Provider<MainPresenter>
//    private val presenter by moxyPresenter { presenterProvider.get() }


    lateinit var btnLogin: Button
    lateinit var etMail: EditText
    lateinit var etPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnLogin = findViewById(R.id.btnLogin)
        etMail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)

        btnLogin.setOnClickListener{
            mainActivityPresenter.login(etMail.text.toString(), etPassword.text.toString())
        }
    }


    //VIEW IMPLEMENTATION
    override fun startSending() {
        Toast.makeText(this, "startSending", Toast.LENGTH_SHORT).show()
    }

    override fun endSending() {
        Toast.makeText(this, "endSending", Toast.LENGTH_SHORT).show()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showError(message: Int) {
        Toast.makeText(this, getString(message), Toast.LENGTH_SHORT).show()
    }

    override fun showSuccess() {
        Toast.makeText(this, "SUCCESS", Toast.LENGTH_SHORT).show()
    }
}