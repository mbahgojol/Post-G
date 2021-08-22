package com.blank.mydiary.utils

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

fun <T> Single<T>.singleIo(): Single<T> = compose {
    it.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.observableIo(): Observable<T> = compose {
    it.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

fun <T> Flowable<T>.flowableIo(): Flowable<T> = compose {
    it.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}