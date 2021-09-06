package com.game.utils.messageUtils;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.sf.json.JSONObject;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message implements Serializable {
    public int status;
    public String message;
    public JSONObject data;

    @Override
    public String toString(){
        return JSON.toJSONString(this);
    }
}
