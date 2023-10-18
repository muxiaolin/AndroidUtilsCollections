package com.mgtj.airadio.core.utils.location

import android.location.*
import java.lang.Math.floor
import java.text.DecimalFormat
import java.util.*

/**
 * author : 彭林
 * date   : 2020/7/24
 * desc   : 坐标转换工具
 */
object LocationTool {
    //圆周率
    const val pi = 3.1415926535897932384626
    //Krasovsky 1940 (北京54)椭球长半轴
    const val a = 6378245.0
    //椭球的偏心率
    const val ee = 0.00669342162296594323
    //
    const val TWO_MINUTES = 1000 * 60 * 2

    /**
     * GPS坐标 转换成 角度
     * 例如 113.202222 转换成 113°12′8″
     *
     * @param location
     * @return
     */
    fun gpsToDegree(location: Double): String {
        val degree = floor(location)
        val minute_temp = (location - degree) * 60
        val minute = floor(minute_temp)
        //        double second = Math.floor((minute_temp - minute)*60);
        val second = DecimalFormat("#.##").format((minute_temp - minute) * 60)
        return (degree.toInt()).toString() + "°" + minute.toInt() + "′" + second + "″"
    }

    /**
     * 国际 GPS84 坐标系
     * 转换成
     * [国测局坐标系] 火星坐标系 (GCJ-02)
     *
     *
     * World Geodetic System ==> Mars Geodetic System
     *
     * @param lon 经度
     * @param lat 纬度
     * @return GPS实体类
     */
    fun GPS84ToGCJ02(lon: Double, lat: Double): Gps? {
        if (outOfChina(lon, lat)) {
            return null
        }
        var dLat = transformLat(lon - 105.0, lat - 35.0)
        var dLon = transformLon(lon - 105.0, lat - 35.0)
        val radLat = lat / 180.0 * pi
        var magic = Math.sin(radLat)
        magic = 1 - ee * magic * magic
        val sqrtMagic = Math.sqrt(magic)
        dLat = dLat * 180.0 / (a * (1 - ee) / (magic * sqrtMagic) * pi)
        dLon = dLon * 180.0 / (a / sqrtMagic * Math.cos(radLat) * pi)
        val mgLat = lat + dLat
        val mgLon = lon + dLon
        return Gps(mgLon, mgLat)
    }

    /**
     * [国测局坐标系] 火星坐标系 (GCJ-02)
     * 转换成
     * 国际 GPS84 坐标系
     *
     * @param lon 火星经度
     * @param lat 火星纬度
     */
    fun GCJ02ToGPS84(lon: Double, lat: Double): Gps {
        val gps = transform(lon, lat)
        val lontitude = lon * 2 - gps.longitude
        val latitude = lat * 2 - gps.latitude
        return Gps(lontitude, latitude)
    }

    /**
     * 火星坐标系 (GCJ-02)
     * 转换成
     * 百度坐标系 (BD-09)
     *
     * @param ggLon 经度
     * @param ggLat 纬度
     */
    fun GCJ02ToBD09(ggLon: Double, ggLat: Double): Gps {
        val z = Math.sqrt(ggLon * ggLon + ggLat * ggLat) + 0.00002 * Math.sin(ggLat * pi)
        val theta = Math.atan2(ggLat, ggLon) + 0.000003 * Math.cos(ggLon * pi)
        val bdLon = z * Math.cos(theta) + 0.0065
        val bdLat = z * Math.sin(theta) + 0.006
        return Gps(bdLon, bdLat)
    }

    /**
     * 百度坐标系 (BD-09)
     * 转换成
     * 火星坐标系 (GCJ-02)
     *
     * @param bdLon 百度*经度
     * @param bdLat 百度*纬度
     * @return GPS实体类
     */
    fun BD09ToGCJ02(bdLon: Double, bdLat: Double): Gps {
        val x = bdLon - 0.0065
        val y = bdLat - 0.006
        val z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * pi)
        val theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * pi)
        val ggLon = z * Math.cos(theta)
        val ggLat = z * Math.sin(theta)
        return Gps(ggLon, ggLat)
    }

    /**
     * 百度坐标系 (BD-09)
     * 转换成
     * 国际 GPS84 坐标系
     *
     * @param bdLon 百度*经度
     * @param bdLat 百度*纬度
     * @return GPS实体类
     */
    fun BD09ToGPS84(bdLon: Double, bdLat: Double): Gps {
        val gcj02 = BD09ToGCJ02(bdLon, bdLat)
        return GCJ02ToGPS84(gcj02.longitude, gcj02.latitude)
    }

    /**
     * 国际 GPS84 坐标系
     * 转换成
     * 百度坐标系 (BD-09)
     *
     * @param gpsLon  国际 GPS84 坐标系下 的经度
     * @param gpsLat  国际 GPS84 坐标系下 的纬度
     * @return 百度GPS坐标
     */
    fun GPS84ToBD09(gpsLon: Double, gpsLat: Double): Gps {
        val gcj02 = GPS84ToGCJ02(gpsLon, gpsLat)
        return GCJ02ToBD09(gcj02!!.longitude, gcj02.latitude)
    }

    /**
     * 不在中国范围内
     *
     * @param lon 经度
     * @param lat 纬度
     * @return boolean值
     */
    fun outOfChina(lon: Double, lat: Double): Boolean {
        return lon < 72.004 || lon > 137.8347 || lat < 0.8293 || lat > 55.8271
    }

    /**
     * 转化算法
     *
     * @param lon 经度
     * @param lat 纬度
     * @return  GPS信息
     */
    private fun transform(lon: Double, lat: Double): Gps {
        if (outOfChina(lon, lat)) {
            return Gps(lon, lat)
        }
        var dLat = transformLat(lon - 105.0, lat - 35.0)
        var dLon = transformLon(lon - 105.0, lat - 35.0)
        val radLat = lat / 180.0 * pi
        var magic = Math.sin(radLat)
        magic = 1 - ee * magic * magic
        val sqrtMagic = Math.sqrt(magic)
        dLat = dLat * 180.0 / (a * (1 - ee) / (magic * sqrtMagic) * pi)
        dLon = dLon * 180.0 / (a / sqrtMagic * Math.cos(radLat) * pi)
        val mgLat = lat + dLat
        val mgLon = lon + dLon
        return Gps(mgLon, mgLat)
    }

    /**
     * 纬度转化算法
     *
     * @param x x坐标
     * @param y y坐标
     * @return  纬度
     */
    private fun transformLat(x: Double, y: Double): Double {
        var ret =
            -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x))
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0
        ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0
        ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0
        return ret
    }

    /**
     * 经度转化算法
     *
     * @param x  x坐标
     * @param y  y坐标
     * @return 经度
     */
    private fun transformLon(x: Double, y: Double): Double {
        var ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + (0.1
                * Math.sqrt(Math.abs(x)))
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0
        ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0
        ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0 * pi)) * 2.0 / 3.0
        return ret
    }

    /**
     * 是否更好的位置
     *
     * @param newLocation         The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new one
     * @return `true`: 是<br></br>`false`: 否
     */
    fun isBetterLocation(newLocation: Location, currentBestLocation: Location?): Boolean {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true
        }

        // Check whether the new location fix is newer or older
        val timeDelta = newLocation.time - currentBestLocation.time
        val isSignificantlyNewer = timeDelta > TWO_MINUTES
        val isSignificantlyOlder = timeDelta < -TWO_MINUTES
        val isNewer = timeDelta > 0

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false
        }

        // Check whether the new location fix is more or less accurate
        val accuracyDelta = (newLocation.accuracy - currentBestLocation.accuracy).toInt()
        val isLessAccurate = accuracyDelta > 0
        val isMoreAccurate = accuracyDelta < 0
        val isSignificantlyLessAccurate = accuracyDelta > 200

        // Check if the old and new location are from the same provider
        val isFromSameProvider = isSameProvider(newLocation.provider, currentBestLocation.provider)

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true
        } else if (isNewer && !isLessAccurate) {
            return true
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true
        }
        return false
    }

    /**
     * 是否相同的提供者
     *
     * @param provider0 提供者1
     * @param provider1 提供者2
     * @return `true`: 是<br></br>`false`: 否
     */
    fun isSameProvider(provider0: String?, provider1: String?): Boolean {
        return if (provider0 == null) {
            provider1 == null
        } else provider0 == provider1
    }


    fun isMove(location: Location, preLocation: Location?): Boolean {
        val isMove: Boolean
        if (preLocation != null) {
            val speed = location.speed * 3.6
            val distance = location.distanceTo(preLocation).toDouble()
            val compass = Math.abs(preLocation.bearing - location.bearing).toDouble()
            val angle: Double
            angle = if (compass > 180) {
                360 - compass
            } else {
                compass
            }
            isMove = if (speed != 0.0) {
                if (speed < 35 && distance > 3 && distance < 10) {
                    angle > 10
                } else {
                    speed < 40 && distance > 10 && distance < 100 ||
                            speed < 50 && distance > 10 && distance < 100 ||
                            speed < 60 && distance > 10 && distance < 100 ||
                            speed < 9999 && distance > 100
                }
            } else {
                false
            }
        } else {
            isMove = true
        }
        return isMove
    }

}