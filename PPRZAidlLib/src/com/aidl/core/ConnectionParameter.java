package com.aidl.core;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public final class ConnectionParameter implements Parcelable {

    private final int connectionType;
    private final Bundle paramsBundle;

    public static final Creator<ConnectionParameter> CREATOR = new Creator<ConnectionParameter>() {
        public ConnectionParameter createFromParcel(Parcel source) {
            return new ConnectionParameter(source);
        }

        public ConnectionParameter[] newArray(int size) {
            return new ConnectionParameter[size];
        }
    };

    public ConnectionParameter(int connectionType, Bundle paramsBundle){
        this.connectionType = connectionType;
        this.paramsBundle = paramsBundle;
    }

    private ConnectionParameter(Parcel source) {
        this.connectionType = source.readInt();
        paramsBundle = source.readBundle();
    }

    public int getConnectionType() {
        return connectionType;
    }

    public Bundle getParamsBundle() {
        return paramsBundle;
    }

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof ConnectionParameter)) return false;

        ConnectionParameter that = (ConnectionParameter) o;
        return toString().equals(that.toString());
    }

    @Override
    public int hashCode(){
        return toString().hashCode();
    }

    @Override
    public String toString() {
        String toString = "ConnectionParameter{" +
                "connectionType=" + connectionType +
                ", paramsBundle=[";

        if (paramsBundle != null && !paramsBundle.isEmpty()) {
            boolean isFirst = true;
            for (String key : paramsBundle.keySet()) {
                if (isFirst)
                    isFirst = false;
                else
                    toString += ", ";

                toString += key + "=" + paramsBundle.get(key);
            }
        }

        toString += "]}";
        return toString;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.connectionType);
        dest.writeBundle(paramsBundle);
    }
}
