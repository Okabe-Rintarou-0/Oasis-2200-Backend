package com.game.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.ws.soap.Addressing;
import java.io.Serializable;

@Data
@Addressing
@NoArgsConstructor
public class CharacterInfo implements Serializable {
    public String name;
    public String career;
    public String weapon;   //之后会改为绑定装备槽

    //血量为整数，统称为Health
    public int currentHealth;   //当前生命值
    public int maxHealth;       //最大生命值

    //-----以下四项均为0~100的浮点数, Status
    public double currentPressure;      //当前压力
    public double currentHunger;        //当前饥饿度
    public double currentThirst;       //当前口渴度
    public double currentThiredness;    //当前疲劳度

    //-----以下四项均为0~99的整数, Attribute
    public int stamina;     //体力
    public int strength;    //坚毅
    public int agilence;    //敏捷
    public int defense;     //防御

    //-----以下维护角色在哪个场景
    public String currentSceneName;
    public double posX, posY, posZ;
}
