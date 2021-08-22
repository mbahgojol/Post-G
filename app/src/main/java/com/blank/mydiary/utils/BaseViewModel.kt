package com.blank.mydiary.utils

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

abstract class BaseViewModel : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    fun Disposable.autoDispose() {
        compositeDisposable.add(this)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}