@file:Suppress("SpellCheckingInspection")

package com.flatcode.beautytouchadmin.utils

import com.google.firebase.auth.FirebaseAuth

object DATA {
    const val PROFILE_ID = "profileId"
    const val USERS = "Users"
    const val POSTS = "Posts"
    const val LIKES = "Likes"
    const val POST_ID = "postId"
    const val SHOPPING_CENTER_ID = "shoppingCenterId"
    const val SAVES = "Saves"
    const val M_AD = "Mad"
    const val NAME = "name"
    const val STARTED = "started"
    const val AD_CLICK = "adClick"
    const val AD_LOAD = "adLoad"
    const val SLIDER_SHOW = "ImageLinks"
    var MIN_SLIDER_X = 680
    var MIN_SLIDER_Y = 360
    const val EMPTY = ""
    const val SHOPPING_CENTERS = "ShoppingCenters"
    const val HOT_PRODUCT = "HotProduct"
    const val M_TOOLS = "Mtools"
    const val BEAUTY_TOUCH = "Little Beauty"
    const val PUBLISHER = "KTWe3PaSUSbv3xulRKSwUgConC92"
    const val APP_NAME = "Little Beauty"
    const val ALL = "All"
    const val SKIN = "Skin"
    const val HAIR = "Hair"

    //Other
    val AUTH: FirebaseAuth = FirebaseAuth.getInstance()
    val FIREBASE_USER = AUTH.currentUser
    val FirebaseUserUid = FIREBASE_USER!!.uid

    //Database
    var SKIN_PRODUCTS = "Skin Products"
    var HAIR_PRODUCTS = "Hair Products"

    var BASIC = "basic"
    var USER_NAME = "username"
    var IMAGE_URL = "imageurl"

    var MIN_SQUARE = 500

    //Shared

    //Cloudinary
    const val CLOUDINARY_CLOUD_NAME = "j8jsphcf"
}
