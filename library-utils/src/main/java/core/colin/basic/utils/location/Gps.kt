package com.mgtj.airadio.core.utils.location

/**
 * author : 彭林
 * date   : 2020/7/24
 * desc   :
 */
class Gps {
    var latitude = 0.0
    var longitude = 0.0

    constructor()
    constructor(mLongitude: Double, mLatitude: Double) {
        latitude = mLatitude
        longitude = mLongitude
    }

    override fun toString(): String {
        return "$longitude,$latitude"
    }
}