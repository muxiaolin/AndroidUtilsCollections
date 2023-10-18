package core.colin.basic.utils.location;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import com.blankj.utilcode.util.PermissionUtils;

import java.util.List;
import java.util.Locale;

import core.colin.basic.utils.Utils;

/**
 * author : 彭林
 * date   : 2020/7/3
 * desc   :
 */

public final class LocationHelper {
    private static final String TAG = "LocationHelper";
    private Location mLocation;
    private static final int MIN_TIME_BETWEEN_UPDATES = 0;
    private static final int MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;

    private volatile static LocationHelper instance;
    private boolean isEnable = false;

    private LocationHelper() {
    }

    public static LocationHelper getInstance() {
        if (instance == null) {
            instance = new LocationHelper();
        }
        return instance;
    }

    @SuppressLint("MissingPermission")
    public void register(LocationChangeListener changeListener) {
        if (!LocationUtils.isLocationEnabled()) {
            Log.w(TAG, "无法定位，请打开定位服务");
            return;
        }
        if (!PermissionUtils.isGranted(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION)) {
            Log.w(TAG, "定位权限不可用");
            return;
        }
        if (isEnable) {
            unregister();
        }
        isEnable = LocationUtils.register(MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, new LocationUtils.OnLocationChangeListener() {
            @Override
            public void getLastKnownLocation(Location location) {
                mLocation = location;
//                Log.d(TAG, "getLastKnownLocation 经度：" + location.getLongitude());
//                Log.d(TAG, "getLastKnownLocation 纬度：" + location.getLatitude());
                if (changeListener != null) {
                    changeListener.onLocation(location);
                }
            }

            @Override
            public void onLocationChanged(Location location) {
                mLocation = location;
//                Log.d(TAG, "时间：" + location.getTime());
//                Log.d(TAG, "海拔：" + location.getAltitude());
//                Log.d(TAG, "onLocationChanged 经度：" + location.getLongitude());
//                Log.d(TAG, "onLocationChanged 纬度：" + location.getLatitude());
                if (changeListener != null) {
                    changeListener.onLocation(location);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                switch (status) {
                    case LocationProvider.AVAILABLE: //当前GPS状态为可见状态
                        break;
                    case LocationProvider.OUT_OF_SERVICE: //当前GPS状态为服务区外状态
                        break;
                    case LocationProvider.TEMPORARILY_UNAVAILABLE: //当前GPS状态为暂停服务状态
                        break;
                }
            }

        });
    }

    public boolean isEnable() {
        return isEnable;
    }

    @SuppressLint("MissingPermission")
    public void unregister() {
        if (PermissionUtils.isGranted(ACCESS_COARSE_LOCATION)) {
            LocationUtils.unregister();
            isEnable = false;
            mLocation = null;
        }
    }

    /**
     * @return Location--->getLongitude()获取经度/getLatitude()获取纬度
     */
    public Location getLocation() {
        if (mLocation == null) {
            return null;
        }
        return mLocation;
    }

    public String getLocalCity() {
        if (mLocation == null) {
            return "";
        }
        List<Address> result = getAddress(mLocation);

        String city = "";
        if (result != null && result.size() > 0) {
            city = result.get(0).getLocality();//获取城市
        }
        return city;
    }

    public String getAddressStr() {
        if (mLocation == null) {
            return "";
        }
        List<Address> result = getAddress(mLocation);

        String address = "";
        if (result != null && result.size() > 0) {
            address = result.get(0).getAddressLine(0);//获取详细地址
        }
        return address;
    }

    // 获取地址信息
    private List<Address> getAddress(Location location) {
        List<Address> result = null;
        try {
            if (location != null) {
                Geocoder gc = new Geocoder(Utils.getAppContext(), Locale.getDefault());
                result = gc.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public interface LocationChangeListener {

        void onLocation(Location location);

    }


}
