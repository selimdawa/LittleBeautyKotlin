package com.flatcode.beautytouch.utils

import com.flatcode.beautytouch.model.ADs
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

fun String?.adCount(bannerName: String?, key: String?) {
    if (this == null || bannerName == null || key == null) return
    val ref = FirebaseDatabase.getInstance().getReference(DATA.M_AD).child(this).child(bannerName)
    ref.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val adCount = snapshot.child(key).getValue(Long::class.java) ?: 0L
            val newAdCount = adCount + 1
            val hashMap = HashMap<String, Any>()
            hashMap[key] = newAdCount
            snapshot.ref.updateChildren(hashMap).addOnCompleteListener {
                this@adCount.adName(bannerName)
            }
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}

fun String?.adUserCount(key: String?, number: Int) {
    if (this == null || key == null) return
    val ref = FirebaseDatabase.getInstance().getReference(DATA.USERS).child(this)
    ref.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val adCount = snapshot.child(key).getValue(Long::class.java) ?: 0L
            val newAdCount = adCount + number
            val hashMap = HashMap<String, Any>()
            hashMap[key] = newAdCount
            snapshot.ref.updateChildren(hashMap)
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}

fun String?.adName(bannerName: String?) {
    if (this == null || bannerName == null) return
    val ref = FirebaseDatabase.getInstance().getReference(DATA.M_AD).child(this).child(bannerName)
    ref.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val item = snapshot.getValue(ADs::class.java)
            if (item == null || item.name.isEmpty()) {
                val hashMap = HashMap<String, Any?>()
                hashMap[DATA.NAME] = bannerName
                snapshot.ref.updateChildren(hashMap)
            }
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}