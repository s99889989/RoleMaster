package com.daxton.rolemaster.application;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemFunction {

    public static String getItemID(Player player){
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        return getItemID(itemStack);
    }

    public static String getItemID(ItemStack itemStack){
        if(itemStack == null){
            return "Null:0";
        }
        String type = itemStack.getType().toString();
        int cmd = 0;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta != null && itemMeta.hasCustomModelData()){
            cmd = itemMeta.getCustomModelData();
        }
        return type+":"+cmd;
    }

}
