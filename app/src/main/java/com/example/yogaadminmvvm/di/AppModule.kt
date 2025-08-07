package com.example.yogaadminmvvm.di

import android.content.Context
import com.example.yogaadminmvvm.data.local.AppDatabase
import com.example.yogaadminmvvm.data.local.dao.YogaClassDao
import com.example.yogaadminmvvm.data.local.dao.YogaCourseDao
import com.example.yogaadminmvvm.data.remote.FirebaseService
import com.example.yogaadminmvvm.data.remote.FirebaseYogaClass
import com.example.yogaadminmvvm.data.repository.YogaClassRepository
import com.example.yogaadminmvvm.data.repository.YogaClassRepositoryImpl
import com.example.yogaadminmvvm.data.repository.YogaCourseRepository
import com.example.yogaadminmvvm.data.repository.YogaCourseRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return AppDatabase.getDatabase(appContext)
    }

    @Provides
    @Singleton
    fun provideYogaCourseDao(appDatabase: AppDatabase): YogaCourseDao {
        return appDatabase.yogaCourseDao()
    }

    @Provides
    @Singleton
    fun provideYogaClassDao(appDatabase: AppDatabase): YogaClassDao {
        return appDatabase.yogaClassDao()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }

    // Provider for FirebaseService
    @Provides
    @Singleton
    fun provideFirebaseService(firestore: FirebaseFirestore): FirebaseService {
        return FirebaseService(firestore)
    }

    // Provider for FirebaseService
    @Provides
    @Singleton
    fun provideFirebaseYogaClass(firestore: FirebaseFirestore): FirebaseYogaClass {
        return FirebaseYogaClass(firestore)
    }

    @Provides
    @Singleton
    fun provideYogaCourseRepository(
        yogaCourseDao: YogaCourseDao,
        firebaseService: FirebaseService // Inject FirebaseService
    ): YogaCourseRepository {
        return YogaCourseRepositoryImpl(yogaCourseDao, firebaseService) // Pass FirebaseService
    }

    @Provides
    @Singleton
    fun provideYogaClassRepository(
        yogaClassDao: YogaClassDao,
        yogaClassFirebase: FirebaseYogaClass
    ): YogaClassRepository {
        return YogaClassRepositoryImpl(yogaClassDao, yogaClassFirebase)
    }
}
