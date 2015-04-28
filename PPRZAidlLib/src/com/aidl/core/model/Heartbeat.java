package com.aidl.core.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This function serializes the Heartbeat class
 */
public class Heartbeat implements Parcelable {
	private byte mSysId;
	private byte mCompId;
	
	public Heartbeat() {
	}
	
	public Heartbeat(byte sysId, byte compId) {
		mSysId = sysId;
		mCompId = compId;
	}
	
	public Heartbeat(Parcel in) {
		mSysId = in.readByte();
		mCompId = in.readByte();
	}
	
	public void setSysId(byte sysId) {
		mSysId = sysId;
	}
	
	public void setCompId(byte compId) {
		mCompId = compId;
	}
	
	public byte getSysId() {
		return mSysId;
	}
	
	public byte getCompId() {
		return mCompId;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeByte(mSysId);
		dest.writeByte(mCompId);
	}
	
	public static final Parcelable.Creator<Heartbeat> CREATOR = new Parcelable.Creator<Heartbeat>() {
        public Heartbeat createFromParcel(Parcel source) {
            return new Heartbeat(source);
        }

        public Heartbeat[] newArray(int size) {
            return new Heartbeat[size];
        }
    };
}