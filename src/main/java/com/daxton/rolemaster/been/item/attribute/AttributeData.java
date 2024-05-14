package com.daxton.rolemaster.been.item.attribute;

import com.daxton.rolemaster.RoleMaster;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

@Getter
@Setter
@ToString
public class AttributeData {

    //屬性
    private Attribute attribute;
    //名稱
    private String mark;
    //值
    private int amount;

    public AttributeData(Attribute attribute, String mark, int amount) {
        this.attribute = attribute;
        this.mark = mark;
        this.amount = amount;
    }

    public void setPlayerAttribute(Player livingEntity){
        AttributeInstance attributeInstance = livingEntity.getAttribute(attribute);
        if (attributeInstance != null) {
            AttributeModifier modifier = new AttributeModifier(mark, amount, AttributeModifier.Operation.ADD_NUMBER);
            attributeInstance.addModifier(modifier);
        }
    }

    public void removePlayerAttribute(Player livingEntity){
        AttributeInstance attributeInstance = livingEntity.getAttribute(attribute);
        if (attributeInstance != null) {
            for(AttributeModifier attributeModifier : attributeInstance.getModifiers()){
                String attrName = attributeModifier.getName();
                if(attrName.equals(mark)){
                    attributeInstance.removeModifier(attributeModifier);
                }
            }
        }

    }

}
