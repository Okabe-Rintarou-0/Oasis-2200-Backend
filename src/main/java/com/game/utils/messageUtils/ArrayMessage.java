package com.game.utils.messageUtils;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.sf.json.JSONArray;

@AllArgsConstructor
@NoArgsConstructor
public class ArrayMessage {
    public int status;
    public String message;
    public JSONArray data;
}
