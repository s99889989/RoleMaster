package com.daxton.rolemaster.skill;

import com.daxton.rolemaster.RoleMaster;
import com.daxton.rolemaster.been.RolePlayer;
import com.daxton.rolemaster.controller.RolePlayerController;
import com.daxton.rolemaster.controller.RolePlayerGetController;
import com.daxton.rolemaster.controller.RoleSkillController;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.mechanics.CustomMechanic;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class RoleItemAttributeMechanic implements ITargetedEntitySkill {
    //屬性
    private final Attribute attribute;
    //標記
    private final String mark;
    //值
    private final String amount;
    //物品
    private final String item;
    //是否移除
    private final boolean remove;
    public RoleItemAttributeMechanic(CustomMechanic customMechanic, MythicLineConfig mythicLineConfig) {

        this.attribute = mythicLineConfig.getEnum(new String[]{"attribute", "attr"}, Attribute.class, Attribute.GENERIC_LUCK, "Not a valid attribute given");
        this.mark = mythicLineConfig.getString(new String[]{"m", "mark"}, "");
        this.amount = mythicLineConfig.getString(new String[]{"a", "amount"}, "0");
        this.item = mythicLineConfig.getString(new String[]{"item", "i"}, "");
        this.remove = mythicLineConfig.getBoolean(new String[]{"remove", "r"}, false);
    }



    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        Entity casterEntity = BukkitAdapter.adapt(skillMetadata.getCaster().getEntity());
        Entity targetEntity = BukkitAdapter.adapt(abstractEntity);

        if(targetEntity instanceof Player){
            Player player = (Player) targetEntity;

            AttributeInstance attributeInstance = player.getAttribute(attribute);
            if (attributeInstance != null) {
                int amount = RoleSkillController.getSkillValue(casterEntity, targetEntity, this.amount);
                String mark = RoleSkillController.getSkillValueString(casterEntity, targetEntity, this.mark);
                RolePlayer rolePlayer = RolePlayerGetController.getRolePlayer(player);
                if(remove){
                    rolePlayer.removeItemAttribute(this.item, mark);
                }else {
                    rolePlayer.setItemAttribute(this.item, this.attribute, mark, amount);
                    rolePlayer.changeMainItem(player.getInventory().getItemInMainHand());
                }

            }
        }


        return SkillResult.INVALID_TARGET;
    }

}
