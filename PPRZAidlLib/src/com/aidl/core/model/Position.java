package com.aidl.core.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This function serializes the Position class
 */
public class Position implements Parcelable {
	private byte satVisible;
	
	private int timeStamp;
	
	private int lat;
	
	private int lon;
	
	private int alt;
	
	private int hdg;
	
	public Position() {
	}
	
	public Position(byte satVisible, int timeStamp, int lat, int lon, int alt, int hdg) {
		this.satVisible = satVisible;
		this.timeStamp = timeStamp;
		this.lat = lat;
		this.lon = lon;
		this.alt = alt;
		this.hdg = hdg;
	}
	
	public Position(Parcel in) {
		this.satVisible = in.readByte();
		this.timeStamp = in.readInt();
		this.lat = in.readInt();
		this.lon = in.readInt();
		this.alt = in.readInt();
		this.hdg = in.readInt();
	}
	
	public void setSatVisible(byte satVisible) {
		this.satVisible = satVisible;
	}
	
	public void setTimeStamp(int timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	public void setLat(int lat) {
		this.lat = lat;
	}
	
	public void setLon(int lon) {
		this.lon = lon;
	}
	
	public void setAlt(int alt) {
		this.alt = alt;
	}
	
	public void setHdg(int hdg) {
		this.hdg = hdg;
	}
	
	public byte getSatVisible() {
		return this.satVisible;
	}
	
	public int getTimeStamp() {
		return this.timeStamp;
	}
	
	public int getLat() {
		return this.lat;
	}
	
	public int getLon() {
		return this.lon;
	}

	public int getAlt() {
		return this.alt;
	}
	
	public int getHdg() {
		return this.hdg;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeByte(this.satVisible);
		dest.writeInt(this.timeStamp);
		dest.writeInt(this.lat);
		dest.writeInt(this.lon);
		dest.writeInt(this.alt);
		dest.writeInt(this.hdg);
	}
	
	public static final Parcelable.Creator<Position> CREATOR = new Parcelable.Creator<Position>() {
        public Position createFromParcel(Parcel source) {
            return new Position(source);
        }

        public Position[] newArray(int size) {
            return new Position[size];
        }
    };
}