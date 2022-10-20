package com.wuqihang.symcservermanager.controller;

import com.wuqihang.symcservermanager.pojo.User;
import com.wuqihang.symcservermanager.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author Wuqihang
 */
@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping("/login-check")
    public String login(@RequestParam("uname") String username, @RequestParam("pwd") String password, HttpServletRequest request, Model model) {
        User user = userService.checkUser(username, password);
        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
        } else {
            model.addAttribute("msg", "Username or Password Error");
            return "login";
        }
        return "redirect:/index";
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        return "redirect:/login";
    }
}
