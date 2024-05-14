package com.daxton.rolemaster.skill;

import com.daxton.rolemaster.RoleMaster;
import com.daxton.rolemaster.been.RolePlayer;
import com.daxton.rolemaster.controller.RolePlayerController;
import com.daxton.rolemaster.controller.RolePlayerGetController;
import com.daxton.rolemaster.controller.RoleSkillController;
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

public class RoleArrowVelocityMechanic implements ITargetedEntitySkill {

    //值
    private final String amount;
    //持續時間
    private final String duration;

    public RoleArrowVelocityMechanic(CustomMechanic customMechanic, MythicLineConfig mythicLineConfig) {

        this.amount = mythicLineConfig.getString(new String[]{"a", "amount"}, "0");
        this.duration = mythicLineConfig.getString(new String[]{"d", "duration"}, "60");

    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {

        Entity casterEntity = BukkitAdapter.adapt(skillMetadata.getCaster().getEntity());
        Entity targetEntity = BukkitAdapter.adapt(abstractEntity);

        if(targetEntity instanceof Player){
            Player player = (Player) targetEntity;
            int amount = RoleSkillController.getSkillValue(casterEntity, targetEntity, this.amount);
            int duration = RoleSkillController.getSkillValue(casterEntity, targetEntity, this.duration);

            RolePlayer rolePlayer = RolePlayerGetController.getRolePlayer(player);
            rolePlayer.setArrowVelocity(amount);
            if(duration > 0){
                SchedulerFunction.runLater(RoleMaster.roleMaster, ()->{
                    rolePlayer.setArrowVelocity(0);
                },duration);
            }
        }

        return SkillResult.INVALID_TARGET;
    }

}
