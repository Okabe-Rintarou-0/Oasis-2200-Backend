package com.game.entity;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.Document;

import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Archive implements Serializable {
    private String id;
    private Document data;
    private Date time;

    public Archive(Document data, Date time) {
        this.data = data;
        this.time = time;
    }

    @Override
    public String toString(){
        return JSON.toJSONString(this);
    }
}
