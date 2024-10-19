package com.services.provider.di

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import com.services.provider.databinding.FragmentAllUsersBinding
import com.services.provider.databinding.FragmentAppointmentBinding
import com.services.provider.databinding.FragmentChatsBinding
import com.services.provider.databinding.FragmentLoginBinding
import com.services.provider.databinding.FragmentPendingUsersBinding
import com.services.provider.databinding.FragmentRatingBinding
import com.services.provider.databinding.FragmentSearchBinding
import com.services.provider.databinding.FragmentServicesBinding
import com.services.provider.databinding.FragmentSignupBinding
import com.services.provider.databinding.FragmentSkilledHomeBinding
import com.services.provider.databinding.FragmentUserDetialsBinding
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.FragmentScoped

@Module
@InstallIn(FragmentComponent::class)
object FragmentModule {

    @Provides
    @FragmentScoped
    fun provideSignInFragBinding(@ApplicationContext context: Context): FragmentLoginBinding =
        FragmentLoginBinding.inflate(LayoutInflater.from(context))

    @Provides
    @FragmentScoped
    fun provideSignUpFragBinding(@ApplicationContext context: Context): FragmentSignupBinding =
        FragmentSignupBinding.inflate(LayoutInflater.from(context))

    @Provides
    @FragmentScoped
    fun provideFragmentAppointmentBinding(@ActivityContext context: Context) =
        FragmentAppointmentBinding.inflate((context as Activity).layoutInflater)

    @Provides
    @FragmentScoped
    fun provideFragmentSkilledHomeBinding(@ActivityContext context: Context) =
        FragmentSkilledHomeBinding.inflate((context as Activity).layoutInflater)


    @Provides
    @FragmentScoped
    fun provideFragmentChatsBinding(@ActivityContext context: Context) =
        FragmentChatsBinding.inflate((context as Activity).layoutInflater)

    @Provides
    @FragmentScoped
    fun provideFragmentSearchBinding(@ActivityContext context: Context) =
        FragmentSearchBinding.inflate((context as Activity).layoutInflater)

    @Provides
    @FragmentScoped
    fun provideFragmentServicesBinding(@ActivityContext context: Context) =
        FragmentServicesBinding.inflate((context as Activity).layoutInflater)

    @Provides
    @FragmentScoped
    fun provideFragmentUserDetialsBinding(@ActivityContext context: Context) =
        FragmentUserDetialsBinding.inflate((context as Activity).layoutInflater)

    @Provides
    @FragmentScoped
    fun provideFragmentRatingBinding(@ActivityContext context: Context) =
        FragmentRatingBinding.inflate((context as Activity).layoutInflater)
  @Provides
    @FragmentScoped
    fun provideFragmentPendingUsersBinding(@ActivityContext context: Context) =
        FragmentPendingUsersBinding.inflate((context as Activity).layoutInflater)
  @Provides
    @FragmentScoped
    fun provideFragmentAllUsersBinding(@ActivityContext context: Context) =
        FragmentAllUsersBinding.inflate((context as Activity).layoutInflater)

}