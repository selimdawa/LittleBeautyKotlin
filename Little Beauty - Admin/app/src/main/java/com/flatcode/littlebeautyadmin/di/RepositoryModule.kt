package com.flatcode.littlebeautyadmin.di

import com.flatcode.littlebeautyadmin.repository.ADsRepository
import com.flatcode.littlebeautyadmin.repository.AuthRepository
import com.flatcode.littlebeautyadmin.repository.HotProductRepository
import com.flatcode.littlebeautyadmin.repository.MainRepository
import com.flatcode.littlebeautyadmin.repository.PostRepository
import com.flatcode.littlebeautyadmin.repository.ShoppingRepository
import com.flatcode.littlebeautyadmin.repository.SliderRepository
import com.flatcode.littlebeautyadmin.repository.ToolsRepository
import com.flatcode.littlebeautyadmin.repository.UserRepository
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