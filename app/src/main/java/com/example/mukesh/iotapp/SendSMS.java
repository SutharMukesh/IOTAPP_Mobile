package com.example.mukesh.iotapp;

/**
 * Created by mukesh on 16/4/17.
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class SendSMS {
    public String SendSms() {

        try {
            // Construct data
            String user = "username=" + URLEncoder.encode("mukeshsuthar336@address.com", "UTF-8");
            String hash = "&hash=" + URLEncoder.encode("2d6ee050c0b67bf5a0933c2215d3942df184f3ea2b1780b980676078d8916141", "UTF-8");
            String message = "&message=" + URLEncoder.encode("This is your message", "UTF-8");
            String sender = "&sender=" + URLEncoder.encode("TXTLCL", "UTF-8");
            String numbers = "&numbers=" + URLEncoder.encode("918097441941", "UTF-8");

            // Send data
            String data = "http://api.textlocal.in/send/?" + user + hash + numbers + message + sender;
            URL url = new URL(data);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            String sResult="";
            while ((line = rd.readLine()) != null) {
                // Process line...
                sResult=sResult+line+" ";
            }
            rd.close();

            return sResult;
        } catch (Exception e) {
            System.out.println("Error SMS "+e);
            return "Error "+e;
        }
    }
}