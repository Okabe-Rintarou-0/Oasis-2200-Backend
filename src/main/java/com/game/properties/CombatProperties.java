package com.game.properties;

import com.game.annotation.Properties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description:
 * @date 2021/9/8 12:37
 */
@Properties
@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(
        prefix = "combat"
)
public class CombatProperties {
    Integer frameSyncInterval;
}
