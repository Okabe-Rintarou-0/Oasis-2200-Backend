package com.game.properties;

import com.game.annotation.Properties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Properties
@ConfigurationProperties(
        prefix = "jwt"
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class JwtProperties {
    long accessTokenExpireTime;
    String encryptJWTKey;
}
