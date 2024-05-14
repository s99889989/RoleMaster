package com.daxton.rolemaster.skill;

import com.daxton.rolemaster.RoleMaster;
import com.daxton.rolemaster.api.crawl.CrPlayer;
import com.daxton.unrealcore.application.method.SchedulerFunction;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.mechanics.CustomMechanic;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class RolePostMechanic implements ITargetedEntitySkill {

    //姿勢
    private final String pose;
    //持續時間
    private final int duration;
    //取消
    private final boolean enable;

    public static Map<String, CrPlayer> crPlayerMap = new HashMap<>();

    public RolePostMechanic(CustomMechanic customMechanic, MythicLineConfig mythicLineConfig) {
        this.pose = mythicLineConfig.getString(new String[]{"p", "pose"}, "crawl");
        this.duration = mythicLineConfig.getInteger(new String[]{"d", "duration"}, 0);
        this.enable = mythicLineConfig.getBoolean(new String[]{"e", "enable"}, true);
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        Entity targetEntity = BukkitAdapter.adapt(abstractEntity);
        if(!(targetEntity instanceof Player)){
            return SkillResult.INVALID_TARGET;
        }
        Player player = (Player) targetEntity;
        String uuidString = player.getUniqueId().toString();
        CrPlayer crPlayer;
        if(!crPlayerMap.containsKey(uuidString)){
            crPlayer = new CrPlayer(player);
        }else {
            crPlayer = crPlayerMap.get(uuidString);
        }

        if(this.pose.equalsIgnoreCase("Crawl")){
            if(this.enable){
                crPlayer.startCrawling();
                if(duration > 0){
                    SchedulerFunction.runLater(RoleMaster.roleMaster,()->{
                        crPlayer.stopCrawling();
                    }, duration);
                }
            }else {
                crPlayer.stopCrawling();
            }
        }


        return SkillResult.INVALID_TARGET;
    }

}
