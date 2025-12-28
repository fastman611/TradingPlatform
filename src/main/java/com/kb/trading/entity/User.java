package com.kb.trading.entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.*;
import java.math.BigDecimal;
@Entity
@Table(name = "user")
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {
    private String username;      // 用户名

    private String password;      // 密码

    private String nickname;      // 昵称

    @Column(unique = true)
    private String phone;         // 手机号

    private String email;         // 邮箱

    private String avatar;        // 头像

    private String address;       // 地址

    @Column(precision = 10, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;  // 余额

    private Integer status = 1;   // 状态 0-禁用 1-正常

    private String role = "USER"; // 角色
}
