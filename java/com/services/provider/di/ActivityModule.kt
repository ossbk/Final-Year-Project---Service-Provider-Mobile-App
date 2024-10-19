package com.services.provider.di

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import com.services.provider.databinding.ActivityAddServiceBinding
import com.services.provider.databinding.ActivityAdminBinding
import com.services.provider.databinding.ActivityAuthBinding
import com.services.provider.databinding.ActivityBookAppointmentBinding
import com.services.provider.databinding.ActivityCategoryBinding
import com.services.provider.databinding.ActivityChatBinding
import com.services.provider.databinding.ActivityMainBinding
import com.services.provider.databinding.ActivityPendingBinding
import com.services.provider.databinding.ActivityProfileBinding
import com.services.provider.databinding.ActivityServiceDetailsBinding
import com.services.provider.databinding.ActivityServiceDetailsNewBinding
import com.services.provider.databinding.ActivitySkilledMainBinding
import com.services.provider.databinding.ActivityUpdateProfileBinding
import com.services.provider.databinding.ActivityUserMainBinding
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped


@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {

    @Provides
    @ActivityScoped
    fun provideMainActivityBinding(@ActivityContext context: Context): ActivityMainBinding =
        ActivityMainBinding.inflate(LayoutInflater.from(context))

    @Provides
    @ActivityScoped
    fun provideAuthActivityBinding(@ActivityContext context: Context): ActivityAuthBinding =
        ActivityAuthBinding.inflate((context as Activity).layoutInflater)

    @Provides
    @ActivityScoped
    fun provideActivityAddServiceBinding(@ActivityContext context: Context) =
        ActivityAddServiceBinding.inflate((context as Activity).layoutInflater)

    @Provides
    @ActivityScoped
    fun provideActivitySkilledMainBinding(@ActivityContext context: Context) =
        ActivitySkilledMainBinding.inflate((context as Activity).layoutInflater)

    @Provides
    @ActivityScoped
    fun provideActivityBookAppointmentBinding(@ActivityContext context: Context) =
        ActivityBookAppointmentBinding.inflate((context as Activity).layoutInflater)

    @Provides
    @ActivityScoped
    fun provideActivityServiceDetailsBinding(@ActivityContext context: Context) =
        ActivityServiceDetailsBinding.inflate((context as Activity).layoutInflater)

    @Provides
    @ActivityScoped
    fun provideActivityUserMainBinding(@ActivityContext context: Context) =
        ActivityUserMainBinding.inflate((context as Activity).layoutInflater)

    @Provides
    @ActivityScoped
    fun provideActivityChatBinding(@ActivityContext context: Context) =
        ActivityChatBinding.inflate((context as Activity).layoutInflater)

    @Provides
    @ActivityScoped
    fun provideActivityProfileBinding(@ActivityContext context: Context) =
        ActivityProfileBinding.inflate((context as Activity).layoutInflater)
    @Provides
    @ActivityScoped
    fun provideActivityServiceDetailsNewBinding(@ActivityContext context: Context) =
        ActivityServiceDetailsNewBinding.inflate((context as Activity).layoutInflater)
    @Provides
    @ActivityScoped
    fun provideActivityCategory(@ActivityContext context: Context) =
        ActivityCategoryBinding.inflate((context as Activity).layoutInflater)
    @Provides
    @ActivityScoped
    fun provideActivityUpdateProfileBinding(@ActivityContext context: Context) =
        ActivityUpdateProfileBinding.inflate((context as Activity).layoutInflater)
    @Provides
    @ActivityScoped
    fun provideActivityAdminBinding(@ActivityContext context: Context) =
        ActivityAdminBinding.inflate((context as Activity).layoutInflater)
   @Provides
    @ActivityScoped
    fun provideActivityPendingBinding(@ActivityContext context: Context) =
        ActivityPendingBinding.inflate((context as Activity).layoutInflater)

}