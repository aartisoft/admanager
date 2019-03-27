package com.admanager.core;


public class DummyAdapter extends Adapter {

    private Runnable runnable;

    public DummyAdapter(Runnable listener) {
        super(null);
        this.runnable = listener;
    }

    public DummyAdapter() {
        this(null);
    }

    @Override
    protected void init() {
        loaded();
    }

    @Override
    protected void destroy() {

    }

    @Override
    protected String getAdapterName() {
        return "Dummy";
    }

    @Override
    protected void show() {
        if (runnable != null) {
            runnable.run();
        }
        closed();
    }
}