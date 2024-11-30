package com.zyneonstudios.meta;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class VersionManifest {

    private final String url;
    private String jsonString;
    private JSONObject json;
    private JSONArray versions;

    public VersionManifest(String url) {
        this.url = url;
        this.jsonString = null;
        this.json = null;
        update();
    }

    public void update() {
        System.out.println("Updating version manifest...");
        try {
            System.out.println("Connecting to "+url+"...");
            URLConnection data = new URL(url).openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(data.getInputStream()));
            System.out.println("Reading data...");
            json = JSON.parseObject(reader);
            jsonString = json.toJSONString();
            System.out.println("Getting versions...");
            versions = json.getJSONArray("versions");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("Successfully updated version manifest!");
    }

    public String getUrl() {
        return url;
    }

    public String getJsonString() {
        return jsonString;
    }

    public JSONObject getJson() {
        return json;
    }

    public String getLatestRelease() {
        if(json == null) {
            update();
        }
        return json.getJSONObject("latest").getString("release");
    }

    public String getLatestSnapshot() {
        if(json == null) {
            update();
        }
        return json.getJSONObject("latest").getString("snapshot");
    }

    public JSONArray getVersions() {
        return versions;
    }
}