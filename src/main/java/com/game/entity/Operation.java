package com.game.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Operation implements Serializable {
    private String name;
    private double h;
    private double v;
    private Boolean jump;
    private Boolean fire;
    private double scroll;

    Operation() {
        this.name = ""; this.h = 0d; this.v = 0d; this.jump = false; this.fire = false; this.scroll = 0d;
    }

    public boolean isNormal() {
        if (Math.abs(h) > 0.000001) {
            return false;
        }
        if (Math.abs(v) > 0.000001) {
            return false;
        }
        if (Math.abs(scroll) > 0.000001) {
            return false;
        }
        return !jump && !fire;
    }

}
