package com.blackwood3.driveroo;

/**
 * Created by calvin on 24/09/17.
 */

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;


public class HttpUtils {

    public static JSONObject submitPostData(Map<String, String> params, String encode, String operation) throws MalformedURLException {
        /**
         * 发送POST请求到服务器并返回服务器信息
         * @param params 请求体内容
         * @param encode 编码格式
         * @return 服务器返回信息
         */
        JSONObject nullreturn = new JSONObject();
        byte[] data = getPOSTRequestData(params, encode).toString().getBytes();
        URL url = new URL("http://192.168.1.107:5001/");
        if (operation.equals("login")) {
            url = new URL("http://192.168.1.107:5001/login");
        } else if (operation.equals("signup")) {
            url = new URL("http://192.168.1.107:5001/signup");
        }
        String doc2 = new String(data);
        Log.w("Input data", doc2);
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(3000);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
            OutputStream outputStream = new BufferedOutputStream(httpURLConnection.getOutputStream());
            outputStream.write(data);
            outputStream.flush();

            int response = httpURLConnection.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = httpURLConnection.getInputStream();
                return dealResponseResult(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpURLConnection.disconnect();
        }

        return nullreturn;
    }

    public static JSONObject submitGETData(Map<String, String> params, String encode, String operation) throws MalformedURLException {
        JSONObject nullreturn = new JSONObject();
        String url = new String();
        if(operation.equals("getProfile")){
            url = "http://192.168.1.107:5001/getProfile?";
            url += getGETRequestUrl(params,encode);
            Log.w("URL",url);
            URL obj = new URL(url);
            HttpURLConnection httpURLConnection = null;
            try{
                httpURLConnection = (HttpURLConnection) obj.openConnection();
                httpURLConnection.setRequestMethod("GET");
                int response = httpURLConnection.getResponseCode();
                if (response == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    return dealResponseResult(inputStream);
                }

            }catch (IOException e){
                e.printStackTrace();
            }finally {
                httpURLConnection.disconnect();
            }

        }else if(operation.equals("check_server")){
            url = "http://192.168.1.107:5001/check_server?";
            url += getGETRequestUrl(params,encode);
            Log.w("URL",url);
            URL obj = new URL(url);
            HttpURLConnection httpURLConnection = null;
            try{
                httpURLConnection = (HttpURLConnection) obj.openConnection();
                httpURLConnection.setRequestMethod("GET");
                int response = httpURLConnection.getResponseCode();
                if (response == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    return dealResponseResult(inputStream);
                }

            }catch (IOException e){
                e.printStackTrace();
            }finally {
                httpURLConnection.disconnect();
            }
        }else if(operation.equals("update_start")){
            url = "http://192.168.1.107:5001/update_start?";
            url += getGETRequestUrl(params,encode);
            Log.w("URL",url);
            URL obj = new URL(url);
            HttpURLConnection httpURLConnection = null;
            try{
                httpURLConnection = (HttpURLConnection) obj.openConnection();
                httpURLConnection.setRequestMethod("GET");
                int response = httpURLConnection.getResponseCode();
                if (response == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    return dealResponseResult(inputStream);
                }

            }catch (IOException e){
                e.printStackTrace();
            }finally {
                httpURLConnection.disconnect();
            }
        } else if(operation.equals("update_end")){
            url = "http://192.168.1.107:5001/update_end?";
            url += getGETRequestUrl(params,encode);
            Log.w("URL",url);
            URL obj = new URL(url);
            HttpURLConnection httpURLConnection = null;
            try{
                httpURLConnection = (HttpURLConnection) obj.openConnection();
                httpURLConnection.setRequestMethod("GET");
                int response = httpURLConnection.getResponseCode();
                if (response == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    return dealResponseResult(inputStream);
                }

            }catch (IOException e){
                e.printStackTrace();
            }finally {
                httpURLConnection.disconnect();
            }
        }
        return nullreturn;
    }

    public static StringBuffer getPOSTRequestData(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), encode))
                        .append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }

    public static String getGETRequestUrl(Map<String, String> params, String encode) {
        StringBuilder extra = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                extra.append(entry.getKey());
                extra.append("=");
                extra.append(URLEncoder.encode(entry.getValue(), encode));
                extra.append("&");
            }
            extra.deleteCharAt(extra.length() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return extra.toString();
    }

    public static JSONObject dealResponseResult(InputStream inputStream) {
        String resultData = null;
        JSONObject nullreturn = new JSONObject();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        try {
            while ((len = inputStream.read(data)) != -1) {
                byteArrayOutputStream.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultData = new String(byteArrayOutputStream.toByteArray());
        try {
            JSONObject resultJSON = new JSONObject(resultData);
            return resultJSON;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return nullreturn;
    }
}