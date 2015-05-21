package com.aidllib;

import com.aidllib.core.ConnectionParameter;
import com.aidllib.core.mavlink.waypoints.Waypoint;
import com.aidllib.IEventListener;
import java.util.List;

interface IMavLinkServiceClient {
    Bundle getAttribute(String type);

    void addEventListener(String id, in IEventListener listener);

    void removeEventListener(String id);

    void connectDroneClient(in ConnectionParameter connParams);

    void disconnectDroneClient();

    void onEvent(String type);

    void requestWpList();

    List<Waypoint> getWpList();
}