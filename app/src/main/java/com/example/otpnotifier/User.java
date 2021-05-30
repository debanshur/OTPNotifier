package com.example.otpnotifier;

import android.os.StrictMode;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class User {
    public static String mobile;
    public String otp;

    public void update_db(String otp){
        System.out.println(mobile + "==>" + otp);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        URL url = null;
        HttpURLConnection http = null;
        String url_str = "https://kvdb.io/ErWL3Zo96gQSinBcfQ2afn/" + mobile;
        try {
            url = new URL(url_str);
            http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String data = otp;

            byte[] out = data.getBytes();

            OutputStream stream = http.getOutputStream();
            stream.write(out);

            System.out.println(http.getResponseCode() + " " + http.getResponseMessage());

        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if (http != null) {
                http.disconnect();
            }
        }

    }
}
