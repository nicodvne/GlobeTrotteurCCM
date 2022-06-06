package com.example.globetrotteur;

import android.widget.Toast;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.FileUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import android.widget.Toast;


public class HttpSender {

    private URL url;

    public HttpSender(String apiUrl) {
        try {
            this.url = new URL(apiUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void sendImage(File file) {
        url = this.url;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();

            String boundary = UUID.randomUUID().toString();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            DataOutputStream request = new DataOutputStream(connection.getOutputStream());

            request.writeBytes("--" + boundary + "\r\n");
            request.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"\r\n\r\n");
            request.write(FileUtils.readFileToByteArray(file));
            request.writeBytes("\r\n");

            request.writeBytes("--" + boundary + "--\r\n");
            request.flush();

        } catch (Exception e) {
            e.printStackTrace();


            return;
        }

    }

}
