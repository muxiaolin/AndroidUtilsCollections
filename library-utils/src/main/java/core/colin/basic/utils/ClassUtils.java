package core.colin.basic.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;


import com.blankj.utilcode.util.LogUtils;

import java.lang.reflect.Method;

/**
 * @author 彭林
 * @version v1.0
 * @date 2020/8/12
 */
public class ClassUtils {

    public static Class findClassInManifest(Context context, Class clazz) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SERVICES);
            ServiceInfo[] serviceInfos = packageInfo.services;
            if (serviceInfos != null && serviceInfos.length > 0) {
                Class aClass;
                for (int i = 0; i < serviceInfos.length; ++i) {
                    try {
                        aClass = Class.forName(serviceInfos[i].name);
                    } catch (Throwable e) {
                        aClass = null;
                    }

                    if (aClass != null && aClass == clazz) {
                        return aClass;
                    }
                }
            }
        } catch (Throwable e) {
            LogUtils.d(" findClassInManifest error = " + e.toString());
        }

        return null;
    }

    public static Class findImplClassInManifest(Context context, Class clazz) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SERVICES);
            ServiceInfo[] serviceInfos = packageInfo.services;
            if (serviceInfos != null && serviceInfos.length > 0) {
                Class aClass;
                for (int i = 0; i < serviceInfos.length; ++i) {
                    try {
                        aClass = Class.forName(serviceInfos[i].name);
                    } catch (Throwable e) {
                        aClass = null;
                    }

                    if (aClass != null && aClass.getSuperclass() == clazz) {
                        return aClass;
                    }
                }
            }
        } catch (Throwable e) {
            LogUtils.d(" findImplClassInManifest error = " + e.toString());
        }

        return null;
    }

    public static ServiceInfo findImplServiceInfoInManifest(Context context, Class clazz) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SERVICES);
            ServiceInfo[] serviceInfos = packageInfo.services;
            if (serviceInfos != null && serviceInfos.length > 0) {
                for (int i = 0; i < serviceInfos.length; ++i) {
                    Class aClass;
                    try {
                        aClass = Class.forName(serviceInfos[i].name);
                    } catch (Throwable var10) {
                        aClass = null;
                    }

                    if (aClass != null && aClass.getSuperclass() == clazz) {
                        return serviceInfos[i];
                    }
                }
            }
        } catch (Throwable e) {
            LogUtils.d(" findImplClassInManifest error = " + e.toString());
        }

        return null;
    }

    public static Method m1405a(Class<?> cls, String str, Class<?>... clsArr) throws NoSuchMethodException {
        Method a = m1406a(cls.getDeclaredMethods(), str, clsArr);
        if (a != null) {
            a.setAccessible(true);
            return a;
        } else if (cls.getSuperclass() != null) {
            return m1405a((Class<?>) cls.getSuperclass(), str, clsArr);
        } else {
            throw new NoSuchMethodException();
        }
    }

    private static Method m1406a(Method[] methodArr, String str, Class<?>[] clsArr) {
        if (str == null) {
            throw new NullPointerException("Method name must not be null.");
        }
        for (Method method : methodArr) {
            if (method.getName().equals(str) && m1408a((Class<?>[]) method.getParameterTypes(), clsArr)) {
                return method;
            }
        }
        return null;
    }

    private static boolean m1408a(Class<?>[] clsArr, Class<?>[] clsArr2) {
        boolean z = true;
        if (clsArr == null) {
            return clsArr2 == null || clsArr2.length == 0;
        }
        if (clsArr2 == null) {
            if (clsArr.length != 0) {
                z = false;
            }
            return z;
        } else if (clsArr.length != clsArr2.length) {
            return false;
        } else {
            for (int i = 0; i < clsArr.length; i++) {
                if (clsArr2[i] != null && !clsArr[i].isAssignableFrom(clsArr2[i])) {
                    return false;
                }
            }
            return true;
        }
    }
}
