package com.daxton.rolemaster.skill;

import com.daxton.rolemaster.RoleMaster;
import com.daxton.rolemaster.controller.RoleSkillController;
import com.daxton.unrealcore.application.method.SchedulerFunction;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.mechanics.CustomMechanic;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class RoleAttributeMechanic implements ITargetedEntitySkill {

    //屬性
    private final Attribute attribute;
    //名稱
    private final String name;
    //值
    private final String amount;
    //持續時間
    private final String duration;

    public RoleAttributeMechanic(CustomMechanic customMechanic, MythicLineConfig mythicLineConfig) {

        this.attribute = mythicLineConfig.getEnum(new String[]{"attribute", "attr"}, Attribute.class, Attribute.GENERIC_LUCK, "Not a valid attribute given");
        this.name = mythicLineConfig.getString(new String[]{"n", "name"}, "");
        this.amount = mythicLineConfig.getString(new String[]{"a", "amount"}, "");
        this.duration = mythicLineConfig.getString(new String[]{"d", "duration"}, "60");

    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {

        Entity casterEntity = BukkitAdapter.adapt(skillMetadata.getCaster().getEntity());
        Entity targetEntity = BukkitAdapter.adapt(abstractEntity);

        if(targetEntity instanceof LivingEntity){
            LivingEntity livingEntity = (LivingEntity) targetEntity;

            AttributeInstance attributeInstance = livingEntity.getAttribute(attribute);
            if (attributeInstance != null) {
                int amount = RoleSkillController.getSkillValue(casterEntity, targetEntity, this.amount);
                int duration  = RoleSkillController.getSkillValue(casterEntity, targetEntity, this.duration);

                String targetUUID = targetEntity.getUniqueId().toString();
                String name = targetUUID+RoleSkillController.getSkillValueString(casterEntity, targetEntity, this.name);
                AttributeModifier modifier = new AttributeModifier(name, amount, AttributeModifier.Operation.ADD_NUMBER);

                attributeInstance.addModifier(modifier);
                if (duration <= 0) {
                    duration = 20;
                }
                SchedulerFunction.runLater(RoleMaster.roleMaster, ()->{
                    attributeInstance.removeModifier(modifier);
                }, duration);
            }

            return SkillResult.SUCCESS;
        }else {
            return SkillResult.INVALID_TARGET;
        }

    }

}
