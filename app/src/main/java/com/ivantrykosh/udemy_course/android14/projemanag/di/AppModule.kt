package com.ivantrykosh.udemy_course.android14.projemanag.di

import com.ivantrykosh.udemy_course.android14.projemanag.data.remote.firebase.FirebaseAuth
import com.ivantrykosh.udemy_course.android14.projemanag.data.remote.firebase.Firestore
import com.ivantrykosh.udemy_course.android14.projemanag.data.repository.UserAuthRepositoryImpl
import com.ivantrykosh.udemy_course.android14.projemanag.data.repository.UserRepositoryImpl
import com.ivantrykosh.udemy_course.android14.projemanag.domain.repository.UserAuthRepository
import com.ivantrykosh.udemy_course.android14.projemanag.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirestoreObject(): Firestore {
        return Firestore()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuthObject(): FirebaseAuth {
        return FirebaseAuth()
    }

    @Provides
    @Singleton
    fun provideUserRepository(firestore: Firestore, firebaseAuth: FirebaseAuth): UserRepository {
        return UserRepositoryImpl(firestore, firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideUserAuthRepository(firebaseAuth: FirebaseAuth): UserAuthRepository {
        return UserAuthRepositoryImpl(firebaseAuth)
    }
}