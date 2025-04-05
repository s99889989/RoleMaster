package com.daxton.rolemaster.skill;

import com.daxton.rolemaster.RoleMaster;
import com.daxton.rolemaster.api.crawl.CrPlayer;
import com.daxton.rolemaster.controller.RoleSkillController;
import com.daxton.unrealcore.application.method.SchedulerFunction;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RoleStunMechanic implements ITargetedEntitySkill {

    private final String probability;

    private final String sound;

    private final String duration;



    public RoleStunMechanic(CustomMechanic customMechanic, MythicLineConfig mythicLineConfig) {
        this.probability = mythicLineConfig.getString(new String[]{"p", "probability"}, "");
        this.sound = mythicLineConfig.getString(new String[]{"sound"}, "");
        this.duration = mythicLineConfig.getString(new String[]{"duration"}, "20");
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {

        Entity casterEntity = BukkitAdapter.adapt(skillMetadata.getCaster().getEntity());
        Entity targetEntity = BukkitAdapter.adapt(abstractEntity);


        int probability = RoleSkillController.getSkillValue(casterEntity, targetEntity, this.probability);
        if(probability < 0){
            return SkillResult.INVALID_TARGET;
        }
        if(probability > 100){
            probability = 100;
        }

        Random random = new Random();
        int randomNumber = random.nextInt(101);
        if (randomNumber < probability) {
            Location location = targetEntity.getLocation();
            if(location.getWorld() != null){
                location.getWorld().playSound(location, sound, SoundCategory.PLAYERS, 1.0F, 1.0F);
            }
            int duration  = RoleSkillController.getSkillValue(casterEntity, targetEntity, this.duration);
            setStun(targetEntity);
            SchedulerFunction.runLater(RoleMaster.unrealCorePlugin.getJavaPlugin(), ()->{
                removeStun(targetEntity);
            }, duration);
        }



        return SkillResult.INVALID_TARGET;
    }

    public void setStun(Entity targetEntity){
        if(!(targetEntity instanceof LivingEntity)){
            return;
        }
        LivingEntity livingEntity = (LivingEntity) targetEntity;
        if(livingEntity.hasAI()){
            livingEntity.setAI(false);
        }
        if(livingEntity.hasGravity()){
            livingEntity.setGravity(false);
        }
    }

    public void removeStun(Entity targetEntity){
        if(!(targetEntity instanceof LivingEntity)){
            return;
        }
        LivingEntity livingEntity = (LivingEntity) targetEntity;
        livingEntity.setAI(true);
        livingEntity.setGravity(true);
    }

}
