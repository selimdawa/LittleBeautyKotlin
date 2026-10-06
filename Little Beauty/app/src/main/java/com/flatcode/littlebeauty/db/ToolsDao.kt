package com.flatcode.littlebeauty.db

import androidx.room.*
import com.flatcode.littlebeauty.model.Tools
import kotlinx.coroutines.flow.Flow

@Dao
interface ToolsDao {
    @Query("SELECT * FROM tools WHERE id = 0")
    fun getTools(): Flow<Tools?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTools(tools: Tools)

    @Query("DELETE FROM tools")
    suspend fun deleteAllTools()
}