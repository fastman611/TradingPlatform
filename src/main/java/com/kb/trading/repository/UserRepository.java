package com.kb.trading.repository;
import com.kb.trading.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    // 通过用户名查找用户
    Optional<User> findByUsername(String username);

    // 通过手机号查找用户
    Optional<User> findByPhone(String phone);

    // 通过邮箱查找用户
    Optional<User> findByEmail(String email);

    // 检查用户名是否已存在
    boolean existsByUsername(String username);

    // 检查手机号是否已存在
    boolean existsByPhone(String phone);

    // 检查邮箱是否已存在
    boolean existsByEmail(String email);
}
