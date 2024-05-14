package com.daxton.rolemaster.been.item.damage;

import java.util.HashMap;
import java.util.Map;

public class ItemDamage {

    private Map<String, Integer> damageMap = new HashMap<>();

    public void setDamage(String mark, int damage){
        damageMap.put(mark, damage);
    }

    public void removeDamage(String mark){
        damageMap.remove(mark);
    }

    public double getDamage(double damage){

        for(int d : damageMap.values()){
            damage += d;
        }

        return damage;
    }

}
