package com.example.yogaadminmvvm.data.remote

import com.example.yogaadminmvvm.data.local.entities.YogaCourseEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseService @Inject constructor(private val firestore: FirebaseFirestore) {

    private val coursesCollection = firestore.collection("yogaCourses")

    // Takes YogaCourseEntity, uses its 'id' (Int) to form the String document ID
    suspend fun uploadYogaCourse(course: YogaCourseEntity) {
        coursesCollection.document(course.id.toString()).set(course).await()
    }

    suspend fun updateYogaCourse(course: YogaCourseEntity) {
        coursesCollection.document(course.id.toString()).set(course).await()
    }

    // This already takes a String, which is good.
    suspend fun deleteYogaCourse(courseId: String) {
        coursesCollection.document(courseId).delete().await()
    }
}