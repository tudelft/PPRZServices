package com.aidllib.core.mavlink.waypoints;

public class Waypoint {
	private float lat;
	
	private float lon;
	
	private float alt;
	
	private short seq; // WP index
	
	private byte targetSys;
	
	private byte targetComp;
	
	public Waypoint(float lat, float lon, float alt, short seq, byte targetSys, byte targetComp) {
		this.lat = lat;
		this.lon = lon;
		this.alt = alt;
		this.seq = seq;
		this.targetSys = targetSys;
		this.targetComp = targetComp;
	}
	
	public void setLat(float lat) {
		this.lat = lat;
	}
	
	public void setLon(float lon) {
		this.lon = lon;
	}
	
	public void setAlt(float alt) {
		this.alt = alt;
	}
	
	public void setSeq(short seq) {
		this.seq = seq;
	}
	
	public void setTargetSys(byte targetSys) {
		this.targetSys = targetSys;
	}
	
	public void setTargetComp(byte targetComp) {
		this.targetComp = targetComp;
	}
	
	public float getLat() {
		return this.lat;
	}
	
	public float getLon() {
		return this.lon;
	}
	
	public float getAlt() {
		return this.alt;
	}
	
	public short getSeq() {
		return this.seq;
	}
	
	public byte getTargetSys() {
		return this.targetSys;
	}
	
	public byte getTargetComp() {
		return this.targetComp;
	}
}