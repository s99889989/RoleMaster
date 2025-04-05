package com.daxton.rolemaster.skill;

import com.daxton.rolemaster.RoleMaster;
import com.daxton.rolemaster.been.RolePlayer;
import com.daxton.rolemaster.been.item.damage.ItemDamage;
import com.daxton.rolemaster.controller.RolePlayerController;
import com.daxton.rolemaster.controller.RolePlayerGetController;
import com.daxton.rolemaster.controller.RoleSkillController;
import com.daxton.unrealcore.application.UnrealCoreAPI;
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

public class RoleItemDamageMechanic implements ITargetedEntitySkill {

    //標記
    private final String mark;
    //值
    private final String amount;
    //物品
    private final String item;
    //是否移除
    private final boolean remove;
    //持續時間
    private final String duration;

    public RoleItemDamageMechanic(CustomMechanic customMechanic, MythicLineConfig mythicLineConfig) {

        this.mark = mythicLineConfig.getString(new String[]{"m", "mark"}, "");
        this.amount = mythicLineConfig.getString(new String[]{"a", "amount"}, "0");
        this.item = mythicLineConfig.getString(new String[]{"item", "i"}, "");
        this.remove = mythicLineConfig.getBoolean(new String[]{"remove", "r"}, false);
        this.duration = mythicLineConfig.getString(new String[]{"duration","dt"}, "60");

    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {

        Entity casterEntity = BukkitAdapter.adapt(skillMetadata.getCaster().getEntity());
        Entity targetEntity = BukkitAdapter.adapt(abstractEntity);

        if(targetEntity instanceof Player){
            Player player = (Player) targetEntity;

            int amount = RoleSkillController.getSkillValue(casterEntity, targetEntity, this.amount);
            String mark = RoleSkillController.getSkillValueString(casterEntity, targetEntity, this.mark);
            int duration = RoleSkillController.getSkillValue(casterEntity, targetEntity, this.duration);

            RolePlayer rolePlayer = RolePlayerGetController.getRolePlayer(player);
            ItemDamage itemDamage = new ItemDamage();
            if(rolePlayer.getItemDamageMap().containsKey(this.item)){
                itemDamage = rolePlayer.getItemDamageMap().get(this.item);
            }
            rolePlayer.getItemDamageMap().put(this.item, itemDamage);

            if(remove){
                itemDamage.removeDamage(mark);
            }else {

                itemDamage.setDamage(mark, amount);
                if(duration > 0){
                    ItemDamage finalItemDamage = itemDamage;
                    SchedulerFunction.runLater(RoleMaster.unrealCorePlugin.getJavaPlugin(), ()->{
                        finalItemDamage.removeDamage(mark);
                    },duration);
                }
            }


        }

        return SkillResult.INVALID_TARGET;
    }
}
