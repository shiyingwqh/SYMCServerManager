package com.wuqihang.symcservermanager.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wuqihang.symcservermanager.pojo.User;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Wuqihang
 */
@Component
public class UserServiceImpl implements UserService, DisposableBean {

    private Map<Integer, User> map;
    private AtomicInteger ids = new AtomicInteger(0);
    private ObjectMapper mapper;
    private final File file;

    public UserServiceImpl() {
        file = new File("user.json");
        mapper = new ObjectMapper();
        map = new Hashtable<>();
        String userJson = null;
        if (file.exists()) {
            try {
                Scanner scanner = new Scanner(file);
                userJson = scanner.next();
                scanner.close();
            } catch (FileNotFoundException e) {
                userJson = null;
            }
        }
        if (userJson == null) {
            int id = ids.get();
            map.put(id, new User(id, true, "admin", "admin"));
        } else {
            try {
                List<User> users = mapper.readValue(userJson, new TypeReference<List<User>>() {});
                for (User user : users) {
                    int id = ids.get();
                    user.setId(id);
                    map.put(id,user);
                }
            } catch (JsonProcessingException e) {
                int id = ids.get();
                map.put(id, new User(id, true, "admin", "admin"));
            }
        }
    }

    @Override
    public User getUser(int id) {
        return map.getOrDefault(id, null);
    }

    @Override
    public User getUser(String username) {
        for (int key : map.keySet()) {
            User user = map.get(key);
            if (user.getUsername().equals(username)){
                return user;
            }
        }
        return null;
    }

    @Override
    public List<User> getAllUser() {
        return new ArrayList<>(map.values());
    }

    @Override
    public void addUser(User user) {
        if (!map.containsKey(user.getId())) {
            map.put(user.getId(), user);
        } else {
            int i;
            while (map.containsKey(i = ids.get())) ;
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