package com.game.properties;

import com.game.annotation.Properties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description:
 * @date 2021/8/24 12:20
 */
@Properties
@ConfigurationProperties(
        prefix = "room"
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomProperties {
    Integer maxCount;
    Integer sectionSize;
    Integer sectionCount;
    String sectionKeySuffix;
}
