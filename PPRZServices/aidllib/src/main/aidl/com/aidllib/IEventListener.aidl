package com.aidllib;

interface IEventListener {
    void onConnectionFailed();

    void onEvent(String type, int sysId); // for calls by the class that stores the listeners
}