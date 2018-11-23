package com.admanager.core;


public class DummyAdapter extends Adapter {

    public DummyAdapter() {
        super(null);
    }

    public void init() {
        loaded();
    }

    @Override
    public void destroy() {

    }

    @Override
    public void show() {
        closed();
    }
}