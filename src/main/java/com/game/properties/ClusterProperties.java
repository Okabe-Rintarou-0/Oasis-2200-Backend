package com.game.properties;

import com.game.annotation.Properties;
import com.game.utils.ipUtils.IpUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.net.SocketException;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description:
 * @date 2021/8/21 11:13
 */
@Properties
@ConfigurationProperties(
        prefix = "cluster"
)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClusterProperties {
    String strategy;

    @Bean
    String intraNetIp() throws SocketException {
        return IpUtil.getIntranetIp();
    }
}
