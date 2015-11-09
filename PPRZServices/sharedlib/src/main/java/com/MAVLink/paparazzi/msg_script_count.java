        // MESSAGE SCRIPT_COUNT PACKING
package com.MAVLink.paparazzi;
import com.MAVLink.MAVLinkPacket;
import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.Messages.MAVLinkPayload;
        //import android.util.Log;
        
        /**
        * This message is emitted as response to SCRIPT_REQUEST_LIST by the MAV to get the number of mission scripts.
        */
        public class msg_script_count extends MAVLinkMessage{
        
        public static final int MAVLINK_MSG_ID_SCRIPT_COUNT = 183;
        public static final int MAVLINK_MSG_LENGTH = 4;
        private static final long serialVersionUID = MAVLINK_MSG_ID_SCRIPT_COUNT;
        
        
         	/**
        * Number of mission items in the sequence
        */
        public short count;
         	/**
        * System ID
        */
        public byte target_system;
         	/**
        * Component ID
        */
        public byte target_component;
        
        
        /**
        * Generates the payload for a mavlink message for a message of this type
        * @return
        */
        public MAVLinkPacket pack(){
		MAVLinkPacket packet = new MAVLinkPacket();
		packet.len = MAVLINK_MSG_LENGTH;
		packet.sysid = 255;
		packet.compid = 190;
		packet.msgid = MAVLINK_MSG_ID_SCRIPT_COUNT;
        		packet.payload.putShort(count);
        		packet.payload.putByte(target_system);
        		packet.payload.putByte(target_component);
        
		return packet;
        }
        
        /**
        * Decode a script_count message into this class fields
        *
        * @param payload The message to decode
        */
        public void unpack(MAVLinkPayload payload) {
        payload.resetIndex();
        	    this.count = payload.getShort();
        	    this.target_system = payload.getByte();
        	    this.target_component = payload.getByte();
        
        }
        
        /**
        * Constructor for a new message, just initializes the msgid
        */
        public msg_script_count(){
    	msgid = MAVLINK_MSG_ID_SCRIPT_COUNT;
        }
        
        /**
        * Constructor for a new message, initializes the message with the payload
        * from a mavlink packet
        *
        */
        public msg_script_count(MAVLinkPacket mavLinkPacket){
        this.sysid = mavLinkPacket.sysid;
        this.compid = mavLinkPacket.compid;
        this.msgid = MAVLINK_MSG_ID_SCRIPT_COUNT;
        unpack(mavLinkPacket.payload);
        //Log.d("MAVLink", "SCRIPT_COUNT");
        //Log.d("MAVLINK_MSG_ID_SCRIPT_COUNT", toString());
        }
        
              
        /**
        * Returns a string with the MSG name and data
        */
        public String toString(){
    	return "MAVLINK_MSG_ID_SCRIPT_COUNT -"+" count:"+count+" target_system:"+target_system+" target_component:"+target_component+"";
        }
        }
        