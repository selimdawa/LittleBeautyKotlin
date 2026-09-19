package com.flatcode.beautytouch.utils

import com.google.firebase.auth.FirebaseAuth

object DATA {
    //Database
    var USERS = "Users"
    var POSTS = "Posts"
    var SKIN_PRODUCTS = "Skin Products"
    var HAIR_PRODUCTS = "Hair Products"
    var SHOPPING_CENTERS = "ShoppingCenters"
    var M_TOOLS = "Mtools"
    var M_REWARD = "MReward"
    var NAME = "name"
    const val AD_CLICKED = "AdClicked"
    const val AD_LOADED = "AdLoaded"
    const val BANNER_SKIN = "BannerSkin"
    const val BANNER_HAIR = "BannerHair"
    const val BANNER_FAVORITES = "BannerFavorites"
    const val BANNER_SHOPPING_CENTRES = "BannerShoppingCenters"
    const val INTERSTITIAL_HOME = "InterstitialHome"
    var ADS_LOADED_COUNT = "adsLoadedCount"
    var ADS_CLICKED_COUNT = "adsClickedCount"
    var AD_CLICK = "adClick"
    var AD_LOAD = "adLoad"
    var M_AD = "Mad"
    var NULL = "null"
    var VIEWS_COUNT = "viewsCount"
    var SAVES = "Saves"
    var LIKES = "Likes"
    var POST_ID = "postId"
    var HOT_PRODUCT = "HotProduct"
    var CURRENT_VERSION = 2
    var BASIC = "basic"
    var USER_NAME = "username"
    var IMAGE_URL = "imageurl"
    var IMAGE_LINKS = "ImageLinks"
    var EMPTY = ""
    var SPACE = " "
    var ID = "id"
    var IMAGE = "image"
    var PUBLISHER_NAME = "KTWe3PaSUSbv3xulRKSwUgConC92" //id_
    var APP_NAME = "Little Beauty" //app_
    var WHATSAPP = "https://wa.me/message/E2YOU4NVTIEAD1" //Whatsapp_
    var FB_DESINGER = "fb://profile/100037312172320" //Facebook_designer
    var FB_DESINGER_2 = "https://www.facebook.com/mohamed.deeb.50115" //Facebook_designer
    var FB_PROGRAMMER = "fb://profile/100075460898489" //Facebook_programmer
    var FB_PROGRAMMER_2 = "https://www.facebook.com/100075460898489" //Facebook_programmer
    var MIN_SQUARE = 500

    //Shared
    var COLOR_OPTION = "color_option"

    //Other
    var DOT = "."
    val AUTH = FirebaseAuth.getInstance()
    val FIREBASE_USER = AUTH.currentUser
    val FirebaseUserUid = FIREBASE_USER!!.uid

    //Cloudinary
    const val CLOUDINARY_CLOUD_NAME = "j8jsphcf"
    const val CLOUDINARY_UPLOAD_PRESET = "flat_code"
}