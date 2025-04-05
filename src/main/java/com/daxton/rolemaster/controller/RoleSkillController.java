package com.daxton.rolemaster.controller;

import com.daxton.rolemaster.RoleMaster;
import com.daxton.rolemaster.application.ActionString;
import com.daxton.rolemaster.application.ItemFunction;
import com.daxton.rolemaster.application.NumberUtil;
import com.daxton.rolemaster.application.aims.LookTarget;
import com.daxton.rolemaster.been.RolePlayer;
import com.daxton.rolemaster.been.role.SkillBeen;
import com.daxton.unrealcore.application.UnrealCoreAPI;
import com.daxton.unrealcore.application.base.StringConversionBasics;
import com.daxton.unrealcore.application.method.SchedulerFunction;
import com.daxton.unrealcore.display.been.module.display.ImageModuleData;
import io.lumine.mythic.bukkit.MythicBukkit;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;


import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RoleSkillController {

    //施法時間
    public static Map<String, SchedulerFunction.RunTask> castTimeMap = new HashMap<>();
    //執行次數
    public static Map<String, SchedulerFunction.RunTask> timesMap = new HashMap<>();

    public static void onSkill(Player player, String key){
        String uuidString = player.getUniqueId().toString();
        int i = getKeyInt(key);
        String bindSkillName = RolePlayerGetController.getBindSKill(player, i);
        if(!bindSkillName.isEmpty()){

            SkillBeen skillBeen = RolePlayerGetController.getSkillBeen(player, bindSkillName);
            if(skillBeen == null){
                return;
            }



            String itemID = ItemFunction.getItemID(player);
            if(!skillBeen.getNeedWeapons().isEmpty()){

                if(!skillBeen.getNeedWeapons().contains(itemID)){
                    return;
                }
            }

            int range = skillBeen.getTargetDistance(player);
            RolePlayer rolePlayer = RolePlayerGetController.getRolePlayer(player);

            if(rolePlayer.isCost(bindSkillName) || castTimeMap.containsKey(uuidString)){
               return;
            }


            //施法時間
            int castTIme = skillBeen.getCastTime(player);
            if(castTIme > 0){
                if(!castTimeMap.containsKey(uuidString)){


                    RoleEventController.skillChantStart(player, LookTarget.getLivingTarget(player, range));

                    ImageModuleData moduleData = (ImageModuleData) RoleHUDController.getModuleData("Cast");
                    int height =  StringConversionBasics.getInt(moduleData.getHeight(), 80);
                    moduleData.setHeight(0+"");
                    moduleData.setTransparent(255+"");
                    AtomicInteger ci = new AtomicInteger();
                    SchedulerFunction.RunTask runTask = SchedulerFunction.runTimer(RoleMaster.unrealCorePlugin.getJavaPlugin(), ()->{
                        ci.getAndIncrement();
                        double setHeight = (double) ci.get() / (castTIme*10)  * height;
                        if(ci.get() >= castTIme*10){
                            castTimeMap.get(uuidString).cancel();
                            moduleData.setTransparent(0+"");
                            moduleData.setHeight(height+"");
                            UnrealCoreAPI.setHUD(player, moduleData);
                            castTimeMap.remove(uuidString);
                            RoleEventController.skillChantEnd(player, LookTarget.getLivingTarget(player, range));
                            cast(rolePlayer, player, skillBeen, bindSkillName, i);
                        }else {
                            RoleEventController.skillChantTick(player, LookTarget.getLivingTarget(player, range));
                            moduleData.setHeight(setHeight+"");
                            UnrealCoreAPI.setHUD(player, moduleData);
                        }
                    }, 2, 2);
                    castTimeMap.put(uuidString, runTask);
                }

            }else {
                cast(rolePlayer, player, skillBeen, bindSkillName, i);
            }




        }
    }



    public static void cast(RolePlayer rolePlayer, Player player, SkillBeen skillBeen,  String bindSkillName, int i){
        //目標範圍
        int range = skillBeen.getTargetDistance(player);
        LivingEntity target = LookTarget.getLivingTarget(player, range);

        List<Entity> entityList = new ArrayList<>();
        List<Location> locationList = new ArrayList<>();

        //判斷是否需要目標
        boolean needTarget = skillBeen.isNeedTarget();
        if(needTarget){
            if(target == null){
                return;
            }
        }
        if(target != null){
            entityList.add(target);

            locationList.add(target.getLocation());

        }
//        entityList.add(player);
//        locationList.add(player.getLocation());
        //花費魔力
        int costMana = skillBeen.getCostMana(player);
        if(!rolePlayer.costMana(costMana)){
            return;
        }


        if(rolePlayer.costSkill(bindSkillName, skillBeen.getCD(player), i)){
            String uuidString = player.getUniqueId().toString();
            //執行次數
            int times = skillBeen.getTimes(player);


            if(times > 1){
                int interval = skillBeen.getInterval(player);
                AtomicInteger eTimes = new AtomicInteger();
                SchedulerFunction.RunTask runTask = SchedulerFunction.runTimer(RoleMaster.unrealCorePlugin.getJavaPlugin(), ()->{

                    eTimes.getAndIncrement();
                    if(eTimes.get() > times){
                        timesMap.get(uuidString).cancel();
                        timesMap.remove(uuidString);
                    }else {
                        skillBeen.getActionList().forEach(actionString -> {
                            Map<String, String> stringStringMap = ActionString.convertToMap(actionString);
                            String skillName = stringStringMap.get("skill");
                            MythicBukkit.inst().getAPIHelper().castSkill((Entity)player, skillName, (Entity)player, player.getLocation(), entityList, locationList, 1.0F);
                        });
                    }
                }, interval, interval);
                timesMap.put(uuidString, runTask);
            }else {
                skillBeen.getActionList().forEach(actionString -> {
                    Map<String, String> stringStringMap = ActionString.convertToMap(actionString);
                    String skillName = stringStringMap.get("skill");
                    MythicBukkit.inst().getAPIHelper().castSkill((Entity)player, skillName, (Entity)player, player.getLocation(), entityList, locationList, 1.0F);
                });
            }


            Map<String, String> roleValue = new HashMap<>();
            roleValue.put("role_mana_now", RolePlayerGetController.getManaNow(player)+"");
            roleValue.put("role_mana_max", RolePlayerGetController.getManaMax(player)+"");
            UnrealCoreAPI.setCustomValueMap(player, roleValue);
        }
    }

    //使用技能
    public static void useSkill(Player player, SkillBeen skillBeen){

        int nowLevel = RolePlayerGetController.getSkillLevelNow(player, skillBeen.getSkillName());
        if(!skillBeen.isPassive() || nowLevel < 1){
            return;
        }

        List<Entity> entityList = new ArrayList<>();
        List<Location> locationList = new ArrayList<>();

        String itemID = ItemFunction.getItemID(player);
        if(!skillBeen.getNeedWeapons().isEmpty()){
            if(!skillBeen.getNeedWeapons().contains(itemID)){
                return;
            }
        }

        skillBeen.getActionList().forEach(actionString -> {
            Map<String, String> stringStringMap = ActionString.convertToMap(actionString);
            String skillName = stringStringMap.get("skill");
            MythicBukkit.inst().getAPIHelper().castSkill((Entity)player, skillName, (Entity)player, player.getLocation(), entityList, locationList, 1.0F);
        });
    }

    //轉換佔位符值
    public static int getSkillValue(Entity casterEntity, Entity targetEntity, String numberString){
       return (int) getSkillValueDouble(casterEntity, targetEntity, numberString);
    }

    //轉換佔位符值
    public static String getSkillValueString(Entity casterEntity, Entity targetEntity, String numberString){
        Player player = null;
        if(casterEntity instanceof Player){
            player = (Player) casterEntity;
        }
        if(targetEntity != null){
            if(player == null && targetEntity instanceof Player){
                player = (Player) targetEntity;
            }
        }

        if(player != null){
            RolePlayer rolePlayer = RolePlayerGetController.getRolePlayer(player);
            return rolePlayer.getValueString(numberString);
        }
        return numberString;
    }

    //轉換佔位符值
    public static double getSkillValueDouble(Entity casterEntity, Entity targetEntity, String numberString){
        Player player = null;
        if(casterEntity instanceof Player){
            player = (Player) casterEntity;
        }
        if(targetEntity != null){
            if(player == null && targetEntity instanceof Player){
                player = (Player) targetEntity;
            }
        }

        if(player != null){
            RolePlayer rolePlayer = RolePlayerGetController.getRolePlayer(player);
            return rolePlayer.getValueInt(numberString);
        }else {
            return NumberUtil.countDouble(numberString);
        }
    }

    //把按鍵轉1~6
    public static int getKeyInt(String key){
        if(key.equals("Z")){
            return 1;
        }
        if(key.equals("X")){
            return 2;
        }
        if(key.equals("C")){
            return 3;
        }
        if(key.equals("V")){
            return 4;
        }
        if(key.equals("B")){
            return 5;
        }
        if(key.equals("N")){
            return 6;
        }
        return 0;
    }

}
