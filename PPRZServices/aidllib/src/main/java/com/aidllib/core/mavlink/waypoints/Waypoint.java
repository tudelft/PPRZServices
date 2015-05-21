package com.aidllib.core.mavlink.waypoints;

import android.os.Parcel;
import android.os.Parcelable;

public class Waypoint implements Parcelable {
	private float mLat;
	
	private float mLon;
	
	private float mAlt;
	
	private int mSeq; // WP index
	
	private byte mTargetSys;
	
	private byte mTargetComp;
	
	public Waypoint(float lat, float lon, float alt, int seq, byte targetSys, byte targetComp) {
		mLat = lat;
		mLon = lon;
		mAlt = alt;
		mSeq = seq;
		mTargetSys = targetSys;
		mTargetComp = targetComp;
	}

	public Waypoint(Parcel in) {
		mLat = in.readFloat();
		mLon = in.readFloat();
		mAlt = in.readFloat();
		mSeq = in.readInt();
		mTargetSys = in.readByte();
		mTargetComp = in.readByte();
	}
	
	public void setLat(float lat) {
		mLat = lat;
	}
	
	public void setLon(float lon) {
		mLon = lon;
	}
	
	public void setAlt(float alt) {
		mAlt = alt;
	}
	
	public void setSeq(short seq) {
		mSeq = seq;
	}
	
	public void setTargetSys(byte targetSys) {
		mTargetSys = targetSys;
	}
	
	public void setTargetComp(byte targetComp) {
		mTargetComp = targetComp;
	}
	
	public float getLat() {
		return mLat;
	}
	
	public float getLon() {
		return mLon;
	}
	
	public float getAlt() {
		return mAlt;
	}
	
	public int getSeq() {
		return mSeq;
	}
	
	public byte getTargetSys() {
		return mTargetSys;
	}
	
	public byte getTargetComp() {
		return mTargetComp;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeFloat(mLat);
		dest.writeFloat(mLon);
		dest.writeFloat(mAlt);
		dest.writeInt(mSeq);
		dest.writeByte(mTargetSys);
		dest.writeByte(mTargetComp);
	}

	public static final Parcelable.Creator<Waypoint> CREATOR = new Parcelable.Creator<Waypoint>() {
		public Waypoint createFromParcel(Parcel source) {
			return new Waypoint(source);
		}

		public Waypoint[] newArray(int size) {
			return new Waypoint[size];
		}
	};
}