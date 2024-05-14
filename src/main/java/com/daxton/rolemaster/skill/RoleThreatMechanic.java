package com.daxton.rolemaster.skill;

import com.daxton.rolemaster.controller.RoleSkillController;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.mechanics.CustomMechanic;

public class RoleThreatMechanic implements ITargetedEntitySkill {

    String mode;
    String amountString;

    public RoleThreatMechanic(CustomMechanic customMechanic, MythicLineConfig mythicLineConfig) {

        this.mode = mythicLineConfig.getString(new String[]{"mode", "m"}, "Add");
        this.amountString = mythicLineConfig.getString(new String[]{"amount", "a"}, "1");

    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {

        ActiveMob activeMob = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(abstractEntity.getBukkitEntity());
        if(activeMob == null){

            return SkillResult.INVALID_CONFIG;
        }

        AbstractEntity casterEntity = skillMetadata.getCaster().getEntity();

        if (!casterEntity.isDead() && !(casterEntity.getHealth() <= 0.0)) {
            if (activeMob.getThreatTable() == null) {
                return SkillResult.INVALID_CONFIG;
            } else {
                if (MythicBukkit.inst().getMobManager().isActiveMob(casterEntity)) {
                    ActiveMob amt = MythicBukkit.inst().getMobManager().getMythicMobInstance(casterEntity);
                    if (activeMob.hasFaction() && activeMob.getFaction().equals(amt.getFaction())) {

                        return SkillResult.INVALID_TARGET;
                    }
                }
                double amount = RoleSkillController.getSkillValue(BukkitAdapter.adapt(casterEntity), BukkitAdapter.adapt(abstractEntity), amountString);

                switch (mode.toLowerCase()) {
                    case "add":
                        if (amount > 0.0) {

                            activeMob.getThreatTable().threatGain(casterEntity, amount);
                        } else {
                            activeMob.getThreatTable().threatLoss(casterEntity, amount * -1.0);
                        }
                        break;
                    case "remove":
                        if (amount > 0.0) {
                            activeMob.getThreatTable().threatLoss(casterEntity, amount);
                        } else {
                            activeMob.getThreatTable().threatGain(casterEntity, amount * -1.0);
                        }
                        break;
                    case "multiply":
                        activeMob.getThreatTable().threatSet(casterEntity, activeMob.getThreatTable().getThreat(casterEntity) * amount);
                        break;
                    case "divide":
                        if (amount == 0.0) {
                            return SkillResult.CONDITION_FAILED;
                        }

                        activeMob.getThreatTable().threatSet(casterEntity, activeMob.getThreatTable().getThreat(casterEntity) / amount);
                        break;
                    case "set":
                        activeMob.getThreatTable().threatSet(casterEntity, amount);
                        break;
                    case "reset":
                    case "delete":
                        activeMob.getThreatTable().threatLoss(casterEntity, activeMob.getThreatTable().getThreat(casterEntity));
                        break;
                    case "forcetop":
                    case "force":
                    case "topthreat":
                    case "top":
                        activeMob.getThreatTable().threatSet(casterEntity, activeMob.getThreatTable().getTopTargetThreat() + 1.0);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + mode);
                }

                return SkillResult.SUCCESS;
            }
        } else {
            return SkillResult.INVALID_TARGET;
        }
    }

}
