package com.daxton.rolemaster.skill;

import com.daxton.rolemaster.api.entity.RoleHolo;
import com.daxton.rolemaster.controller.RoleSkillController;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.mechanics.CustomMechanic;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RoleHoloTextMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {

    public static Map<String, RoleHolo> roleHoloMap = new ConcurrentHashMap<>();
    //顯示名稱
    private final String text;
    //標記
    private final String mark;
    //持續時間
    private final String duration;
    //移動
    private final boolean teleport;
    //是否刪除
    private final boolean delete;
    //高度
    private final String yString;

    public RoleHoloTextMechanic(CustomMechanic customMechanic, MythicLineConfig mythicLineConfig) {

        this.text = mythicLineConfig.getString(new String[]{"text", "t"}, "");
        this.mark = mythicLineConfig.getString(new String[]{"mark", "m"}, "");
        this.duration = mythicLineConfig.getString(new String[]{"duration","dt"}, "0");
        this.teleport = mythicLineConfig.getBoolean(new String[]{"teleport", "tp"}, true);
        this.delete = mythicLineConfig.getBoolean(new String[]{"delete", "d"}, false);
        this.yString = mythicLineConfig.getString(new String[]{"y"}, "0");

    }

    @Override
    public SkillResult castAtLocation(SkillMetadata skillMetadata, AbstractLocation target) {
        Entity casterEntity = BukkitAdapter.adapt(skillMetadata.getCaster().getEntity());

        Location location =target.toPosition().toLocation();
        setHolo(casterEntity, location);

        return SkillResult.INVALID_TARGET;
    }
    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {

        Entity casterEntity = BukkitAdapter.adapt(skillMetadata.getCaster().getEntity());
        Entity targetEntity = BukkitAdapter.adapt(abstractEntity);
        Location location = targetEntity.getLocation();
        setHolo(casterEntity, location);

        return SkillResult.INVALID_TARGET;
    }

    public void setHolo(Entity casterEntity, Location inpuLocation){
        double y = RoleSkillController.getSkillValueDouble(casterEntity, null, this.yString);
        Location location = null;

        if(inpuLocation != null){
            location = inpuLocation.clone().add(0, y, 0);
        }
        if(location == null){
            return;
        }
        String targetUUID = casterEntity.getUniqueId().toString();
        String mark = targetUUID+RoleSkillController.getSkillValueString(casterEntity, null, this.mark);
        int duration = RoleSkillController.getSkillValue(casterEntity, null, this.duration);

        if(roleHoloMap.containsKey(mark)){
            RoleHolo roleHolo = roleHoloMap.get(mark);

            if(this.teleport){
                roleHolo.setLocation(inpuLocation, 0, y, 0);
            }

            roleHolo.setDuration(duration);
            if(delete){
                roleHolo.remove();
                roleHoloMap.remove(mark);

            }
        }else {

            RoleHolo roleHolo = new RoleHolo(mark, casterEntity, inpuLocation, 0, y, 0);
            roleHolo.setMessage(this.text);
            roleHolo.setDuration(duration);
            roleHoloMap.put(mark, roleHolo);

        }


    }

}
