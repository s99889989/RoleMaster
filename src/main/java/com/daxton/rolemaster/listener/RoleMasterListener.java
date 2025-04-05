package com.daxton.rolemaster.listener;

import com.daxton.rolemaster.RoleMaster;
import com.daxton.rolemaster.application.Convert;
import com.daxton.rolemaster.been.RolePlayer;
import com.daxton.rolemaster.controller.*;
import com.daxton.rolemaster.skill.*;
import com.daxton.rolemaster.type.DamageType;
import com.daxton.unrealcore.common.event.PlayerKeyBoardEvent;
import com.daxton.unrealcore.communication.event.PlayerConnectionSuccessfulEvent;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.mechanics.CustomMechanic;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.Locale;

public class RoleMasterListener implements Listener {

    @EventHandler//當玩家連線成功
    public void onPlayerJoin(PlayerConnectionSuccessfulEvent event) {
        if(Bukkit.getServer().getPluginManager().getPlugin("UnrealResource") != null){
            return;
        }
        Player player = event.getPlayer();
        RoleHUDController.setDefaultHUD(player);

        AttributeInstance attributeInstance = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        if (attributeInstance != null) {
            for(AttributeModifier attributeModifier : attributeInstance.getModifiers()){
                String attrName = attributeModifier.getName();
                attributeInstance.removeModifier(attributeModifier);
            }
        }
    }

    public static void removeNearbyArmorStands(Player player, double distance) {
        // 获取玩家位置
        Location playerLocation = player.getLocation();

        // 循环遍历所有盔甲架
        for (ArmorStand armorStand : player.getWorld().getEntitiesByClass(ArmorStand.class)) {
            // 检查盔甲架与玩家的距离
            double distanceSquared = armorStand.getLocation().distanceSquared(playerLocation);

            // 如果距离小于等于指定距离的平方，则删除盔甲架
            if (distanceSquared <= distance * distance) {
                armorStand.remove();
            }
        }
    }

    //玩家登入
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();

        RoleMasterController.login(player);

        RolePlayerController.login(player);

        RolePlayerController.initialization(player);



    }
    //玩家登出
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        RolePlayerController.logout(player);
    }

    //按下按鍵
    @EventHandler
    public void onPlayerKeyBoard(PlayerKeyBoardEvent event){
        Player player = event.getPlayer();
        String uuidString = player.getUniqueId().toString();
        int keyID = event.getKeyID();
        String keyName = event.getKeyName();
        boolean inputNow = event.isInputNow();
        int keyAction = event.getKeyAction();

        if(!inputNow){
            if(keyAction == 1){
                if(keyName.equals("Z") || keyName.equals("X") || keyName.equals("C") || keyName.equals("V") || keyName.equals("B") || keyName.equals("N")){

                    RoleSkillController.onSkill(player, keyName);
                }


            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if(event.getEntity().getKiller() != null){
            Player player = event.getEntity().getKiller();

            RolePlayerController.skillExpAdd(player, 4000);
            RolePlayerController.baseExpAdd(player, 1000);
        }

    }

    @EventHandler
    public void onMythicMechanicLoadEvent(MythicMechanicLoadEvent event) {
        CustomMechanic customMechanic = event.getContainer();
        SkillExecutor manager =customMechanic.getManager();
        File file = customMechanic.getFile();
        String skill = customMechanic.getInternalName();

        MythicLineConfig mythicLineConfig = event.getConfig();

        String name = event.getMechanicName();
        switch (name.toUpperCase(Locale.ENGLISH)) {
            case "ROLEDAMAGE":  //傷害
                event.register(new RoleDamageMechanic(customMechanic, mythicLineConfig));
                break;
            case "ROLEMANA":  //魔力
                event.register(new RoleManaMechanic(customMechanic, mythicLineConfig));
                break;
            case "ROLEMODEL": //目標模型
                event.register(new RoleModelMechanic(customMechanic, mythicLineConfig));
                break;
            case "ROLEHOLOMODEL":  //全息模型
                event.register(new RoleHoloModelMechanic(customMechanic, mythicLineConfig));
                break;
            case "ROLETHREAT": //仇恨值
                event.register(new RoleThreatMechanic(customMechanic, mythicLineConfig));
                break;
            case "ROLEEXP": //經驗
                event.register(new RoleExpMechanic(customMechanic, mythicLineConfig));
                break;
            case "ROLEHEAL": //經驗
                event.register(new RoleHealMechanic(customMechanic, mythicLineConfig));
                break;
            case "ROLEATTRIBUTE": //附加屬性
                event.register(new RoleAttributeMechanic(customMechanic, mythicLineConfig));
                break;
            case "ROLESTUN": //暈眩
                event.register(new RoleStunMechanic(customMechanic, mythicLineConfig));
                break;
            case "ROLEHOLOTEXT": //全息文字
                event.register(new RoleHoloTextMechanic(customMechanic, mythicLineConfig));
                break;
            case "ROLETOTEM": //圖騰
                event.register(new RoleTotemMechanic(manager, file, skill, mythicLineConfig));
                break;
            case "ROLEITEMATTRIBUTE": //物品屬性
                event.register(new RoleItemAttributeMechanic(customMechanic, mythicLineConfig));
                break;
            case "ROLEITEMDAMAGE": //物品傷害
                event.register(new RoleItemDamageMechanic(customMechanic, mythicLineConfig));
                break;
            case "ROLEARROWVELOCITY": //物品傷害
                event.register(new RoleArrowVelocityMechanic(customMechanic, mythicLineConfig));
                break;
        }
    }

    //物理攻擊
    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageByEntityEvent event){

        Entity damager = event.getDamager();
        Player player = Convert.convertPlayer(damager);
        //攻擊者是玩家
        if(player != null){
//            EntityDamageEvent.DamageCause  damageCause = event.getCause();
            double a = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue();


            RolePlayer rolePlayer = RolePlayerGetController.getRolePlayer(player);

            double damage = event.getDamage();
            double damageFinal = event.getFinalDamage();
            double damageItem = rolePlayer.getDamage(damageFinal);

            DamageType damageType = DamageType.getDamageType(damager, event.getDamage());

            double criticalPower = rolePlayer.getCriticalPoser();

            double stateNumber = rolePlayer.getAttack(damageType, damage, damageFinal, damageItem) * criticalPower;


//            RoleMaster.sendLogger("暴擊倍率: "+criticalPower);
//            RoleMaster.sendLogger("傷害類型: "+damageType+" : "+stateNumber+" : "+a);


            event.setDamage(stateNumber);
        }

        Entity attacked = event.getEntity();
        if(attacked instanceof Player){
            Player playerED = (Player) attacked;
            RolePlayer rolePlayer = RolePlayerGetController.getRolePlayer(playerED);
            boolean dodgeSuccessful = rolePlayer.getDodgeSuccessful();
            if(dodgeSuccessful){
                event.setCancelled(true);
            }

        }

    }

    //發射箭矢
    @EventHandler
    public void onArrowShoot(EntityShootBowEvent event) {
        Entity shooter = event.getEntity();
        if (shooter instanceof Player) {
            Player player = (Player) shooter;
            RolePlayer rolePlayer = RolePlayerGetController.getRolePlayer(player);
            Arrow arrow = (Arrow) event.getProjectile();
            Vector vector = arrow.getVelocity();

            double amount = vector.getX()+vector.getY()+vector.getZ();
            rolePlayer.setArrowVelocityLsat((int) amount);
//            RoleMaster.sendLogger(vector.toString());
//            RoleMaster.sendLogger("值: "+amount);
            arrow.setVelocity(arrow.getVelocity().multiply(1 + rolePlayer.getArrowVelocity()));
        }
    }

}
