package com.daxton.rolemaster.skill;

import com.daxton.rolemaster.RoleMaster;
import com.daxton.rolemaster.controller.RoleSkillController;
import com.daxton.unrealcore.application.UnrealCoreAPI;
import com.daxton.unrealcore.application.method.SchedulerFunction;
import com.daxton.unrealcore.been.entity.gecko.GeckoEntityModelBeen;
import com.daxton.unrealcore.been.entity.gecko.GeckoModelRemoveBeen;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.mechanics.CustomMechanic;
import org.bukkit.entity.Entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RoleModelMechanic implements ITargetedEntitySkill {

    private static Map<String, SchedulerFunction.RunTask> runTaskMap = new ConcurrentHashMap<>();

    //標記
    private final String mark;
    //模型ID
    private final String model_id;

    private final String animation;
    //持續時間
    private final String duration;

    public RoleModelMechanic(CustomMechanic customMechanic, MythicLineConfig mythicLineConfig) {
        this.mark = mythicLineConfig.getString(new String[]{"mark"}, "RoleSkill");
        this.model_id = mythicLineConfig.getString(new String[]{"m", "mid", "model", "modelid"}, "");
        this.animation = mythicLineConfig.getString(new String[]{"a", "animation"}, "");
        this.duration = mythicLineConfig.getString(new String[]{"duration","dt"}, "0");
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        Entity casterEntity = BukkitAdapter.adapt(skillMetadata.getCaster().getEntity());
        Entity targetEntity = BukkitAdapter.adapt(abstractEntity);
        String uuidString = targetEntity.getUniqueId().toString();


        if(model_id.isEmpty()){
            GeckoModelRemoveBeen geckoModelRemoveBeen = new GeckoModelRemoveBeen(uuidString, this.mark);
            UnrealCoreAPI.inst().getEntityHelper().removeEntityModel(geckoModelRemoveBeen);
            runTaskMap.remove(uuidString);
        }else {
            if(runTaskMap.containsKey(uuidString)){
                runTaskMap.get(uuidString).cancel();
                runTaskMap.remove(uuidString);
            }
            GeckoEntityModelBeen geckoEntityModelBeen = new GeckoEntityModelBeen(uuidString, this.mark, this.model_id);
            UnrealCoreAPI.inst().getEntityHelper().setEntityModel(geckoEntityModelBeen);

            int duration = RoleSkillController.getSkillValue(casterEntity, targetEntity, this.duration);
            if(duration > 0){
                SchedulerFunction.RunTask runTask = SchedulerFunction.runLater(RoleMaster.unrealCorePlugin.getJavaPlugin(), ()->{
                    GeckoModelRemoveBeen geckoModelRemoveBeen = new GeckoModelRemoveBeen(uuidString, this.mark);
                    UnrealCoreAPI.inst().getEntityHelper().removeEntityModel(geckoModelRemoveBeen);
                    runTaskMap.remove(uuidString);
                },duration);
                runTaskMap.put(uuidString, runTask);
            }

        }

        return SkillResult.INVALID_TARGET;
    }
}
