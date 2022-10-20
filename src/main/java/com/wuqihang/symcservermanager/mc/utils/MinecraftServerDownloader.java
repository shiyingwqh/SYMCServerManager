package com.wuqihang.symcservermanager.mc.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.wuqihang.symcservermanager.mc.MinecraftServerVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * @author Wuqihang
 */
public class MinecraftServerDownloader {
    private final List<MinecraftServerVersion> minecraftServerVersions = new ArrayList<>();
    private final Logger logger = LoggerFactory.getLogger(MinecraftServerDownloader.class);
    private final JsonMapper mapper = new JsonMapper();

    public MinecraftServerDownloader() throws IOException {
        URL url = new URL("http://launchermeta.mojang.com/mc/game/version_manifest.json");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(15000);
        connection.connect();
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = connection.getInputStream();
            byte[] bytes = inputStream.readAllBytes();
            inputStream.close();
            JsonNode jsonNode = mapper.readTree(bytes);
            JsonNode versions = jsonNode.get("versions");
            if (versions.isArray()) {
                ArrayNode arrayNode = (ArrayNode) versions;
                arrayNode.forEach(jsonNode1 -> {
                    try {
                        MinecraftServerVersion version = mapper.readValue(jsonNode1.toString(), MinecraftServerVersion.class);
                        if (version != null) {
                            logger.info("Version "+ version.getId() + " added!");
                            minecraftServerVersions.add(version);
                        }
                    } catch (JsonProcessingException ignored) {
                    }
                });
            }
        }
        connection.disconnect();
    }

    public List<String> getAllId() {
        return minecraftServerVersions.stream().map(MinecraftServerVersion::getId).toList();
    }

    public String getUrl(String id) {
        return minecraftServerVersions.stream()
                .filter(minecraftServerVersion -> id.equals(minecraftServerVersion.getId()))
                .map(MinecraftServerVersion::getUrl).findFirst().orElse(null);
    }

    public Future<Boolean> download(String id, String path) {
        FutureTask<Boolean> task = new FutureTask<>(() -> {
            URL url = new URL(getUrl(id));
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setConnectTimeout(15000);
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                connection.disconnect();
                return false;
            }
            InputStream inputStream = connection.getInputStream();
            byte[] bytes = inputStream.readAllBytes();
            inputStream.close();
            connection.disconnect();
            JsonNode root = mapper.readTree(bytes);
            String serverUrlStr = root.get("downloads").get("server").get("url").asText();
            URL serverUrl = new URL(serverUrlStr);
            connection = (HttpsURLConnection) serverUrl.openConnection();
            connection.connect();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                connection.disconnect();
                logger.info(id + "Server Jar Failed");
                return false;
            }
            File file = new File(path, "server.jar");
            boolean newFile = file.createNewFile();
            try {
                if (!newFile) {
                    return false;
                }
                inputStream = connection.getInputStream();
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                byte[] buffer = new byte[4096];
                int len;
                while ((len = inputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, len);
                }
                fileOutputStream.close();
                inputStream.close();
                logger.info(id + "Server Jar Downloaded");
            } catch (Exception e) {
                file.delete();
                logger.info(id + "Server Jar Failed");
                return false;
            }
            connection.disconnect();
            connection.disconnect();
            return true;
        });
        task.run();
        return task;
    }
}
