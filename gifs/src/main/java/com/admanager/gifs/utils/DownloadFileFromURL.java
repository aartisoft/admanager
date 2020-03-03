package com.admanager.gifs.utils;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadFileFromURL extends AsyncTask<String, String, String> {
    private final OnDownloadListener listener;
    private String downloadUrl, name, path;

    public DownloadFileFromURL(String url, String name, String path, OnDownloadListener listener) {
        downloadUrl = url;
        this.name = name;
        this.path = path;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (listener != null) {
            listener.onDownloadStart(downloadUrl);
        }
    }

    @Override
    protected String doInBackground(String... f_url) {
        int count;
        try {
            URL url = new URL(downloadUrl);
            URLConnection connection = url.openConnection();
            connection.connect();

            int lengthOfFile = connection.getContentLength();

            InputStream input = new BufferedInputStream(url.openStream(), 8192);
            OutputStream output = new FileOutputStream(path + "/" + name);

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                publishProgress("" + (int) ((total * 100) / lengthOfFile));
                output.write(data, 0, count);
            }

            output.flush();

            output.close();
            input.close();
        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }

        return null;
    }

    /**
     * Updating progress bar
     */
    protected void onProgressUpdate(String... progress) {
        // setting progress percentage
        if (listener != null) {
            listener.onDownloadProgress(downloadUrl, Integer.parseInt(progress[0]));
        }
    }

    /**
     * After completing background task Dismiss the progress dialog
     **/
    @Override
    protected void onPostExecute(String file_url) {

        if (listener != null) {
            listener.onDownloadFinished(downloadUrl, path, name);
        }
    }

}