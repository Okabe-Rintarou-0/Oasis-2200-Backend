package com.game.utils.messageUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description:
 * @date 2021/8/22 17:39
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RedisMessage implements Serializable {
    public String intraNetIp;
    public String message;
    public Object data;
}
