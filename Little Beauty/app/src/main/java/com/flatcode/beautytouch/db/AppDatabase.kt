package com.flatcode.beautytouch.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.flatcode.beautytouch.model.ADs
import com.flatcode.beautytouch.model.FavoriteEntity
import com.flatcode.beautytouch.model.InterestedEntity
import com.flatcode.beautytouch.model.Points
import com.flatcode.beautytouch.model.Post
import com.flatcode.beautytouch.model.Reward
import com.flatcode.beautytouch.model.ShoppingCenter
import com.flatcode.beautytouch.model.SliderEntity
import com.flatcode.beautytouch.model.Tools
import com.flatcode.beautytouch.model.User

@Database(
    entities = [
        User::class, Post::class, ADs::class, ShoppingCenter::class, Tools::class, Reward::class, Points::class,
        FavoriteEntity::class, InterestedEntity::class, SliderEntity::class
    ],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ],
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
    abstract fun adsDao(): ADsDao
    abstract fun shoppingCenterDao(): ShoppingCenterDao
    abstract fun toolsDao(): ToolsDao
    abstract fun rewardDao(): RewardDao
    abstract fun pointsDao(): PointsDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun interestedDao(): InterestedDao
    abstract fun sliderDao(): SliderDao
}
