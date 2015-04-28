package com.aidl;

interface IEventListener {
    void onConnectionFailed();

    void onEvent(String type);
}