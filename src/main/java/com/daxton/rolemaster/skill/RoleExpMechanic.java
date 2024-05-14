package com.daxton.rolemaster.skill;

import com.daxton.rolemaster.controller.RolePlayerController;
import com.daxton.rolemaster.controller.RoleSkillController;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.mechanics.CustomMechanic;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class RoleExpMechanic implements ITargetedEntitySkill {

    private final String amount;

    public RoleExpMechanic(CustomMechanic customMechanic, MythicLineConfig mythicLineConfig) {

        this.amount = mythicLineConfig.getString(new String[]{"a", "amount"}, "");
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {

        Entity casterEntity = BukkitAdapter.adapt(skillMetadata.getCaster().getEntity());
        Entity targetEntity = BukkitAdapter.adapt(abstractEntity);



        if(targetEntity instanceof Player){
            int amount = RoleSkillController.getSkillValue(casterEntity, targetEntity, this.amount);
            Player player = (Player) targetEntity;
            RolePlayerController.skillExpAdd(player, amount);

        }



        return SkillResult.INVALID_TARGET;
    }

}
