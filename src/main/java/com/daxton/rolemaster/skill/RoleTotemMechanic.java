package com.daxton.rolemaster.skill;

import com.daxton.rolemaster.RoleMaster;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.*;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.mechanics.TotemMechanic;
import io.lumine.mythic.core.skills.projectiles.Projectile;
import io.lumine.mythic.core.skills.projectiles.ProjectileBulletType;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;

@MythicMechanic(
        author = "Ashijin",
        name = "totem",
        aliases = {"toteme", "t"},
        description = "Creates a static totem projectile at the target"
)
public class RoleTotemMechanic extends Projectile implements ITargetedEntitySkill, ITargetedLocationSkill {

    protected PlaceholderInt maxCharges;
    protected float YOffset;

    public RoleTotemMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
        super(manager, file, skill, mlc);
        this.maxCharges = mlc.getPlaceholderInteger(new String[]{"charges", "ch", "c"}, 0, new String[0]);
        this.YOffset = mlc.getFloat(new String[]{"yoffset", "yo"}, 1.0F);
        this.stopOnHitEntity = mlc.getBoolean(new String[]{"stopatentity", "se"}, false);
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        try {
            new RoleTotemMechanic.TotemTracker(skillMetadata, abstractEntity.getLocation());
            return SkillResult.SUCCESS;
        } catch (Exception var4) {
            MythicLogger.error("An error occurred executing a Totem Mechanic", var4);
            return SkillResult.ERROR;
        }
    }

    @Override
    public SkillResult castAtLocation(SkillMetadata skillMetadata, AbstractLocation abstractLocation) {
        try {


            new RoleTotemMechanic.TotemTracker(skillMetadata, abstractLocation);
            return SkillResult.SUCCESS;
        } catch (Exception var4) {
            MythicLogger.error("An error occurred executing a Totem Mechanic", var4);
            return SkillResult.ERROR;
        }
    }

    private class TotemTracker extends Projectile.ProjectileTracker {
        private AbstractLocation position;
        private int charges;

        public TotemTracker(SkillMetadata data, AbstractLocation target) {
            super(data, target);
            this.position = target;
            this.charges = RoleTotemMechanic.this.maxCharges.get(data);

            this.start();
        }

        public void projectileStart() {
            this.startLocation = this.position;
            this.currentLocation = this.position;
            if (RoleTotemMechanic.this.YOffset != 0.0F) {
                this.currentLocation.setY(this.currentLocation.getY() + (double)RoleTotemMechanic.this.YOffset);
            }

            if (RoleTotemMechanic.this.bullet != null) {
                this.bullet = RoleTotemMechanic.this.bullet.create(this, (AbstractEntity)null);
            }

        }
        public void evaluatePotentialTargets(){

        }
        public void projectileMove() {
        }

        public void projectileTick() {
//            RoleMaster.sendLogger("Tick!");
            if (RoleTotemMechanic.this.onTickSkill.isPresent() && ((Skill)RoleTotemMechanic.this.onTickSkill.get()).isUsable(this.data)) {
                SkillMetadata sData = this.data.deepClone();
                AbstractLocation location;
                if (RoleTotemMechanic.this.bulletType.isPresent() && RoleTotemMechanic.this.bulletType.get() == ProjectileBulletType.ARROW) {
                    location = this.previousLocation.clone();
                } else {
                    location = this.currentLocation.clone();
                }

                HashSet<AbstractLocation> targets = new HashSet();
                targets.add(location);
                sData.setLocationTargets(targets);
                sData.setOrigin(location);
                ((Skill)RoleTotemMechanic.this.onTickSkill.get()).execute(sData);
            }

            this.evaluateTargetsInBB();


////            this.targets.clear();
////            this.targets.addAll(MythicBukkit.inst().getVolatileCodeHandler().getWorldHandler().getEntitiesNearLocation(position, 2));
//            this.inRange.addAll(MythicBukkit.inst().getVolatileCodeHandler().getWorldHandler().getEntitiesNearLocation(position, 2));
//
//            RoleMaster.sendLogger("命中: "+this.inRange.size()+" : "+this.targets.size());
////            this.inRange.forEach(abstractEntity -> RoleMaster.sendLogger(abstractEntity.getName()));

            if (!this.targets.isEmpty()) {

                this.doHit((Collection)this.targets.clone());
                if (RoleTotemMechanic.this.stopOnHitEntity) {
                    this.terminate();
                }

                --this.charges;
                if (RoleTotemMechanic.this.maxCharges.get(this.data) > 0 && this.charges <= 0) {
                    this.terminate();
                }
            }

            this.targets.clear();
        }

        public void doHit(Collection<AbstractEntity> targets) {
//            targets.forEach(abstractEntity -> RoleMaster.sendLogger("命中: "+abstractEntity.getName()));
            if (RoleTotemMechanic.this.onHitSkill.isPresent() && ((Skill)RoleTotemMechanic.this.onHitSkill.get()).isUsable(this.data)) {
                SkillMetadata sData = this.data.deepClone();
                sData.setEntityTargets(targets);
                sData.setOrigin(this.currentLocation.clone());
                ((Skill)RoleTotemMechanic.this.onHitSkill.get()).execute(sData);
            }

        }

        public void setCancelled() {
            this.terminate();
        }

        public boolean getCancelled() {
            return this.components.hasTerminated();
        }

    }

}
