package com.kb.trading.service;
import com.kb.trading.entity.User;
import java.util.List;
public interface UserService {
    // 用户注册
    User register(User user);

    // 用户登录
    User login(String username, String password);

    // 根据ID获取用户
    User getUserById(Long id);

    // 获取所有用户（管理员用）
    List<User> getAllUsers();

    // 更新用户信息
    User updateUser(User user);

    // 删除用户
    void deleteUser(Long id);

    // 检查用户名是否可用
    boolean isUsernameAvailable(String username);

    // 检查手机号是否可用
    boolean isPhoneAvailable(String phone);
}
