package com.daxton.rolemaster.skill;

import com.daxton.rolemaster.RoleMaster;
import com.daxton.rolemaster.been.RolePlayer;
import com.daxton.rolemaster.been.role.SkillBeen;
import com.daxton.rolemaster.controller.RolePlayerController;
import com.daxton.rolemaster.controller.RolePlayerGetController;
import com.daxton.unrealcore.application.base.StringConversionBasics;
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
import org.bukkit.entity.Player;

public class RoleManaMechanic implements ITargetedEntitySkill {

    private final String skill;
    private final String amount;

    private final String sound;

    public RoleManaMechanic(CustomMechanic customMechanic, MythicLineConfig mythicLineConfig) {
        this.skill = mythicLineConfig.getString(new String[]{"s", "skill"}, "");
        this.amount = mythicLineConfig.getString(new String[]{"a", "amount"}, "0");
        this.sound = mythicLineConfig.getString(new String[]{"sound"}, "");
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {

        Entity targetEntity = BukkitAdapter.adapt(abstractEntity);
        Location location = targetEntity.getLocation();
        if(location.getWorld() != null){
            location.getWorld().playSound(location, sound, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }


        if(targetEntity instanceof Player){
            Player player = (Player) targetEntity;
            RolePlayer rolePlayer = RolePlayerGetController.getRolePlayer(player);
            int amount = StringConversionBasics.getInt(this.amount, 0);
            rolePlayer.regenerationMana(amount);


        }



        return SkillResult.INVALID_TARGET;
    }

}
