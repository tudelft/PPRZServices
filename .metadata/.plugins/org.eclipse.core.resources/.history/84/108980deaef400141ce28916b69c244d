package com.aidl.core.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This function serializes the Altitude class
 */
public class Altitude implements Parcelable {
	private double mAltitude;
	private double mTargetAltitude;
	
	public Altitude() {
	}
	
	public Altitude(double altitude, double targetAltitude) {
		mAltitude = altitude;
		mTargetAltitude = targetAltitude;
	}
	
	public Altitude(Parcel in) {
		mAltitude = in.readDouble();
		mTargetAltitude = in.readDouble();
	}
	
	public void setAltitude(double altitude) {
		mAltitude = altitude;
	}
	
	public void setTargetAltitude(double targetAltitude) {
		mTargetAltitude = targetAltitude;
	}
	
	public double getAltitude() {
		return mAltitude;
	}
	
	public double getTargetAltitude() {
		return mTargetAltitude;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(mAltitude);
		dest.writeDouble(mTargetAltitude);
	}
	
	public static final Parcelable.Creator<Altitude> CREATOR = new Parcelable.Creator<Altitude>() {
        public Altitude createFromParcel(Parcel source) {
            return new Altitude(source);
        }

        public Altitude[] newArray(int size) {
            return new Altitude[size];
        }
    };
}