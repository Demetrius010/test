package com.bignerdranch.android.mvicoretest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.badoo.binder.Binder
import com.badoo.binder.lifecycle.ManualLifecycle
import com.badoo.mvicore.android.AndroidTimeCapsule
import com.badoo.mvicore.element.TimeCapsule
import com.bignerdranch.android.mvicoretest.databinding.ActivityMainBinding
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.disposables.Disposable

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    var left = 0L
            get(){
                field = binding.leftEt.text.toString().toLongOrNull() ?: 0L
                return field
            }
    var right = 0L
        get(){
            field = binding.rightEt.text.toString().toLongOrNull() ?: 0L
            return field
        }

//    val feature = SimpleFeature()
//    val asyncFeature = AsyncFeature()
//    val complexFeature = ComplexFeature()
    lateinit var timeCapsule: AndroidTimeCapsule
    lateinit var mathOpFuture: MathOperationsFuture
    //val binder = Binder(ManualLifecycle())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)//R.layout.activity_main)

        timeCapsule = AndroidTimeCapsule(savedInstanceState)//используем для сохранения State между поворотами экрана
        mathOpFuture = MathOperationsFuture(timeCapsule)// restore


        binding.addBtn.setOnClickListener{// Now you can observe and subscribe to its state changes:
            //Observable.just(SimpleFeature.Wish.IncreaseCounter).subscribe(feature)
            Observable.just(MathOperationsFuture.Wish.Add(left,right)).subscribe(mathOpFuture)
        }
        binding.minusBtn.setOnClickListener{
            Observable.just(MathOperationsFuture.Wish.Minus(left,right)).subscribe(mathOpFuture)
        }
        binding.multiplyBtn.setOnClickListener{
            Observable.just(MathOperationsFuture.Wish.Multiply(left, right)).subscribe(mathOpFuture)
        }
        binding.divideBtn.setOnClickListener{
            Observable.just(MathOperationsFuture.Wish.Divide(left, right)).subscribe(mathOpFuture)
        }

        Observable.wrap(mathOpFuture).subscribe{state->
            binding.resultTw.setText(state.result.toString())
        }

        Observable.wrap(mathOpFuture.news).subscribe{news->
            Toast.makeText(this, (news as MathOperationsFuture.News.ArithmeticExceptionNews).throwable.message, Toast.LENGTH_SHORT).show()
        }



//        // And it's also a Consumer of Wishes. Trigger some state changes:
//        Observable.wrap(feature).subscribe{state->
//            twResult.setText(state.toString())
//            Log.d("MainActivity", state.toString())
//        }

//        Observable.just(AsyncFeature.Wish.LoadNewData).subscribe(asyncFeature)
//        Observable.wrap(asyncFeature).subscribe{state->
//            Log.d("MainActivity", state.toString())
//        }

        //подписка на news  (вместе с News приходит и state!!! и как я понял отдельно News нельзя отправить (см. схему News идет только если был отправлен State) )
//        Observable.wrap(asyncFeature.news).subscribe{news->
//            Log.d("MainActivity", "News "+ (news as AsyncFeature.News.ErrorExecutingRequest).throwable.message)
//        }



//        binder.bind(this to feature using ViewEventToWish)
//        binder.bind(feature to this using StateToViewModel)

//        val output: PublishSubject<String> = PublishSubject.create()
//        val input: Consumer<String> = Consumer { System.out.println(it) }
//
//        val lifecycle = ManualLifecycle()
//        val binder = Binder(lifecycle)
//
//        binder.bind(output to input)
//        output.onNext("1")
//        lifecycle.begin()
//        output.onNext("2")
//        output.onNext("3")
//        lifecycle.end()
//        output.onNext("4")
//        lifecycle.begin()
//        output.onNext("5")
//        output.onNext("6")
//        lifecycle.end()
//        output.onNext("7")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        timeCapsule.saveState(outState)// save используем для сохранения State между поворотами экрана
    }

    override fun onDestroy() {
        super.onDestroy()
//        feature.dispose()
//        asyncFeature.dispose()
        mathOpFuture.dispose()
    }
}