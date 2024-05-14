package com.daxton.rolemaster.been.item.attribute;

import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;


@NoArgsConstructor
public class ItemAttribute {

    private Map<String, AttributeData> attributeDataMap = new HashMap<>();

    public void setAttribute(String mark, AttributeData attributeData){
        attributeDataMap.put(mark, attributeData);
    }

    public void removeAttribute(String mark){
        attributeDataMap.remove(mark);
    }

    public void setPlayerAttribute(Player player){
        attributeDataMap.forEach((s, attributeData) ->{
            attributeData.setPlayerAttribute(player);
        });
    }

    public void removePlayerAttribute(Player player){
        attributeDataMap.forEach((s, attributeData) -> {
            attributeData.removePlayerAttribute(player);
        });
    }

}
