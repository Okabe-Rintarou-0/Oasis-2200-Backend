package com.game.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerStatus implements Serializable {
    private String name;
    private double posX, posY, posZ;
    private String weapon;
    private int currentHealth, maxHealth;
}
