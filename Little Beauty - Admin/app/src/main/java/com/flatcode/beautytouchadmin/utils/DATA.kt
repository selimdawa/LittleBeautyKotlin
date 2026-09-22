package com.flatcode.beautytouchadmin.utils

import com.google.firebase.auth.FirebaseAuth

object DATA {
    const val PROFILE_ID = "profileId"
    const val USERS = "Users"
    const val POSTS = "Posts"
    const val ADS = "Ad"
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
    const val AD_CLICKED = "AdClicked"
    const val AD_LOADED = "AdLoaded"
    const val BANNER_SKIN = "BannerSkin"
    const val BANNER_HAIR = "BannerHair"
    const val BANNER_FAVORITES = "BannerFavorites"
    const val BANNER_SHOPPING_CENTRES = "BannerShoppingCenters"
    const val INTERSTITIAL_HOME = "InterstitialHome"
    var MIN_SLIDER_X = 680
    var MIN_SLIDER_Y = 360
    const val EMPTY = ""
    const val SPACE = " "
    const val VIEWS = "views"
    const val SHOPPING_CENTERS = "ShoppingCenters"
    const val SLIDERS = "ImageLinks"
    const val HOT_PRODUCT = "HotProduct"
    const val M_TOOLS = "Mtools"
    const val BEAUTY_TOUCH = "Little Beauty"
    const val PUBLISHER = "KTWe3PaSUSbv3xulRKSwUgConC92"
    const val APP_NAME = "Little Beauty"
    const val ALL = "All"
    const val SKIN = "Skin"
    const val HAIR = "Hair"
    const val DOT = "."

    //Other
    val AUTH: FirebaseAuth = FirebaseAuth.getInstance()
    val FIREBASE_USER = AUTH.currentUser
    val FirebaseUserUid = FIREBASE_USER!!.uid

    //Database
    var SKIN_PRODUCTS = "Skin Products"
    var HAIR_PRODUCTS = "Hair Products"

    var M_PREWARD = "MPreward"
    var M_POINTS = "Mpoints"

    var CURRENT_VERSION = 2
    var BASIC = "basic"
    var USER_NAME = "username"
    var IMAGE_URL = "imageurl"

    var ID = "id"
    var IMAGE = "image"
    var MIN_SQUARE = 500

    //Shared
    var COLOR_OPTION = "color_option"
    
    const val WEBSITE = "https://flat-code.com"
    const val FB_ID = "/flatcode"

    //Cloudinary
    const val CLOUDINARY_CLOUD_NAME = "j8jsphcf"
    const val CLOUDINARY_UPLOAD_PRESET = "flat_code"
}
