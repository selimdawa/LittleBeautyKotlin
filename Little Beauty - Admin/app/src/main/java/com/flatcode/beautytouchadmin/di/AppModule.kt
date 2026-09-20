package com.flatcode.beautytouchadmin.di

import android.content.Context
import androidx.room.Room
import com.flatcode.beautytouchadmin.db.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils

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
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()

    @Provides
    @Singleton
    fun provideCloudinary(): Cloudinary = Cloudinary(
        ObjectUtils.asMap(
            "cloud_name", "YOUR_CLOUD_NAME",
            "api_key", "YOUR_API_KEY",
            "api_secret", "YOUR_API_SECRET"
        )
    )

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "beauty_touch_admin_db"
        ).build()
    }

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    fun providePostDao(db: AppDatabase): PostDao = db.postDao()

    @Provides
    fun provideADsDao(db: AppDatabase): ADsDao = db.adsDao()

    @Provides
    fun provideToolsDao(db: AppDatabase): ToolsDao = db.toolsDao()

    @Provides
    fun provideShoppingCenterDao(db: AppDatabase): ShoppingCenterDao = db.shoppingCenterDao()

    @Provides
    fun provideRewardDao(db: AppDatabase): RewardDao = db.rewardDao()

    @Provides
    fun providePointsDao(db: AppDatabase): PointsDao = db.pointsDao()

    @Provides
    fun provideMainDao(db: AppDatabase): MainDao = db.mainDao()
}
