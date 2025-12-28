package com.kb.trading.controller;
import com.kb.trading.entity.User;
import com.kb.trading.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    // 用户注册
    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userService.register(user);
    }

    // 用户登录
    @PostMapping("/login")
    public User login(@RequestParam String username,
                      @RequestParam String password) {
        return userService.login(username, password);
    }

    // 获取用户信息
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // 获取所有用户（管理员用）
    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // 更新用户信息
    @PutMapping("/update")
    public User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    // 删除用户
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "用户删除成功";
    }

    // 检查用户名是否可用
    @GetMapping("/check-username")
    public String checkUsername(@RequestParam String username) {
        boolean available = userService.isUsernameAvailable(username);
        return available ? "用户名可用" : "用户名已存在";
    }

    // 检查手机号是否可用
    @GetMapping("/check-phone")
    public String checkPhone(@RequestParam String phone) {
        boolean available = userService.isPhoneAvailable(phone);
        return available ? "手机号可用" : "手机号已注册";
    }
}
