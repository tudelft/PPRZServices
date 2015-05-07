        // MESSAGE AUTOPILOT_VERSION PACKING
package com.MAVLink.common;
import com.MAVLink.MAVLinkPacket;
import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.Messages.MAVLinkPayload;
        //import android.util.Log;
        
        /**
        * Version and capability of autopilot software
        */
        public class msg_autopilot_version extends MAVLinkMessage{
        
        public static final int MAVLINK_MSG_ID_AUTOPILOT_VERSION = 148;
        public static final int MAVLINK_MSG_LENGTH = 60;
        private static final long serialVersionUID = MAVLINK_MSG_ID_AUTOPILOT_VERSION;
        
        
         	/**
        * bitmask of capabilities (see MAV_PROTOCOL_CAPABILITY enum)
        */
        public long capabilities;
         	/**
        * UID if provided by hardware
        */
        public long uid;
         	/**
        * Firmware version number
        */
        public int flight_sw_version;
         	/**
        * Middleware version number
        */
        public int middleware_sw_version;
         	/**
        * Operating system version number
        */
        public int os_sw_version;
         	/**
        * HW / board version (last 8 bytes should be silicon ID, if any)
        */
        public int board_version;
         	/**
        * ID of the board vendor
        */
        public short vendor_id;
         	/**
        * ID of the product
        */
        public short product_id;
         	/**
        * Custom version field, commonly the first 8 bytes of the git hash. This is not an unique identifier, but should allow to identify the commit using the main version number even for very large code bases.
        */
        public byte flight_custom_version[] = new byte[8];
         	/**
        * Custom version field, commonly the first 8 bytes of the git hash. This is not an unique identifier, but should allow to identify the commit using the main version number even for very large code bases.
        */
        public byte middleware_custom_version[] = new byte[8];
         	/**
        * Custom version field, commonly the first 8 bytes of the git hash. This is not an unique identifier, but should allow to identify the commit using the main version number even for very large code bases.
        */
        public byte os_custom_version[] = new byte[8];
        
        
        /**
        * Generates the payload for a mavlink message for a message of this type
        * @return
        */
        public MAVLinkPacket pack(){
		MAVLinkPacket packet = new MAVLinkPacket();
		packet.len = MAVLINK_MSG_LENGTH;
		packet.sysid = 255;
		packet.compid = 190;
		packet.msgid = MAVLINK_MSG_ID_AUTOPILOT_VERSION;
        		packet.payload.putLong(capabilities);
        		packet.payload.putLong(uid);
        		packet.payload.putInt(flight_sw_version);
        		packet.payload.putInt(middleware_sw_version);
        		packet.payload.putInt(os_sw_version);
        		packet.payload.putInt(board_version);
        		packet.payload.putShort(vendor_id);
        		packet.payload.putShort(product_id);
        		 for (int i = 0; i < flight_custom_version.length; i++) {
                    packet.payload.putByte(flight_custom_version[i]);
                    }
        		 for (int i = 0; i < middleware_custom_version.length; i++) {
                    packet.payload.putByte(middleware_custom_version[i]);
                    }
        		 for (int i = 0; i < os_custom_version.length; i++) {
                    packet.payload.putByte(os_custom_version[i]);
                    }
        
		return packet;
        }
        
        /**
        * Decode a autopilot_version message into this class fields
        *
        * @param payload The message to decode
        */
        public void unpack(MAVLinkPayload payload) {
        payload.resetIndex();
        	    this.capabilities = payload.getLong();
        	    this.uid = payload.getLong();
        	    this.flight_sw_version = payload.getInt();
        	    this.middleware_sw_version = payload.getInt();
        	    this.os_sw_version = payload.getInt();
        	    this.board_version = payload.getInt();
        	    this.vendor_id = payload.getShort();
        	    this.product_id = payload.getShort();
        	     for (int i = 0; i < this.flight_custom_version.length; i++) {
                    this.flight_custom_version[i] = payload.getByte();
                    }
        	     for (int i = 0; i < this.middleware_custom_version.length; i++) {
                    this.middleware_custom_version[i] = payload.getByte();
                    }
        	     for (int i = 0; i < this.os_custom_version.length; i++) {
                    this.os_custom_version[i] = payload.getByte();
                    }
        
        }
        
        /**
        * Constructor for a new message, just initializes the msgid
        */
        public msg_autopilot_version(){
    	msgid = MAVLINK_MSG_ID_AUTOPILOT_VERSION;
        }
        
        /**
        * Constructor for a new message, initializes the message with the payload
        * from a mavlink packet
        *
        */
        public msg_autopilot_version(MAVLinkPacket mavLinkPacket){
        this.sysid = mavLinkPacket.sysid;
        this.compid = mavLinkPacket.compid;
        this.msgid = MAVLINK_MSG_ID_AUTOPILOT_VERSION;
        unpack(mavLinkPacket.payload);
        //Log.d("MAVLink", "AUTOPILOT_VERSION");
        //Log.d("MAVLINK_MSG_ID_AUTOPILOT_VERSION", toString());
        }
        
                              
        /**
        * Returns a string with the MSG name and data
        */
        public String toString(){
    	return "MAVLINK_MSG_ID_AUTOPILOT_VERSION -"+" capabilities:"+capabilities+" uid:"+uid+" flight_sw_version:"+flight_sw_version+" middleware_sw_version:"+middleware_sw_version+" os_sw_version:"+os_sw_version+" board_version:"+board_version+" vendor_id:"+vendor_id+" product_id:"+product_id+" flight_custom_version:"+flight_custom_version+" middleware_custom_version:"+middleware_custom_version+" os_custom_version:"+os_custom_version+"";
        }
        }
        