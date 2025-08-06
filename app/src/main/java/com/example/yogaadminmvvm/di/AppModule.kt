package com.example.yogaadminmvvm.di

import android.content.Context
import com.example.yogaadminmvvm.data.local.AppDatabase
import com.example.yogaadminmvvm.data.local.dao.YogaClassDao
import com.example.yogaadminmvvm.data.local.dao.YogaCourseDao
import com.example.yogaadminmvvm.data.repository.YogaClassRepository
import com.example.yogaadminmvvm.data.repository.YogaClassRepositoryImpl
import com.example.yogaadminmvvm.data.repository.YogaCourseRepository
import com.example.yogaadminmvvm.data.repository.YogaCourseRepositoryImpl
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
    fun provideYogaCourseRepository(yogaCourseDao: YogaCourseDao): YogaCourseRepository {
        return YogaCourseRepositoryImpl(yogaCourseDao)
    }

    @Provides
    @Singleton
    fun provideYogaClassRepository(yogaClassDao: YogaClassDao): YogaClassRepository {
        return YogaClassRepositoryImpl(yogaClassDao)
    }
}
