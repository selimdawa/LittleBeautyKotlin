package com.flatcode.beautytouch.di

import android.content.Context
import androidx.room.Room
import com.flatcode.beautytouch.db.ADsDao
import com.flatcode.beautytouch.db.AppDatabase
import com.flatcode.beautytouch.db.FavoriteDao
import com.flatcode.beautytouch.db.InterestedDao
import com.flatcode.beautytouch.db.PointsDao
import com.flatcode.beautytouch.db.PostDao
import com.flatcode.beautytouch.db.RewardDao
import com.flatcode.beautytouch.db.ShoppingCenterDao
import com.flatcode.beautytouch.db.SliderDao
import com.flatcode.beautytouch.db.ToolsDao
import com.flatcode.beautytouch.db.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "beauty_touch_db"
        ).build()
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()

    @Provides
    fun providePostDao(database: AppDatabase): PostDao = database.postDao()

    @Provides
    fun provideADsDao(database: AppDatabase): ADsDao = database.adsDao()

    @Provides
    fun provideShoppingCenterDao(database: AppDatabase): ShoppingCenterDao = database.shoppingCenterDao()

    @Provides
    fun provideToolsDao(database: AppDatabase): ToolsDao = database.toolsDao()

    @Provides
    fun provideRewardDao(database: AppDatabase): RewardDao = database.rewardDao()

    @Provides
    fun providePointsDao(database: AppDatabase): PointsDao = database.pointsDao()

    @Provides
    fun provideFavoriteDao(database: AppDatabase): FavoriteDao = database.favoriteDao()

    @Provides
    fun provideInterestedDao(database: AppDatabase): InterestedDao = database.interestedDao()

    @Provides
    fun provideSliderDao(database: AppDatabase): SliderDao = database.sliderDao()
}
