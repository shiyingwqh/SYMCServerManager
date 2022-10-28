package com.wuqihang.mcserverlauncher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.wuqihang.mcserverlauncher.config.MinecraftServerConfig;
import com.wuqihang.mcserverlauncher.server.MinecraftServer;
import com.wuqihang.mcserverlauncher.server.MinecraftServerException;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException, MinecraftServerException, InterruptedException {
    }
}
