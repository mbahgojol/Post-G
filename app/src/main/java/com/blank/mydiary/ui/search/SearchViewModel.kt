package com.blank.mydiary.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blank.mydiary.api.FirebaseService
import com.blank.mydiary.api.ResultState
import com.blank.mydiary.data.Jurnal
import com.google.firebase.crashlytics.FirebaseCrashlytics

class SearchViewModel : ViewModel() {
    val resultStateSearch = MutableLiveData<ResultState>()
    val resultStateDeleteJurnal = MutableLiveData<ResultState>()

    fun searchText(deviceId: String, text: String) {
        resultStateSearch.value = ResultState.Loading(true)
        FirebaseService.getText(deviceId)
            .whereGreaterThanOrEqualTo("title", text)
            .whereLessThan("title", text.plus('z'))
            .get()
            .addOnSuccessListener {
                resultStateSearch.value = ResultState.Success(it)
            }.addOnFailureListener {
                FirebaseCrashlytics.getInstance().recordException(it)
                resultStateSearch.value = ResultState.Error(it)
                resultStateSearch.value = ResultState.Loading(false)
            }
    }

    fun deleteJurnal(jurnal: Jurnal) {
        FirebaseService.deleteData(jurnal)
            .addOnFailureListener {
                FirebaseCrashlytics.getInstance().recordException(it)
                resultStateDeleteJurnal.value = ResultState.Error(it)
            }.addOnSuccessListener {
                resultStateDeleteJurnal.value = ResultState.Success(it)
            }
    }
}