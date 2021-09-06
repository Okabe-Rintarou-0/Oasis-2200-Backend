package com.game.entity;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description:
 * @date 2021/8/25 16:07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CharacterInfoCollection {
    Map<String, List<CharacterInfo>> data = new HashMap<>();

    public void addCharacter(String gameId, List<CharacterInfo> infos) {
        data.put(gameId, infos);
    }

    public boolean isFull() {
        return data.size() == 2;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
