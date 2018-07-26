package com.ccc.lib.notification.util.util;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Date：2018/7/26 11:35
 * <p>
 * author: CodingCodersCode
 */
public class ParcelabelUtil {
    public static byte[] marshall(Parcelable parcelable) {
        Parcel parcel = Parcel.obtain();
        parcelable.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        return parcel.marshall();
    }

    public static Parcel unmarshall(byte[] byteArrayExtra) {
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(byteArrayExtra, 0, byteArrayExtra.length);
        parcel.setDataPosition(0);

        return parcel;
    }
}
