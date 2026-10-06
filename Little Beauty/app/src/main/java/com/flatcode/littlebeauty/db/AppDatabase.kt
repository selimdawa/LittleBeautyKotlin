package com.flatcode.littlebeauty.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.flatcode.littlebeauty.model.ADs
import com.flatcode.littlebeauty.model.FavoriteEntity
import com.flatcode.littlebeauty.model.HotProductEntity
import com.flatcode.littlebeauty.model.InterestedEntity
import com.flatcode.littlebeauty.model.Points
import com.flatcode.littlebeauty.model.Post
import com.flatcode.littlebeauty.model.Reward
import com.flatcode.littlebeauty.model.ShoppingCenter
import com.flatcode.littlebeauty.model.SliderEntity
import com.flatcode.littlebeauty.model.Tools
import com.flatcode.littlebeauty.model.User

@Database(
    entities = [
        User::class, Post::class, ADs::class, ShoppingCenter::class, Tools::class, Reward::class, Points::class,
        FavoriteEntity::class, InterestedEntity::class, SliderEntity::class, HotProductEntity::class
    ],
    version = 3,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3)
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
    abstract fun hotProductDao(): HotProductDao
}
