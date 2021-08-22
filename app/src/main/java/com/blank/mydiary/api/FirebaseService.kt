package com.blank.mydiary.api

import android.net.Uri
import com.blank.mydiary.data.SendJurnal
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.File
import java.io.FileInputStream

object FirebaseService {
    private val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference
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

    fun saveText(jurnal: SendJurnal): Task<DocumentReference> =
        dbFb().collection(jurnal.deviceId)
            .add(jurnal.data)

    fun getText(deviceId: String) = dbFb()
        .collection(deviceId)
}