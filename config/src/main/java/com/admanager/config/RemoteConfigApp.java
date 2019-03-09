package com.admanager.config;

import java.util.HashMap;
import java.util.Map;

public class RemoteConfigApp {
    private static RemoteConfigApp INSTANCE;
    private Map<String, Object> defaults;

    private RemoteConfigApp(Map<String, Object> defaults) {
        this.defaults = defaults;
    }

    static RemoteConfigApp getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("You should initialize RemoteConfigApp!");
        }
        return INSTANCE;
    }

    private static RemoteConfigApp init(RemoteConfigApp remoteConfigApp) {
        INSTANCE = remoteConfigApp;
        return INSTANCE;
    }

    Map<String, Object> getDefaults() {
        return defaults;
    }

    public static class Builder {

        private Map<String, Object> defaults;

        public Builder(Map<String, Object> defaults) {
            this.defaults = defaults;
        }

        public void build() {
            if (defaults == null) {
                defaults = new HashMap<>();
            }

            RemoteConfigApp.init(new RemoteConfigApp(defaults));
        }

    }
}