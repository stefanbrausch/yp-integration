package org.jenkinsci.plugins.ypintegration;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class YpEngine {

    private final String baseurl;
    private final DefaultHttpClient httpClient;

    public static YpEngine connect() {
        return YpEngine.connect(Secrets.YP_LINK, Secrets.YP_USER, Secrets.YP_PASSWORD);
    }

    public static String categoryLookup(String id) {
        YpEngine yp;
        JSONObject json;
        yp = YpEngine.connect();
        try {
            json = yp.getJson(String.format("software/%s", id));
            json = yp.getJson(String.format("categories/%s", json.get("category")));
            return String.valueOf(json.get("name"));
        } catch (IOException e) {
            Constants.LOGGER.info("error occurred: " + e.getMessage());
        } finally {
            yp.disconnect();
        }
        return null;
    }

    public static YpEngine connect(String url, String username, String password) {
        UsernamePasswordCredentials creds;
        CredentialsProvider credsProvider;
        DefaultHttpClient httpClient;

        httpClient = new DefaultHttpClient();
        creds = new UsernamePasswordCredentials(username, String.copyValueOf(password.toCharArray()));
        credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT), creds);
        httpClient.setCredentialsProvider(credsProvider);
        return new YpEngine(url, httpClient);
    }

    // --

    public YpEngine(String url, DefaultHttpClient httpClient) {
        this.baseurl = url;
        this.httpClient = httpClient;
    }

    public void disconnect() {
        httpClient.getConnectionManager().shutdown();
    }


    public JSONObject getJson(String path) throws IOException {
        return (JSONObject) JSONValue.parse(getString(path));
    }

    public JSONArray getJsonArray(String path) throws IOException {
        return (JSONArray) JSONValue.parse(getString(path));
    }

    public String getString(String path) throws IOException {
        return request(new HttpGet(baseurl + path));
    }

    public long post(String path, String body) throws IOException {
        HttpPost post;
        StringEntity entity;
        String str;

        post = new HttpPost(baseurl + path);
        entity = new StringEntity(body);
        entity.setContentType("application/json");
        post.setEntity(entity);
        str = request(post);
        return Long.parseLong(str.trim());
    }

    public String put(String path, String body) throws IOException {
        HttpPut put;
        StringEntity entity;

        put = new HttpPut(baseurl + path);
        entity = new StringEntity(body);
        entity.setContentType("application/json");
        put.setEntity(entity);
        return request(put);
    }

    public String request(HttpRequestBase request) throws IOException {
        BufferedReader br;
        HttpResponse response;
        StringBuffer outputBuffer;

        br = null;
        try {
            request.setHeader("Accept", "application/json");
            response = httpClient.execute(request);
            outputBuffer = new StringBuffer();
            if (response.getEntity() != null) {
                br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
                outputBuffer = new StringBuffer();
                String output;
                while ((output = br.readLine()) != null) {
                    outputBuffer.append(output + "\n");
                }
            }
            if (response.getStatusLine().getStatusCode() != 200) {
                String responseMessage = "";
                if (outputBuffer.length() > 0) {
                    responseMessage = " : The error message was: " + outputBuffer.toString();
                }
                String errorMessage = "Failed : HTTP error code : " + response.getStatusLine().getStatusCode() + responseMessage;
                throw new IOException(errorMessage);
            }
            return outputBuffer.toString();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    // ignored
                }
            }
        }
    }

    public String baseurl() {
        return baseurl;
    }
}
