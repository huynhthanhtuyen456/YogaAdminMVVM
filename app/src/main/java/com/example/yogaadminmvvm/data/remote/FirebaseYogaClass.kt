package com.example.yogaadminmvvm.data.remote

import com.example.yogaadminmvvm.data.local.entities.YogaClassEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseYogaClass @Inject constructor(private val firestore: FirebaseFirestore) {

    private val classesCollection = firestore.collection("yogaClasses")

    // Takes YogaCourseEntity, uses its 'id' (Int) to form the String document ID
    suspend fun uploadYogaCourse(yogaClass: YogaClassEntity) {
        classesCollection.document(yogaClass.id.toString()).set(yogaClass).await()
    }

    suspend fun updateYogaCourse(yogaClass: YogaClassEntity) {
        classesCollection.document(yogaClass.id.toString()).set(yogaClass).await()
    }

    // This already takes a String, which is good.
    suspend fun deleteYogaCourse(yogaClassId: String) {
        classesCollection.document(yogaClassId).delete().await()
    }
}