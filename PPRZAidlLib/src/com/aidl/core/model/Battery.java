package com.aidl.core.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This function serializes the Position class
 */
public class Battery implements Parcelable {
	private int battVolt;
	
	private int battLevel;
	
	private int battCurrent;
	
	public Battery() {
	}
	
	public Battery(int battVolt, int battLevel, int battCurrent) {
		this.battVolt = battVolt;
		this.battLevel = battLevel;
		this.battCurrent = battCurrent;
	}
	
	public Battery(Parcel in) {
		this.battVolt = in.readInt();
		this.battLevel = in.readInt();
		this.battCurrent = in.readInt();
	}
	
	public void setBattVolt(int battVolt) {
		this.battVolt = battVolt;
	}
	
	public void setBattLevel(int battLevel) {
		this.battLevel = battLevel;
	}
	
	public void setBattCurrent(int battCurrent) {
		this.battCurrent = battCurrent;
	}
	
	public int getBattVolt() {
		return this.battVolt;
	}
	
	public int getBattLevel() {
		return this.battLevel;
	}
	
	public int getBattCurrent() {
		return this.battCurrent;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.battVolt);
		dest.writeInt(this.battLevel);
		dest.writeInt(this.battCurrent);
	}
	
	public static final Parcelable.Creator<Battery> CREATOR = new Parcelable.Creator<Battery>() {
        public Battery createFromParcel(Parcel source) {
            return new Battery(source);
        }

        public Battery[] newArray(int size) {
            return new Battery[size];
        }
    };
}