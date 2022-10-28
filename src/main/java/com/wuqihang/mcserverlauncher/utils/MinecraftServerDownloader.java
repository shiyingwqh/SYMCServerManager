package com.wuqihang.mcserverlauncher.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

/**
 * @author Wuqihang
 */
public class MinecraftServerDownloader {
    private final List<MinecraftServerVersion> minecraftServerVersions = new ArrayList<>();
    private final Logger logger = LoggerFactory.getLogger(MinecraftServerDownloader.class);
    private final JsonMapper mapper = new JsonMapper();
    private final ExecutorService pool;

    protected MinecraftServerDownloader() {
        pool = Executors.newFixedThreadPool(8);
    }

    public void init() throws IOException {
        File file = DataFiles.VERSIONS_JSON;
        if (file.exists()) {
            Set<MinecraftServerVersion> versions = mapper.readValue(file, new TypeReference<Set<MinecraftServerVersion>>() {
            });
            minecraftServerVersions.addAll(versions);
            return;
        }
        URL url = new URL("http://launchermeta.mojang.com/mc/game/version_manifest.json");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(15000);
        connection.connect();
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = connection.getInputStream();
            byte[] bytes = inputStream.readAllBytes();
            JsonNode jsonNode = mapper.readTree(bytes);
            JsonNode versions = jsonNode.get("versions");
            if (versions.isArray()) {
                ArrayNode arrayNode = (ArrayNode) versions;
                arrayNode.forEach(jsonNode1 -> {
                    try {
                        MinecraftServerVersion version = mapper.readValue(jsonNode1.toString(), MinecraftServerVersion.class);
                        if (version != null) {
                            logger.debug("Version " + version.getId() + " added!");
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

    public FutureTask<Boolean> download(String id, String path) {
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
            connection.disconnect();
            JsonNode root = mapper.readTree(bytes);
            String serverUrlStr = root.get("downloads").get("server").get("url").asText();
            URL serverUrl = new URL(serverUrlStr);
            connection = (HttpsURLConnection) serverUrl.openConnection();
            connection.connect();
            logger.info("Download From: " + serverUrlStr);
            long size = connection.getContentLengthLong();
            long cur = 0;
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                connection.disconnect();
                logger.info(id + " Server Jar Failed");
                return false;
            }
            File file = new File(path, "server.jar");
            boolean newFile = file.createNewFile();
            try {
                if (!newFile) {
                    return false;
                }
                inputStream = connection.getInputStream();
                try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                    byte[] buffer = new byte[8192];
                    int len;
                    while ((len = inputStream.read(buffer)) > 0) {
                        fileOutputStream.write(buffer, 0, len);
                        cur += len;
                    }
                }
                logger.info(id + " Server Jar Downloaded");
            } catch (Exception e) {
                file.delete();
                logger.info(id + " Server Jar Download Failed");
                return false;
            }
            connection.disconnect();
            connection.disconnect();
            return true;
        });
        pool.submit(task);
        return task;
    }

    public FutureTask<Boolean> downloadForgeInstaller(String id, String version, String category, String format, String savePath) throws IOException {
        FutureTask<Boolean> task = new FutureTask<>(() -> {
            URL url = new URL("https://bmclapi2.bangbang93.com/forge/download?mcversion=" + URLDecoder.decode(id) +
                    "&version=" + URLDecoder.decode(version) +
                    "&category=" + URLDecoder.decode(category) +
                    "&format=" + URLDecoder.decode(format));
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

            connection.connect();
            if (connection.getResponseCode() != 200) {
                return false;
            }
            logger.info("Download Form: " + url);
            InputStream inputStream = connection.getInputStream();

            File file = new File(savePath, "forge_installer.jar");
            boolean newFile = file.createNewFile();
            if (!newFile) {
                return false;
            }
            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                byte[] buffer = new byte[8192];
                int len;
                while ((len = inputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, len);
                }
            } catch (IOException e) {
                return false;
            }

            return true;
        });
        pool.submit(task);
        return task;
    }

    public void destroy() throws IOException {
        pool.shutdown();
        File file = DataFiles.VERSIONS_JSON;
        if (!file.exists()) {
            if (!file.createNewFile()) {
                return;
            }
        }
        mapper.writeValue(file, minecraftServerVersions);
    }
}
