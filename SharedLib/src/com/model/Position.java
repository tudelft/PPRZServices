package com.model;

public class Position {
	private byte satVisible = 0;
	
	private int timeStamp = 0;
	
	/**
	 * Global position in latitude, longitude and altitude.
	 * Units lat,lon: degrees*1e7
	 * Units alt: milimeters above reference ellipsoid
	 */
	private int lat;
	
	private int lon;
	
	private int alt;
	
	/**
     * Compass heading in degrees * 100, 0.0..359.99 degrees. If unknown, set to: MAX_VALUE
     */
	private short hdg = Short.MAX_VALUE;
	
	public byte getSatVisible() {
		return satVisible;
	}
	
	public int getTimeStamp() {
		return timeStamp;
	}
	
	public int getLat() {
		return lat;
	}
	
	public int getLon() {
		return lon;
	}
	
	public int getAlt() {
		return alt;
	}
	
	public int getHdg() {
		return hdg;
	}
	
	public void setSatVisible(byte satVisible) {
		this.satVisible = satVisible;
	}
	
	public void setLlaHdg(int lat, int lon, int alt, short hdg) {
		this.lat = lat;
		this.lon = lon;
		this.alt = alt;
		this.hdg = hdg;
	}
}