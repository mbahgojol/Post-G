package com.blank.mydiary.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blank.mydiary.api.FirebaseService
import com.blank.mydiary.api.ResultState
import com.blank.mydiary.data.Jurnal
import com.google.firebase.firestore.ktx.toObjects

class HomeViewModel : ViewModel() {
    val resultStateJurnal = MutableLiveData<ResultState>()
    val resultStateDeleteJurnal = MutableLiveData<ResultState>()

    fun getJurnal(deviceId: String) {
        resultStateJurnal.value = ResultState.Loading(true)
        FirebaseService.getText(deviceId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    e.printStackTrace()
                    resultStateJurnal.value = ResultState.Error(e)
                    resultStateJurnal.value = ResultState.Loading(false)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val model = snapshot.toObjects<Jurnal>().toMutableList()
                    resultStateJurnal.value = ResultState.Success(model)
                    resultStateJurnal.value = ResultState.Loading(false)
                } else {
                    resultStateJurnal.value = ResultState.Success(mutableListOf<Jurnal>())
                }
            }

    }

    fun deleteJurnal(jurnal: Jurnal) {
        FirebaseService.deleteData(jurnal)
            .addOnFailureListener {
                resultStateDeleteJurnal.value = ResultState.Error(it)
            }.addOnSuccessListener {
                resultStateDeleteJurnal.value = ResultState.Success(it)
            }
    }
}