package com.admanager.core;

public class DummyAdapter extends Adapter {

    public DummyAdapter() {
        super("Dummy", null);
    }

    @Override
    protected void init() {
        loaded();
    }

    @Override
    protected void show() {
        closed();
    }
}