package com.daxton.rolemaster.controller;

import com.daxton.rolemaster.been.RolePlayer;
import com.daxton.rolemaster.been.role.RoleBeen;
import com.daxton.rolemaster.been.role.SkillBeen;
import com.daxton.unrealcore.application.base.YmlFileUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RolePlayerGetController {


    //獲取 基礎 點數 增加
    public static int getCostPointBase(int level){
        FileConfiguration levelConfig = RoleMasterController.getYmlFile("common/level.yml");
        return YmlFileUtil.getInt(levelConfig, "BaseLevel."+level+".Point", 0);
    }

    //獲取 技能 點數 增加
    public static int getCostPointSkill(int level){
        FileConfiguration levelConfig = RoleMasterController.getYmlFile("common/level.yml");
        return YmlFileUtil.getInt(levelConfig, "SkillLevel."+level+".Point", 0);
    }

    //獲取 基礎 屬性 花費
    public static int getAttributeCost(Player player, String attributeName){

        FileConfiguration attributeConfig = RoleMasterController.getYmlFile("common/attributes.yml");
        int baseNow = getAttributeBaseNow(player, attributeName)+1;

        return YmlFileUtil.getInt(attributeConfig, "AttributeUpgradeCost."+baseNow, 0);
    }

    //獲取 基礎 屬性 目前
    public static int getAttributeBaseNow(Player player, String attributeName){
        String uuidString = player.getUniqueId().toString();
        FileConfiguration dataConfig = RoleMasterController.getYmlFile("data/"+uuidString+".yml");
        return YmlFileUtil.getInt(dataConfig, "Base.Attributes."+attributeName, 0);
    }

    //獲取 基礎 屬性 目前
    public static int getAttributeBaseMax(String attributeName){
        FileConfiguration attributeConfig = RoleMasterController.getYmlFile("common/attributes.yml");
        return YmlFileUtil.getInt(attributeConfig, "Attributes."+attributeName+".Max", 0);
    }

    //獲取 玩家 魔量 最大
    public static int getManaMax(Player player){
        String uuidString = player.getUniqueId().toString();
        RolePlayer rolePlayer = RolePlayerController.rolePlayerMap.get(uuidString);
        return rolePlayer.getManaMax();
    }
    //獲取 玩家 魔量 目前
    public static int getManaNow(Player player){
        String uuidString = player.getUniqueId().toString();
        RolePlayer rolePlayer = RolePlayerController.rolePlayerMap.get(uuidString);
        return rolePlayer.getManaNow();
    }

    //獲取 玩家 魔量 回復
    public static int getManaRegeneration(Player player){
        RoleBeen roleBeen = getRoleBeen(player);
        return roleBeen.getManaRegeneration(player);
    }

    //獲取 玩家 綁定 技能名稱
    public static String getBindSKill(Player player, int key){
        String uuidString = player.getUniqueId().toString();
        FileConfiguration dataConfig = RoleMasterController.getYmlFile("data/"+uuidString+".yml");
        return YmlFileUtil.getString(dataConfig, "BindSkill."+key, "");
    }

    //獲取 玩家 技能 等級
    public static int getSkillLevelNow(Player player, String skillName){
        String uuidString = player.getUniqueId().toString();
        FileConfiguration dataConfig = RoleMasterController.getYmlFile("data/"+uuidString+".yml");
        String selectRote = dataConfig.getString("SelectRole");

        return YmlFileUtil.getInt(dataConfig, "RoleList."+selectRote+".Skill."+skillName, 0);
    }

    //獲取 技能 綁定 資料
    public static SkillBeen getSkillBeen(Player player, String skillName){
        RoleBeen roleBeen = getRoleBeen(player);
        return roleBeen.getSkillBeenMap().get(skillName);
    }

    //獲取 玩家 技能 列表
    public static List<SkillBeen> getSkillList(Player player){
        String uuidString = player.getUniqueId().toString();
        FileConfiguration dataConfig = RoleMasterController.getYmlFile("data/"+uuidString+".yml");
        String selectRote = dataConfig.getString("SelectRole");
        RoleBeen roleBeen = RoleMasterController.roleBeenMap.get(selectRote);
        return new ArrayList<>(roleBeen.getSkillBeenMap().values());
    }

    //獲取 玩家 資料
    public static RolePlayer getRolePlayer(Player player){
        String uuidString = player.getUniqueId().toString();
        return RolePlayerController.rolePlayerMap.get(uuidString);
    }

    //獲取 角色 設定
    public static RoleBeen getRoleBeen(Player player){
        String uuidString = player.getUniqueId().toString();
        FileConfiguration dataConfig = RoleMasterController.getYmlFile("data/"+uuidString+".yml");
        String selectRote = dataConfig.getString("SelectRole");
        RoleBeen roleBeen = RoleMasterController.roleBeenMap.get(selectRote);
        if(roleBeen == null){
            return RoleMasterController.roleBeenMap.get("Default");
        }
        return roleBeen;
    }

    //獲取 技能 點數 目前
    public static int getPointSkillNow(Player player){
        String uuidString = player.getUniqueId().toString();
        FileConfiguration dataConfig = RoleMasterController.getYmlFile("data/"+uuidString+".yml");
        String selectRote = dataConfig.getString("SelectRole");
        return YmlFileUtil.getInt(dataConfig, "RoleList."+selectRote+".Point", 0);
    }

    //獲取 技能 等級 目前
    public static int getLevelSkillNow(Player player){
        String uuidString = player.getUniqueId().toString();
        FileConfiguration dataConfig = RoleMasterController.getYmlFile("data/"+uuidString+".yml");
        String selectRote = dataConfig.getString("SelectRole");
        return YmlFileUtil.getInt(dataConfig, "RoleList."+selectRote+".Level", 0);
    }

    //獲取 技能 等級 最高
    public static int getLevelSkillMax(Player player){
        RoleBeen roleBeen = RolePlayerGetController.getRoleBeen(player);
        return roleBeen.getLevelSkillMax();
    }

    //獲取 技能 經驗 目前
    public static int getExpSkillNow(Player player){
        String uuidString = player.getUniqueId().toString();
        FileConfiguration dataConfig = RoleMasterController.getYmlFile("data/"+uuidString+".yml");
        String selectRote = dataConfig.getString("SelectRole");
        return YmlFileUtil.getInt(dataConfig, "RoleList."+selectRote+".Exp", 0);
    }

    //獲取 技能 經驗 最高
    public static int getExpSkillMax(Player player){
        FileConfiguration levelConfig = RoleMasterController.getYmlFile("common/level.yml");
        int level = RolePlayerGetController.getLevelSkillNow(player)+1;
        return YmlFileUtil.getInt(levelConfig, "SkillLevel."+level+".Exp", 0);
    }

    //獲取 基礎 點數 目前
    public static int getPointBaseNow(Player player){
        String uuidString = player.getUniqueId().toString();
        FileConfiguration dataConfig = RoleMasterController.getYmlFile("data/"+uuidString+".yml");
        return YmlFileUtil.getInt(dataConfig, "Base.Point", 0);
    }

    //獲取 基礎 等級 目前
    public static int getLevelBaseNow(Player player){
        String uuidString = player.getUniqueId().toString();
        FileConfiguration dataConfig = RoleMasterController.getYmlFile("data/"+uuidString+".yml");
        return YmlFileUtil.getInt(dataConfig, "Base.Level", 0);
    }

    //獲取 基礎 等級 最高
    public static int getLevelBaseMax(Player player){
        RoleBeen roleBeen = getRoleBeen(player);
        return roleBeen.getLevelBaseMax();
    }

    //獲取 基礎 經驗 目前
    public static int getExpBaseNow(Player player){
        String uuidString = player.getUniqueId().toString();
        FileConfiguration dataConfig = RoleMasterController.getYmlFile("data/"+uuidString+".yml");
        return YmlFileUtil.getInt(dataConfig, "Base.Exp", 0);
    }

    //獲取 基礎 經驗 最高
    public static int getExpBaseMax(Player player){
        FileConfiguration levelConfig = RoleMasterController.getYmlFile("common/level.yml");
        int level = RolePlayerGetController.getLevelBaseNow(player)+1;
        return YmlFileUtil.getInt(levelConfig, "BaseLevel."+level+".Exp", 0);
    }

}
