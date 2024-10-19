package com.services.provider.di

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.services.provider.R
import com.services.provider.data.prefs.MyPref
import com.services.provider.data.repo.AppointmentRepositoryImpl
import com.services.provider.data.repo.AuthRepositoryImpl
import com.services.provider.data.repo.MessageRepositoryImpl
import com.services.provider.data.repo.ServiceRepositoryImpl
import com.services.provider.domain.repository.AppointmentRepository
import com.services.provider.domain.repository.AuthRepository
import com.services.provider.domain.repository.MessageRepository
import com.services.provider.domain.repository.ServicesRepository
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
    fun provideHandler(): Handler = Handler(Looper.getMainLooper())

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences(
            context.getString(R.string.app_name),
            Context.MODE_PRIVATE
        )


    @Provides
    @Singleton
    fun provideFirestore() = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage(): StorageReference = FirebaseStorage.getInstance().reference


    @Provides
    @Singleton
    fun provideAuthRepository(
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth,
        prefRepository: MyPref,
        storageRef: StorageReference,
        serviceRepositoryImpl: ServiceRepositoryImpl,
        appointmentRepositoryImpl: AppointmentRepositoryImpl,
        messageRepositoryImpl: MessageRepositoryImpl
    ): AuthRepository = AuthRepositoryImpl(firestore, firebaseAuth, prefRepository, storageRef,serviceRepositoryImpl,appointmentRepositoryImpl,messageRepositoryImpl)

    @Provides
    @Singleton
    fun provideAppointmentRepository(
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth,
        prefRepository: MyPref,
        storageRef: StorageReference

    ): AppointmentRepository =
        AppointmentRepositoryImpl(firestore, firebaseAuth, prefRepository, storageRef)

    @Provides
    @Singleton
    fun provideServicesRepository(
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth,
        prefRepository: MyPref,
        storageRef: StorageReference
    ): ServicesRepository =
        ServiceRepositoryImpl(firestore, firebaseAuth, prefRepository, storageRef)

    @Provides
    @Singleton
    fun provideMessageRepositoryImpl(
        firestore: FirebaseFirestore,
        prefRepository: MyPref,
    ): MessageRepository =
        MessageRepositoryImpl(firestore, prefRepository)


}