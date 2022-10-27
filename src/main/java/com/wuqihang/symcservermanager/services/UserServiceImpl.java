package com.wuqihang.symcservermanager.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wuqihang.symcservermanager.pojo.User;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Wuqihang
 */
@Component
public class UserServiceImpl implements UserService {

    private final Map<Integer, User> map;
    private final AtomicInteger ids = new AtomicInteger(0);
    private final ObjectMapper mapper;
    private final File file;

    public UserServiceImpl() {
        file = new File("user.json");
        mapper = new ObjectMapper();
        map = new Hashtable<>();
        try {
            List<User> users = mapper.readValue(file, new TypeReference<List<User>>() {
            });
            for (User user : users) {
                int id = ids.getAndIncrement();
                user.setId(id);
                map.put(id, user);
            }
        } catch (IOException e) {
            int id = ids.getAndIncrement();
            map.put(id, new User(id, true, true,"admin", "admin"));
        }

    }

    @Override
    public User getUser(int id) {
        return map.getOrDefault(id, null);
    }

    @Override
    public User getUser(String username) {
        return map.values().stream().filter(user -> Objects.equals(username, user.getUsername())).findFirst().orElse(null);
    }

    @Override
    public List<User> getAllUser() {
        return map.values().stream().toList();
    }

    @Override
    public void addUser(User user) {
        if (!map.containsKey(user.getId())) {
            map.put(user.getId(), user);
        } else {
            int i;
            while (map.containsKey(i = ids.getAndIncrement())) ;
            user.setId(i);
            map.put(i, user);
        }
    }

    @Override
    public void deleteUser(int id) {
        map.remove(id);
    }

    @Override
    public void updateUser(int id, User user) {
        if (map.containsKey(id) && id == user.getId()) {
            map.put(id, user);
        }
    }

    @Override
    public User checkUser(String username, String password) {
        User user = getUser(username);
        if (user == null || !user.getPassword().equals(password)) {
            return null;
        }
        return user;
    }

    @Override
    public void destroy() throws Exception {
        if (!file.exists()) {
            file.createNewFile();
        }
        try (PrintWriter printWriter = new PrintWriter(file, StandardCharsets.UTF_8)) {
            printWriter.write(mapper.writeValueAsString(map.values()));
        }
    }
}
