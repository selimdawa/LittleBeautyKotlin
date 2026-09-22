package com.flatcode.beautytouchadmin.di

import android.content.Context
import androidx.room.Room
import com.flatcode.beautytouchadmin.db.ADsDao
import com.flatcode.beautytouchadmin.db.AppDatabase
import com.flatcode.beautytouchadmin.db.MainDao
import com.flatcode.beautytouchadmin.db.PointsDao
import com.flatcode.beautytouchadmin.db.PostDao
import com.flatcode.beautytouchadmin.db.RewardDao
import com.flatcode.beautytouchadmin.db.ShoppingCenterDao
import com.flatcode.beautytouchadmin.db.ToolsDao
import com.flatcode.beautytouchadmin.db.UserDao
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context, AppDatabase::class.java, "beauty_touch_admin_db"
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
