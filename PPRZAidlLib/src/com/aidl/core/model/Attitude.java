package com.aidl.core.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This function serializes the Attitude class
 */
public class Attitude implements Parcelable {
	private double mRoll;
	private double mPitch;
	private double mYaw;
	
	public Attitude() {
	}
	
	public Attitude(double roll, double pitch, double yaw) {
		mRoll = roll;
		mPitch = pitch;
		mYaw = yaw;
	}
	
	public Attitude(Parcel in) {
		mRoll = in.readDouble();
		mPitch = in.readDouble();
		mYaw = in.readDouble();
	}
	
	public void setRoll(double roll) {
		mRoll = roll;
	}
	
	public void setPitch(double pitch) {
		mPitch = pitch;
	}
	
	public void setYaw(double yaw) {
		mYaw = yaw;
	}
	
	public double getRoll() {
		return mRoll;
	}
	
	public double getPitch() {
		return mPitch;
	}
	
	public double getYaw() {
		return mYaw;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(mRoll);
		dest.writeDouble(mPitch);
		dest.writeDouble(mYaw);
	}
	
	public static final Parcelable.Creator<Attitude> CREATOR = new Parcelable.Creator<Attitude>() {
        public Attitude createFromParcel(Parcel source) {
            return new Attitude(source);
        }

        public Attitude[] newArray(int size) {
            return new Attitude[size];
        }
    };
}