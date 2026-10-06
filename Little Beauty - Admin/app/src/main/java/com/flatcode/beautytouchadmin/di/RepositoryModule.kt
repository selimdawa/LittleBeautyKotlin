package com.flatcode.beautytouchadmin.di

import com.flatcode.beautytouchadmin.repository.ADsRepository
import com.flatcode.beautytouchadmin.repository.AuthRepository
import com.flatcode.beautytouchadmin.repository.HotProductRepository
import com.flatcode.beautytouchadmin.repository.MainRepository
import com.flatcode.beautytouchadmin.repository.PostRepository
import com.flatcode.beautytouchadmin.repository.ShoppingRepository
import com.flatcode.beautytouchadmin.repository.SliderRepository
import com.flatcode.beautytouchadmin.repository.ToolsRepository
import com.flatcode.beautytouchadmin.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(database: FirebaseDatabase): UserRepository {
        return UserRepository(database)
    }

    @Provides
    @Singleton
    fun provideMainRepository(database: FirebaseDatabase): MainRepository {
        return MainRepository(database)
    }

    @Provides
    @Singleton
    fun providePostRepository(database: FirebaseDatabase): PostRepository {
        return PostRepository(database)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(auth: FirebaseAuth): AuthRepository {
        return AuthRepository(auth)
    }

    @Provides
    @Singleton
    fun provideShoppingRepository(database: FirebaseDatabase): ShoppingRepository {
        return ShoppingRepository(database)
    }

    @Provides
    @Singleton
    fun provideToolsRepository(database: FirebaseDatabase): ToolsRepository {
        return ToolsRepository(database)
    }

    @Provides
    @Singleton
    fun provideADsRepository(database: FirebaseDatabase): ADsRepository {
        return ADsRepository(database)
    }

    @Provides
    @Singleton
    fun provideHotProductRepository(database: FirebaseDatabase): HotProductRepository {
        return HotProductRepository(database)
    }

    @Provides
    @Singleton
    fun provideSliderRepository(database: FirebaseDatabase): SliderRepository {
        return SliderRepository(database)
    }
}