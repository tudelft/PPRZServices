        // MESSAGE BLOCK_ITEM PACKING
package com.MAVLink.paparazzi;
import com.MAVLink.MAVLinkPacket;
import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.Messages.MAVLinkPayload;
        //import android.util.Log;
        
        /**
        * Message encoding a mission block item. This message is emitted upon a request for the next block item.
        */
        public class msg_block_item extends MAVLinkMessage{
        
        public static final int MAVLINK_MSG_ID_BLOCK_ITEM = 180;
        public static final int MAVLINK_MSG_LENGTH = 54;
        private static final long serialVersionUID = MAVLINK_MSG_ID_BLOCK_ITEM;
        
        
         	/**
        * Sequence
        */
        public short seq;
         	/**
        * System ID
        */
        public byte target_system;
         	/**
        * Component ID
        */
        public byte target_component;
         	/**
        * The name of the mission block
        */
        public byte name[] = new byte[50];
        
        
        /**
        * Generates the payload for a mavlink message for a message of this type
        * @return
        */
        public MAVLinkPacket pack(){
		MAVLinkPacket packet = new MAVLinkPacket();
		packet.len = MAVLINK_MSG_LENGTH;
		packet.sysid = 255;
		packet.compid = 190;
		packet.msgid = MAVLINK_MSG_ID_BLOCK_ITEM;
        		packet.payload.putShort(seq);
        		packet.payload.putByte(target_system);
        		packet.payload.putByte(target_component);
        		 for (int i = 0; i < name.length; i++) {
                    packet.payload.putByte(name[i]);
                    }
        
		return packet;
        }
        
        /**
        * Decode a block_item message into this class fields
        *
        * @param payload The message to decode
        */
        public void unpack(MAVLinkPayload payload) {
        payload.resetIndex();
        	    this.seq = payload.getShort();
        	    this.target_system = payload.getByte();
        	    this.target_component = payload.getByte();
        	     for (int i = 0; i < this.name.length; i++) {
                    this.name[i] = payload.getByte();
                    }
        
        }
        
        /**
        * Constructor for a new message, just initializes the msgid
        */
        public msg_block_item(){
    	msgid = MAVLINK_MSG_ID_BLOCK_ITEM;
        }
        
        /**
        * Constructor for a new message, initializes the message with the payload
        * from a mavlink packet
        *
        */
        public msg_block_item(MAVLinkPacket mavLinkPacket){
        this.sysid = mavLinkPacket.sysid;
        this.compid = mavLinkPacket.compid;
        this.msgid = MAVLINK_MSG_ID_BLOCK_ITEM;
        unpack(mavLinkPacket.payload);
        //Log.d("MAVLink", "BLOCK_ITEM");
        //Log.d("MAVLINK_MSG_ID_BLOCK_ITEM", toString());
        }
        
               /**
                        * Sets the buffer of this message with a string, adds the necessary padding
                        */
                        public void setName(String str) {
                        int len = Math.min(str.length(), 50);
                        for (int i=0; i<len; i++) {
                        name[i] = (byte) str.charAt(i);
                        }
                        for (int i=len; i<50; i++) {			// padding for the rest of the buffer
                        name[i] = 0;
                        }
                        }
                        
                        /**
                        * Gets the message, formated as a string
                        */
                        public String getName() {
                        String result = "";
                        for (int i = 0; i < 50; i++) {
                        if (name[i] != 0)
                        result = result + (char) name[i];
                        else
                        break;
                        }
                        return result;
                        
                        } 
        /**
        * Returns a string with the MSG name and data
        */
        public String toString(){
    	return "MAVLINK_MSG_ID_BLOCK_ITEM -"+" seq:"+seq+" target_system:"+target_system+" target_component:"+target_component+" name:"+name+"";
        }
        }
        