package com.daxton.rolemaster.skill;

import com.daxton.rolemaster.controller.RoleSkillController;
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

public class RoleHealMechanic implements ITargetedEntitySkill {

    private final String amount;

    private final String sound;

    public RoleHealMechanic(CustomMechanic customMechanic, MythicLineConfig mythicLineConfig) {
        this.amount = mythicLineConfig.getString(new String[]{"a", "amount"}, "0");
        this.sound = mythicLineConfig.getString(new String[]{"sound"}, "");

    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {

        Entity casterEntity = BukkitAdapter.adapt(skillMetadata.getCaster().getEntity());
        Entity targetEntity = BukkitAdapter.adapt(abstractEntity);
        Location location = targetEntity.getLocation();

        if(location.getWorld() != null){
            location.getWorld().playSound(location, sound, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }

        int amount = RoleSkillController.getSkillValue(casterEntity, targetEntity, this.amount);
        if(targetEntity instanceof LivingEntity){
            LivingEntity livingEntity = (LivingEntity) targetEntity;
            double heal = amount + livingEntity.getHealth();
            if(heal > livingEntity.getMaxHealth()){
                heal = livingEntity.getMaxHealth();
            }
            livingEntity.setHealth(heal);
        }

        return SkillResult.INVALID_TARGET;
    }

}
