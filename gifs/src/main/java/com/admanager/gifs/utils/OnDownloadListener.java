package com.admanager.gifs.utils;

public interface OnDownloadListener {
    void onDownloadFinished(String url, String path, String name);

    void onDownloadStart(String url);

    void onDownloadProgress(String url, int progress);
}
