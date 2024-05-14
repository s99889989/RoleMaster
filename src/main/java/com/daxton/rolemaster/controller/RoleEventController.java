package com.daxton.rolemaster.controller;

import com.daxton.rolemaster.application.ActionString;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoleEventController {

    //玩家 技能等級 升級
    public static void levelSkillUp(Player player){
        FileConfiguration eventConfig = RoleMasterController.getYmlFile("common/event.yml");
        List<Entity> entityList = new ArrayList<>();
        List<Location> locationList = new ArrayList<>();
        eventConfig.getStringList("LevelSkillUpEvent").forEach(actionString->{
            Map<String, String> stringStringMap = ActionString.convertToMap(actionString);
            String skillName = stringStringMap.get("skill");
            MythicBukkit.inst().getAPIHelper().castSkill((Entity)player, skillName, (Entity)player, player.getLocation(), entityList, locationList, 1.0F);
        });
    }

    //玩家 基礎等級 升級
    public static void levelBaseUp(Player player){
        FileConfiguration eventConfig = RoleMasterController.getYmlFile("common/event.yml");
        List<Entity> entityList = new ArrayList<>();
        List<Location> locationList = new ArrayList<>();
        eventConfig.getStringList("LevelBaseUpEvent").forEach(actionString->{
            Map<String, String> stringStringMap = ActionString.convertToMap(actionString);
            String skillName = stringStringMap.get("skill");
            MythicBukkit.inst().getAPIHelper().castSkill((Entity)player, skillName, (Entity)player, player.getLocation(), entityList, locationList, 1.0F);
        });
    }

    //玩家 技能吟唱 開始
    public static void skillChantStart(Player player, LivingEntity target){
        FileConfiguration eventConfig = RoleMasterController.getYmlFile("common/event.yml");
        List<Entity> entityList = new ArrayList<>();
        List<Location> locationList = new ArrayList<>();
        eventConfig.getStringList("SkillChantStartEvent").forEach(actionString->{
            Map<String, String> stringStringMap = ActionString.convertToMap(actionString);
            String skillName = stringStringMap.get("skill");
            MythicBukkit.inst().getAPIHelper().castSkill((Entity)player, skillName, (Entity)player, player.getLocation(), entityList, locationList, 1.0F);
        });
    }

    //玩家 技能吟唱 開始
    public static void skillChantTick(Player player, LivingEntity target){
        FileConfiguration eventConfig = RoleMasterController.getYmlFile("common/event.yml");
        List<Entity> entityList = new ArrayList<>();
        List<Location> locationList = new ArrayList<>();
        eventConfig.getStringList("SkillChantTickEvent").forEach(actionString->{
            Map<String, String> stringStringMap = ActionString.convertToMap(actionString);
            String skillName = stringStringMap.get("skill");
            MythicBukkit.inst().getAPIHelper().castSkill((Entity)player, skillName, (Entity)player, player.getLocation(), entityList, locationList, 1.0F);
        });
    }


    //玩家 技能吟唱 結束
    public static void skillChantEnd(Player player, LivingEntity target){
        FileConfiguration eventConfig = RoleMasterController.getYmlFile("common/event.yml");
        List<Entity> entityList = new ArrayList<>();
        List<Location> locationList = new ArrayList<>();
        eventConfig.getStringList("SkillChantEndEvent").forEach(actionString->{
            Map<String, String> stringStringMap = ActionString.convertToMap(actionString);
            String skillName = stringStringMap.get("skill");
            MythicBukkit.inst().getAPIHelper().castSkill((Entity)player, skillName, (Entity)player, player.getLocation(), entityList, locationList, 1.0F);
        });
    }

}
