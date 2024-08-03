package com.ivantrykosh.udemy_course.android14.projemanag.di

import com.ivantrykosh.udemy_course.android14.projemanag.data.remote.firebase.FirebaseAuth
import com.ivantrykosh.udemy_course.android14.projemanag.data.remote.firebase.firestore.FirestoreBoard
import com.ivantrykosh.udemy_course.android14.projemanag.data.remote.firebase.firestore.FirestoreUser
import com.ivantrykosh.udemy_course.android14.projemanag.data.repository.BoardRepositoryImpl
import com.ivantrykosh.udemy_course.android14.projemanag.data.repository.UserAuthRepositoryImpl
import com.ivantrykosh.udemy_course.android14.projemanag.data.repository.UserRepositoryImpl
import com.ivantrykosh.udemy_course.android14.projemanag.domain.repository.BoardRepository
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
    fun provideFirestoreUserObject(): FirestoreUser {
        return FirestoreUser()
    }

    @Provides
    @Singleton
    fun provideFirestoreBoardObject(): FirestoreBoard {
        return FirestoreBoard()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuthObject(): FirebaseAuth {
        return FirebaseAuth()
    }

    @Provides
    @Singleton
    fun provideUserRepository(firestoreUser: FirestoreUser, firebaseAuth: FirebaseAuth): UserRepository {
        return UserRepositoryImpl(firestoreUser, firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideUserAuthRepository(firebaseAuth: FirebaseAuth): UserAuthRepository {
        return UserAuthRepositoryImpl(firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideBoardRepository(firestoreBoard: FirestoreBoard, firebaseAuth: FirebaseAuth): BoardRepository {
        return BoardRepositoryImpl(firestoreBoard, firebaseAuth)
    }
}