package com.daxton.rolemaster.api.entity;

import com.daxton.rolemaster.RoleMaster;
import com.daxton.rolemaster.nms.v1_20_R3.PacketRoleEntity;
import com.daxton.rolemaster.skill.RoleHoloModelMechanic;
import com.daxton.rolemaster.skill.RoleHoloTextMechanic;
import com.daxton.unrealcore.application.UnrealCoreAPI;
import com.daxton.unrealcore.application.method.SchedulerFunction;

import com.daxton.unrealcore.been.entity.gecko.GeckoEntityAngleBeen;
import com.daxton.unrealcore.been.entity.gecko.GeckoEntityAnimationsBeen;
import com.daxton.unrealcore.been.entity.gecko.GeckoEntityModelBeen;
import com.daxton.unrealcore.been.entity.gecko.GeckoModelRemoveBeen;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


import org.bukkit.Location;


import org.bukkit.entity.Entity;


@Getter
@Setter
@ToString
@NoArgsConstructor
public class RoleHolo {

    private String mark = "";

    private PacketRoleEntity packetRoleEntity;

    private SchedulerFunction.RunTask runTask = null;


    public RoleHolo(String mark, Entity casterEntity, Location inputLocation, double x, double y, double z, boolean caster_pitch, boolean caster_yaw) {
        this.mark = mark;
        Location location = inputLocation.clone().add(x, y, z);

        location.setYaw(casterEntity.getLocation().getYaw());
        location.setPitch(casterEntity.getLocation().getPitch());

        if(location.getWorld() != null){

            packetRoleEntity = new PacketRoleEntity(location);
            packetRoleEntity.setCaster_yaw(caster_yaw);
            packetRoleEntity.setCaster_pitch(caster_pitch);
            packetRoleEntity.spawn();
            packetRoleEntity.setVisible(false);

        }

    }

    public RoleHolo(String mark, Entity casterEntity, Location inputLocation, double x, double y, double z) {
        this.mark = mark;
        Location location = inputLocation.clone().add(x, y, z);

        location.setYaw(casterEntity.getLocation().getYaw());
        location.setPitch(casterEntity.getLocation().getPitch());

        if(location.getWorld() != null){

            packetRoleEntity = new PacketRoleEntity(location);
            packetRoleEntity.spawn();
            packetRoleEntity.setVisible(false);

        }

    }
    //加上玩家仰角
    public void setCaster_pitch(boolean caster_pitch) {
        packetRoleEntity.setCaster_pitch(caster_pitch);
    }
    //加上玩家偏航角
    public void setCaster_yaw(boolean caster_yaw) {
        packetRoleEntity.setCaster_yaw(caster_yaw);
    }

    //設置動畫
    public void setAnimation(String animation){
        if(this.packetRoleEntity == null || animation.isEmpty()){
            return;
        }
        String uuidString = packetRoleEntity.getUUIDString();
        GeckoEntityAnimationsBeen geckoEntityAnimationsBeen = new GeckoEntityAnimationsBeen(uuidString, this.mark, animation, 0, 0);
        UnrealCoreAPI.inst().getEntityHelper().setEntityAnimations(geckoEntityAnimationsBeen);
    }

    //設置模型
    public void setModel(String modelID){
        if(this.packetRoleEntity == null){
            return;
        }
        String uuidString = packetRoleEntity.getUUIDString();
        if(modelID.isEmpty()){
            GeckoModelRemoveBeen geckoModelRemoveBeen = new GeckoModelRemoveBeen(uuidString, this.mark);
            UnrealCoreAPI.inst().getEntityHelper().removeEntityModel(geckoModelRemoveBeen);
        }else {
            float pitch = 0;
            if(packetRoleEntity.isCaster_pitch()){
                pitch = this.packetRoleEntity.getPitch();
            }
            GeckoEntityModelBeen geckoEntityModelBeen = new GeckoEntityModelBeen(uuidString, this.mark, modelID);
            UnrealCoreAPI.inst().getEntityHelper().setEntityModel(geckoEntityModelBeen);
            GeckoEntityAngleBeen geckoEntityAngleBeen = new GeckoEntityAngleBeen(uuidString, this.mark, pitch, 0);
            UnrealCoreAPI.inst().getEntityHelper().setEntityAngle(geckoEntityAngleBeen);
//            UnrealCoreAPI.entityModelSet(uuidString, modelID, true);
        }

    }





    //設置持續時間
    public void setDuration(int duration){
        if(duration < 1){
            return;
        }
        if(runTask != null){
            runTask.cancel();
        }
        runTask = SchedulerFunction.runLater(RoleMaster.unrealCorePlugin.getJavaPlugin(), ()->{
            remove();
        },duration);
    }

    //設置顯示內容
    public void setMessage(String message){
        if(this.packetRoleEntity == null){
            return;
        }
        this.packetRoleEntity.setCustomName(message, !message.isEmpty());
    }
    //設置位置
    public void setLocation(Location inputLocation, double x, double y, double z){
        if(this.packetRoleEntity == null){
            return;
        }

        Location location = inputLocation.clone().add(x, y, z);

        this.packetRoleEntity.teleport(location);
    }

    //刪除
    public void remove(){
        if(this.packetRoleEntity == null){
          return;
        }
        if(runTask != null){
            runTask.cancel();
        }
        this.packetRoleEntity.remove();
        RoleHoloModelMechanic.roleHoloMap.remove(this.mark);
        RoleHoloTextMechanic.roleHoloMap.remove(this.mark);
    }



}
