package com.aidllib.core.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This function serializes the State class
 */
public class State implements Parcelable {
	private boolean armed;
	private boolean isFlying;

	public State() {
	}

	public State(boolean armed, boolean isFlying) {
		this.armed = armed;
		this.isFlying = isFlying;
	}

	public State(Parcel in) {
		armed = in.readByte() != 0;     //== true if byte != 0
		isFlying = in.readByte() != 0;	//== true if byte != 0
	}

	public void setIsFlying(boolean newState) {
		this.isFlying = newState;
	}

	public void setArmed(boolean newState) {
		this.armed = newState;
	}

	public boolean isArmed() {
		return armed;
	}

	public boolean isFlying() {
		return isFlying;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeByte((byte) (armed ? 1 : 0));		//if armed == true, byte == 1
		dest.writeByte((byte) (isFlying ? 1 : 0));	//if isFlying == true, byte == 1
	}

	public static final Parcelable.Creator<State> CREATOR = new Parcelable.Creator<State>() {
		public State createFromParcel(Parcel source) {
			return new State(source);
		}

		public State[] newArray(int size) {
			return new State[size];
		}
	};
}