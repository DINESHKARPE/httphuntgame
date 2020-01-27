package com.gameplay;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class RestClinet {

    private HttpURLConnection getConnectionObject(final String requestUrl, final String requestMethod) throws IOException {

        URL url = new URL(requestUrl);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(requestMethod);
        connection.setRequestProperty("userid", Constants.userId);
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);

        return connection;

    }

    public JsonObject getData(final String requestUrl) throws IOException {

        HttpURLConnection httpURLConnection = getConnectionObject(requestUrl, Constants.HTTP_GET);

        StringBuffer response = new StringBuffer();

        if (httpURLConnection.getResponseCode() != 200)
            throw new RuntimeException("HTTP Response Code: " + httpURLConnection.getResponseCode());
        else {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(httpURLConnection.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        }
        httpURLConnection.disconnect();

        return convertStringToJson(response.toString());

    }


    public void postData(final String requestUrl, final String key, final String outputString) throws IOException, JSONException {

        HttpURLConnection httpURLConnection1 = getConnectionObject(requestUrl, Constants.HTTP_POST);

        httpURLConnection1.setRequestProperty("Content-Type", "application/json");
        DataOutputStream wr = new DataOutputStream(httpURLConnection1.getOutputStream());

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(key, outputString);

        String finalString = jsonObject.toString();

        byte[] outputInBytes = finalString.getBytes("UTF-8");
        wr.write( outputInBytes );

        int responseCode = httpURLConnection1.getResponseCode();
        String responseMessage = httpURLConnection1.getResponseMessage();

        System.out.println(responseMessage);
        System.out.println(responseCode);
    }


    private JsonObject convertStringToJson(final String jsonData) {
        return JsonParser.parseString(jsonData).getAsJsonObject();

    }
}
