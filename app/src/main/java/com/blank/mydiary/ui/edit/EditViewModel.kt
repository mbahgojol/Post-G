package com.blank.mydiary.ui.edit

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.blank.mydiary.api.FirebaseService
import com.blank.mydiary.api.ResultState
import com.blank.mydiary.data.SendJurnal
import com.blank.mydiary.utils.BaseViewModel
import com.blank.mydiary.utils.observableIo
import io.reactivex.rxjava3.core.Observable
import java.io.File

class EditViewModel : BaseViewModel() {
    val resultStatusSaveJurnal = MutableLiveData<ResultState>()
    val resultStatusGetAudio = MutableLiveData<ResultState>()

    fun saveJurnal(jurnal: SendJurnal) {
        resultStatusSaveJurnal.value = ResultState.Loading(true)
        val dataAudio = jurnal.data["fileName"] as MutableList<String>
        if (dataAudio.size == 0) {
            saveText(jurnal)
        } else {
            saveAudio(jurnal, dataAudio)
        }
    }

    private fun saveAudio(jurnal: SendJurnal, dataAudio: MutableList<String>) {
        var totalSend = 0
        Observable.create<String> { emitter ->
            dataAudio.forEach { filePath ->
                FirebaseService.saveAudio(File(filePath))
                    .addOnFailureListener {
                        emitter.onError(it)
                    }.addOnSuccessListener {
                        totalSend++
                        val path = it.metadata?.path.toString()
                        emitter.onNext(path)

                        Log.e("totalSend", totalSend.toString())
                        Log.e("PathMetadata", path)

                        if (totalSend == dataAudio.size)
                            emitter.onComplete()
                    }
            }
        }.observableIo()
            .toList()
            .subscribe({
                Log.e("Path", it.toString())
                jurnal.data["fileName"] = it
                saveText(jurnal)
            }, {
                Log.e("Recording", it.message.toString())
                resultStatusSaveJurnal.value = ResultState.Error(it)
                resultStatusSaveJurnal.value = ResultState.Loading(false)
            })
            .autoDispose()
    }

    private fun saveText(jurnal: SendJurnal) {
        FirebaseService.saveText(jurnal)
            .addOnFailureListener {
                it.printStackTrace()
                resultStatusSaveJurnal.value = ResultState.Error(it)
                resultStatusSaveJurnal.value = ResultState.Loading(false)
            }.addOnSuccessListener {
                resultStatusSaveJurnal.value = ResultState.Success(it)
                resultStatusSaveJurnal.value = ResultState.Loading(false)
            }
    }

    fun getAudio(dataAudio: MutableList<String>) {
        resultStatusGetAudio.value = ResultState.Loading(true)
        var totalSend = 0
        Observable.create<Uri> { emitter ->
            dataAudio.forEach { filePath ->
                FirebaseService.getAudio(filePath)
                    .addOnFailureListener {
                        emitter.onError(it)
                    }.addOnSuccessListener { uri ->
                        totalSend++
                        emitter.onNext(uri)

                        if (totalSend == dataAudio.size)
                            emitter.onComplete()
                    }
            }
        }.observableIo()
            .map {
                it.toString()
            }
            .toList()
            .subscribe({
                resultStatusGetAudio.value = ResultState.Success(it)
                resultStatusGetAudio.value = ResultState.Loading(false)
            }, {
                Log.e("Recording", it.message.toString())
                resultStatusGetAudio.value = ResultState.Error(it)
                resultStatusGetAudio.value = ResultState.Loading(false)
            })
            .autoDispose()
    }
}