package com.aidl;

import com.aidl.core.ConnectionParameter;
import com.aidl.IEventListener;

interface IMavLinkServiceClient {
    Bundle getAttribute(String type);

    void addEventListener(String id, in IEventListener listener);

    void removeEventListener(String id);

    void connectDroneClient(in ConnectionParameter connParams);

    void disconnectDroneClient();

    void onEvent(String type);
}