package com.wuqihang.symcservermanager.services;

import com.wuqihang.symcservermanager.pojo.User;
import org.springframework.beans.factory.DisposableBean;

import java.util.List;

/**
 * @author Wuqihang
 */
public interface UserService extends DisposableBean {
    User getUser(int id);
    User getUser(String username);
    List<User> getAllUser();
    void addUser(User user);
    void deleteUser(int id);
    void updateUser(int id, User user);

    User checkUser(String username, String password);
}
