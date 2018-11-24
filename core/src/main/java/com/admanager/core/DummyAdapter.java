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
    protected void destroy() {

    }

    @Override
    protected void show() {
        closed();
    }
}