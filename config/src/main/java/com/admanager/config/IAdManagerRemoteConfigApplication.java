package com.admanager.config;


import java.util.Map;

public interface IAdManagerRemoteConfigApplication {
    Map<String, Object> getRemoteConfigDefaults();
}