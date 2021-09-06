package com.game.utils.messageUtils;

import lombok.AllArgsConstructor;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@AllArgsConstructor
public class StompMessage {
    public String topic;
    public String msg;
    public JSONObject data;
}
