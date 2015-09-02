package com.aidllib;

import com.aidllib.core.ConnectionParameter;
import com.aidllib.IEventListener;
import java.util.List;

interface IMavLinkServiceClient {

    Bundle getAttribute(String type, int sysId); // the listener can retrieve data

    void addEventListener(String id, in IEventListener listener);

    void removeEventListener(String id);

    void connectDroneClient(in ConnectionParameter connParams);

    void disconnectDroneClient();

    void onEvent(String type, int sysId); // for calls by the listener

    void onCallback(in Bundle carrier); // for calls by the listener with an optional argument

    //void requestWpList();

    //List<Waypoint> getWpList();

    //void requestMissionBlockList(); --> onCallback

    //List<String> getMissionBlockList();

    //void onBlockSelected(int id); --> onCallback
}