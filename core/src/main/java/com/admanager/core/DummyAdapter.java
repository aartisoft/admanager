package com.admanager.core;

public class DummyAdapter extends Adapter {

    public DummyAdapter() {
        super(null);
    }

    @Override
    protected void init() {
        loaded();
    }

    @Override
    protected String getAdapterName() {
        return "Dummy";
    }

    @Override
    protected void show() {
        closed();
    }
}