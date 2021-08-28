package com.blank.mydiary.api

import android.net.Uri
import com.blank.mydiary.data.Jurnal
import com.blank.mydiary.data.SendJurnal
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.File
import java.io.FileInputStream

object FirebaseService {
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference
    private fun dbFb() = Firebase.firestore
    var fileName = ""

    fun saveAudio(file: File): UploadTask {
        val ref = storageRef.child("record/${file.name}")
        val stream = FileInputStream(file)
        fileName = ref.name
        return ref.putStream(stream)
    }

    fun getAudio(fileName: String): Task<Uri> = storageRef.child(fileName)
        .downloadUrl

    fun saveText(jurnal: SendJurnal): Task<Void> =
        dbFb().collection(jurnal.data["deviceId"].toString())
            .document(jurnal.data["jurnalId"].toString())
            .set(jurnal.data)

    fun editText(jurnal: SendJurnal): Task<Void> =
        dbFb().collection(jurnal.data["deviceId"].toString())
            .document(jurnal.data["jurnalId"].toString())
            .update(jurnal.data)

    fun deleteData(jurnal: Jurnal): Task<Void> =
        dbFb().collection(jurnal.deviceId)
            .document(jurnal.jurnalId)
            .delete()

    fun getText(deviceId: String) = dbFb()
        .collection(deviceId)
}