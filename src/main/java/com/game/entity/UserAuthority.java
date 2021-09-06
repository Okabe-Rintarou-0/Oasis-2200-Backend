package com.game.entity;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.game.properties.JwtProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthority {
    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    Integer userId;
    String username;
    String password;
    String email;
    int identity;

    public UserAuthority(String username, String password, String email, int identity) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.identity = identity;
    }

    @JsonIgnore
    public String getToken() {
        String token = "";
        token = JWT.create()
                .withClaim("userId", userId.toString())
                .withClaim("username", username)
                .withExpiresAt(new Date(System.currentTimeMillis() + 5000 * 1000))
                .sign(Algorithm.HMAC256(password));
        return token;
    }
}
