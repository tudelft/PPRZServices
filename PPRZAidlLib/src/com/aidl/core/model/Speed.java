package com.aidl.core.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This function serializes the Altitude class
 */
public class Speed implements Parcelable {
	private double mGroundSpeed;
	private double mAirSpeed;
	private double mClimbSpeed;
	private double mTargetSpeed;
	
	public Speed() {
	}
	
	public Speed(double groundSpeed, double airSpeed, double climbSpeed, double targetSpeed) {
		mGroundSpeed = groundSpeed;
		mAirSpeed = airSpeed;
		mClimbSpeed = climbSpeed;
		mTargetSpeed = targetSpeed;
	}
	
	public Speed(Parcel in) {
		mGroundSpeed = in.readDouble();
		mAirSpeed = in.readDouble();
		mClimbSpeed = in.readDouble();
		mTargetSpeed = in.readDouble();
	}
	
	public void setGroundSpeed(double groundSpeed) {
		mGroundSpeed = groundSpeed;
	}
	
	public void setAirspeed(double airSpeed) {
		mAirSpeed = airSpeed;
	}
	
	public void setClimbSpeed(double climbSpeed) {
		mClimbSpeed = climbSpeed;
	}
	
	public void setTargetSpeed(double targetSpeed) {
		mTargetSpeed = targetSpeed;
	}
	
	public double getGroundSpeed() {
		return mGroundSpeed;
	}
	
	public double getAirspeed() {
		return mAirSpeed;
	}
	
	public double getClimbSpeed() {
		return mClimbSpeed;
	}

	public double getTargetSpeed() {
		return mTargetSpeed;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(mGroundSpeed);
		dest.writeDouble(mAirSpeed);
		dest.writeDouble(mClimbSpeed);
		dest.writeDouble(mTargetSpeed);
	}
	
	public static final Parcelable.Creator<Speed> CREATOR = new Parcelable.Creator<Speed>() {
        public Speed createFromParcel(Parcel source) {
            return new Speed(source);
        }

        public Speed[] newArray(int size) {
            return new Speed[size];
        }
    };
}