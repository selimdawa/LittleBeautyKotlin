package com.flatcode.beautytouchadmin.utils

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object FirebaseUtils {
    val database: FirebaseDatabase
        get() = FirebaseDatabase.getInstance()

    fun getReference(path: String): DatabaseReference {
        return database.getReference(path)
    }
}