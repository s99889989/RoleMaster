package com.daxton.rolemaster.skill;

import com.daxton.rolemaster.RoleMaster;
import com.daxton.rolemaster.controller.RoleSkillController;
import com.daxton.rolemaster.type.DamageType;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.mechanics.CustomMechanic;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class RoleDamageMechanic implements ITargetedEntitySkill {
    //值
    private final String amount;
    //聲音
    private final String sound;
    //傷害類型
    private final DamageType type;

    String typeString;

    public RoleDamageMechanic(CustomMechanic customMechanic, MythicLineConfig mythicLineConfig) {

        this.amount = mythicLineConfig.getString(new String[]{"a", "amount"}, "");
        this.sound = mythicLineConfig.getString(new String[]{"sound"}, "");
        typeString = mythicLineConfig.getString(new String[]{"type", "t"}, "MElEE");

        this.type = DamageType.getDamageType(typeString);
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {

        Entity casterEntity = BukkitAdapter.adapt(skillMetadata.getCaster().getEntity());
        Entity targetEntity = BukkitAdapter.adapt(abstractEntity);
        Location location = targetEntity.getLocation();
        if(location.getWorld() != null){
            if(!targetEntity.isDead()){
                location.getWorld().playSound(location, sound, SoundCategory.PLAYERS, 1.0F, 1.0F);
            }

        }

        int amount = RoleSkillController.getSkillValue(casterEntity, targetEntity, this.amount);
        double amountDouble = getAmountDouble();

        if(targetEntity instanceof LivingEntity){
            LivingEntity livingEntity = (LivingEntity) targetEntity;
            livingEntity.damage(amount+amountDouble, casterEntity);

        }

        return SkillResult.INVALID_TARGET;
    }

    private double getAmountDouble() {
        double amountDouble = 0;
        if(type == DamageType.RANGEMULTIPLY){amountDouble = 0.3444440000000001;}
        if(type == DamageType.RANGEADD){amountDouble = 0.3333330000000001;}
        if(type == DamageType.RANGE){amountDouble = 0.3222220000000001;}
        if(type == DamageType.MAGICMULTIPLY){amountDouble = 0.2444440000000001;}
        if(type == DamageType.MAGICADD){amountDouble = 0.2333330000000001;}
        if(type == DamageType.MAGIC){amountDouble = 0.2222220000000001;}
        if(type == DamageType.MELEEMULTIPLY){amountDouble = 0.1444440000000001;}
        if(type == DamageType.MELEEADD){amountDouble = 0.1333330000000001;}
        if(type == DamageType.MELEE){amountDouble = 0;}
        return amountDouble;
    }

}
