package com.aidllib.core.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This function serializes the Altitude class
 */
public class Altitude implements Parcelable {
	private double mAltitude;
	private double mTargetAltitude;
	private double mAGL;
	
	public Altitude() {
	}
	
	public Altitude(double altitude, double targetAltitude, double AGL) {
		mAltitude = altitude;
		mTargetAltitude = targetAltitude;
		mAGL = AGL;
	}
	
	public Altitude(Parcel in) {
		mAltitude = in.readDouble();
		mTargetAltitude = in.readDouble();
        mAGL = in.readDouble();
	}
	
	public void setAltitude(double altitude) {
		mAltitude = altitude;
	}
	
	public void setTargetAltitude(double targetAltitude) {
		mTargetAltitude = targetAltitude;
	}

	public void setAGL(double AGL) {mAGL = AGL; }
	
	public double getAltitude() {
		return mAltitude;
	}
	
	public double getTargetAltitude() {
		return mTargetAltitude;
	}

	public double getAGL() {return mAGL; }

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(mAltitude);
		dest.writeDouble(mTargetAltitude);
        dest.writeDouble(mAGL);
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