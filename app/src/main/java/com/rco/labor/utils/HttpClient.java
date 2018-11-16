package com.rco.labor.utils;

/**
 * Created by Fernando on 9/16/2018.
 */

import android.util.Log;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;

public class HttpClient {
    public static String TAG = HttpClient.class.getName();
    private static int NUMBER_OF_RETRIES = 3;
    private static String CODEPAGE = "UTF-8";
    private final static int timeoutMillis = 1 * 60 * 1000;

    public static String get(String url) throws IOException {
        Log.d(TAG, url);

        Date startDate = new Date();
        String result;

        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutMillis);		// Set the timeout in milliseconds until a connection is established. The default value is zero, that means the timeout is not used.
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutMillis);				// Set the default socket timeout (SO_TIMEOUT) in milliseconds which is the timeout for waiting for data.

        DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);

        HttpGet httpGet = new HttpGet(url);

        HttpResponse httpResponse = httpClient.execute(httpGet);
        HttpEntity httpEntity = httpResponse.getEntity();

        if (httpEntity != null) {
            System.gc();
            InputStream in = httpEntity.getContent();
            result = convertStreamToString(in);
            in.close();

            Date endDate = new Date();
            long elapsedSecs = (endDate.getTime() - startDate.getTime()) / 1000;
            Log.d(TAG, "(" + elapsedSecs + "secs) := " + result);

            return result;
        }

        return null;
    }

    public static String post(String url, String postBody) throws IOException {
        return post(url, postBody, null);
    }

    public static String post(String url, String postBody, HashMap<String, String> headers) throws IOException {
        Log.d(TAG, url + "\n" + postBody);

        Date startDate = new Date();

        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new StringEntity(postBody, CODEPAGE));

        httpPost.setHeader("Content-Type", "application/json");

        if (headers != null)
            for (Map.Entry<String, String> header : headers.entrySet())
                httpPost.setHeader(header.getKey(), header.getValue());

        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutMillis);		// Set the timeout in milliseconds until a connection is established. The default value is zero, that means the timeout is not used.
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutMillis);				// Set the default socket timeout (SO_TIMEOUT) in milliseconds which is the timeout for waiting for data.

        org.apache.http.client.HttpClient httpclient = new DefaultHttpClient(httpParameters);
        HttpResponse httpResponse = httpclient.execute(httpPost);

        HttpEntity httpEntity = httpResponse.getEntity();
        String result = null;

        if (httpEntity != null) {
            System.gc();
            InputStream in = httpEntity.getContent();
            result = convertStreamToString(in);
            in.close();
        }

        Date endDate = new Date();
        long elapsedSecs = (endDate.getTime() - startDate.getTime()) / 1000;
        Log.d(TAG, "(" + elapsedSecs + "secs) := " + result);

        return result;
    }

    public static String postFile(String url, String contentType, String filename, byte[] content) throws Exception {
        String result = null;

        for (int i=0; i<NUMBER_OF_RETRIES; i++)
            try {
                result = postFileBytes(url, contentType, filename, content);
                break;
            } catch (Exception ex) {
                ex.printStackTrace();
                result = null;

                if (i == NUMBER_OF_RETRIES - 1)
                    throw ex;
            }

        return result;
    }

    private static String postFileBytes(String urlStr, String fileContentType, String filename, byte[] data) throws Exception {
        String result = null;

        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;

        final String end = "\r\n";
        final String twoHyphens = "--";
        final String boundary = "*****";

        try {
            Log.d(TAG, "URL: " + urlStr + " (" + filename + "): " + data.toString());

            byte[] buffer = data;

            if (urlStr.indexOf("https") < 0)
                urlStr = urlStr.replace("http", "https");

            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + end);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + filename +"\"" + end);
            outputStream.writeBytes(end);
            outputStream.write(buffer, 0, buffer.length);
            outputStream.writeBytes(end);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + end);
            outputStream.flush();
            outputStream.close();

            int serverResponseCode = connection.getResponseCode();

            result  = serverResponseCode >= 200 && serverResponseCode <= 299 ?
                convertInputStreamToString(connection.getInputStream()) : connection.getResponseMessage();

            Log.d(TAG, ":= " + result);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        } finally {
            if (connection != null)
                connection.disconnect();
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        StringBuffer sb = new StringBuffer();
        String line = null;

        while((line = bufferedReader.readLine()) != null)
            sb.append(line);

        inputStream.close();

        if (sb.length() > 0)
            return sb.toString();

        return null;
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;

        try {
            while ((line = reader.readLine()) != null)
                sb.append(line).append('\n');
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String result = sb.toString().trim();

        return result != null && result.length() > 0 && result.charAt(result.length()-1) == '\n' ?
            result.substring(0, result.length()-1) : result;
    }
}
