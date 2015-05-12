package com.aidllib;

interface IEventListener {
    void onConnectionFailed();

    void onEvent(String type);
}