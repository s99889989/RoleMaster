package com.daxton.rolemaster.controller;

import com.daxton.rolemaster.RoleMaster;
import com.daxton.rolemaster.been.RolePlayer;
import com.daxton.rolemaster.been.role.RoleBeen;
import com.daxton.rolemaster.been.role.SkillBeen;
import com.daxton.unrealcore.application.UnrealCoreAPI;
import com.daxton.unrealcore.application.method.SchedulerFunction;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class RolePlayerController {

    //角色玩家
    public static Map<String, RolePlayer> rolePlayerMap = new HashMap<>();
    //魔力回復用
    public static SchedulerFunction.RunTask runTask;

    //改變角色
    public static void changeRole(Player player,String roleName){

        if(RoleMasterController.roleBeenMap.containsKey(roleName)){
            String uuidString = player.getUniqueId().toString();
            FileConfiguration dataConfig = RoleMasterController.getYmlFile("data/"+uuidString+".yml");
            dataConfig.set("SelectRole", roleName);
            dataConfig.set("BindSkill.1", "");
            dataConfig.set("BindSkill.2", "");
            dataConfig.set("BindSkill.3", "");
            dataConfig.set("BindSkill.4", "");
            dataConfig.set("BindSkill.5", "");
            dataConfig.set("BindSkill.6", "");
            RoleMasterController.save(dataConfig, "data/"+uuidString+".yml");
            logout(player);
            login(player);
            RolePlayerController.setDefaultValue(player);
            RoleHUDController.setDefaultHUD(player);
            RolePlayerController.initialization(player);
        }

    }

    //伺服器啟動
    public static void start(){
        runTask =SchedulerFunction.runTimer(RoleMaster.roleMaster, ()->{
            rolePlayerMap.forEach((s, rolePlayer) -> rolePlayer.regeneration());
        }, 0, 200);
    }
    //停止
    public static void stop(){
        runTask.cancel();
    }
    //登入
    public static void login(Player player){
        String uuidString = player.getUniqueId().toString();
        RolePlayer rolePlayer = new RolePlayer(player);
        rolePlayerMap.put(uuidString, rolePlayer);
        resetPlayer(player);
        usePassiveSkill(player);
    }
    //初始化
    public static void initialization(Player player){
        String uuidString = player.getUniqueId().toString();
        RolePlayer rolePlayer = rolePlayerMap.get(uuidString);
        rolePlayer.initialization();
    }

    //使用被動技能
    public static void usePassiveSkill(Player player){
        RoleBeen roleBeen = RolePlayerGetController.getRoleBeen(player);
        roleBeen.getSkillBeenMap().forEach((s, skillBeen) -> RoleSkillController.useSkill(player, skillBeen));
    }

    //登出
    public static void logout(Player player){
        String uuidString = player.getUniqueId().toString();
        RolePlayer rolePlayer = rolePlayerMap.get(uuidString);
        rolePlayer.save();
        rolePlayerMap.remove(uuidString);
    }

    //重設玩家值
    public static void resetPlayer(Player player){
        String uuidString = player.getUniqueId().toString();
        RolePlayer rolePlayer = rolePlayerMap.get(uuidString);
        rolePlayer.reset();


    }

    //設置玩家綁定
    public static void setBindSKill(Player player, int key, String skillName){
        String uuidString = player.getUniqueId().toString();
        FileConfiguration dataConfig = RoleMasterController.getYmlFile("data/"+uuidString+".yml");
        dataConfig.set("BindSkill."+key, skillName);
        RoleMasterController.save(dataConfig, "data/"+uuidString+".yml");
    }



    //選擇技能 升級
    public static void selectSkillUp(Player player, SkillBeen skillBeen){
        String skillName = skillBeen.getSkillName();
        String uuidString = player.getUniqueId().toString();
        FileConfiguration dataConfig = RoleMasterController.getYmlFile("data/"+uuidString+".yml");
        String selectRote = dataConfig.getString("SelectRole");
        int nowSkillLevel = RolePlayerGetController.getSkillLevelNow(player, skillName);
        int maxSkillLevel = skillBeen.getLevelMax();
        nowSkillLevel++;
        if(nowSkillLevel > maxSkillLevel){
            return;
        }
        int point = RolePlayerGetController.getPointSkillNow(player);
        if(point <= 0){
            return;
        }
        point--;
        dataConfig.set("RoleList."+selectRote+".Point", point);

        dataConfig.set("RoleList."+selectRote+".Skill."+skillName, nowSkillLevel);


        RoleMasterController.save(dataConfig, "data/"+uuidString+".yml");
    }

    //選擇技能 判斷 是否可以升級
    public static boolean selectSkillCanUp(Player player, SkillBeen skillBeen){
        int nowSkillLevel = RolePlayerGetController.getSkillLevelNow(player, skillBeen.getSkillName());
        int maxSkillLevel = skillBeen.getLevelMax();
        nowSkillLevel++;
        if(nowSkillLevel > maxSkillLevel){
            return false;
        }
        int point = RolePlayerGetController.getPointSkillNow(player);
        if(point <= 0){
            return false;
        }
        return true;
    }

    //屬性 升級
    public static void basePointAdd(Player player, String attrName){
        //目前屬性
        int attributeBaseNow = RolePlayerGetController.getAttributeBaseNow(player, attrName);
        //最高屬性
        int attributeBaseMax = RolePlayerGetController.getAttributeBaseMax(attrName);
//        RoleMaster.sendLogger(attrName+" 升級!"+attributeBaseNow+"/"+attributeBaseMax);
        if(attributeBaseNow >= attributeBaseMax){
            return;
        }


        //升級屬性花費點數
        int costPoint = RolePlayerGetController.getAttributeCost(player, attrName);
        //目前點數
        int nowPoint = RolePlayerGetController.getPointBaseNow(player);

        int newPoint = nowPoint - costPoint;
        if(newPoint < 0){
            return;
        }

        int nowAttr = attributeBaseNow+1;
//        RoleMaster.sendLogger(attrName+" : "+newPoint+" : "+nowAttr);
        String uuidString = player.getUniqueId().toString();
        FileConfiguration dataConfig = RoleMasterController.getYmlFile("data/"+uuidString+".yml");
        dataConfig.set("Base.Point", newPoint);
        dataConfig.set("Base.Attributes."+attrName, nowAttr);
        RoleMasterController.save(dataConfig, "data/"+uuidString+".yml");
    }

    //基礎 經驗 增加
    public static void baseExpAdd(Player player, int exp){
        int nowExp = RolePlayerGetController.getExpBaseNow(player);
        nowExp += exp;
        int maxExp = RolePlayerGetController.getExpBaseMax(player);
        while (nowExp >= maxExp){
            if(!baseLevelAdd(player, 1)){
                return;
            }
            nowExp = nowExp - maxExp;
            maxExp = RolePlayerGetController.getExpBaseMax(player);
        }
        String uuidString = player.getUniqueId().toString();
        FileConfiguration dataConfig = RoleMasterController.getYmlFile("data/"+uuidString+".yml");
        dataConfig.set("Base.Exp", nowExp);
        RoleMasterController.save(dataConfig, "data/"+uuidString+".yml");
    }

    //基礎 等級 增加
    public static boolean baseLevelAdd(Player player, int level){
        int nowLevel = RolePlayerGetController.getLevelBaseNow(player);
        int maxLevel = RolePlayerGetController.getLevelBaseMax(player);
        if(nowLevel >= maxLevel){
            return false;
        }
        int addPoint = RolePlayerGetController.getCostPointBase(nowLevel);
        nowLevel += level;
        int nowPoint = RolePlayerGetController.getPointBaseNow(player);
        nowPoint += addPoint;

        String uuidString = player.getUniqueId().toString();
        FileConfiguration dataConfig = RoleMasterController.getYmlFile("data/"+uuidString+".yml");
        dataConfig.set("Base.Level", nowLevel);
        dataConfig.set("Base.Point", nowPoint);
        RoleMasterController.save(dataConfig, "data/"+uuidString+".yml");

        //玩家 基礎等級升級 事件
        RoleEventController.levelBaseUp(player);

        return true;
    }

    //技能 經驗 增加
    public static void skillExpAdd(Player player, int exp){

        int nowExp = RolePlayerGetController.getExpSkillNow(player);
        nowExp += exp;
        int maxExp = RolePlayerGetController.getExpSkillMax(player);
        while (nowExp >= maxExp){
            if(!skillLevelAdd(player, 1)){
                return;
            }
            nowExp = nowExp - maxExp;
            maxExp = RolePlayerGetController.getExpSkillMax(player);
        }
        String uuidString = player.getUniqueId().toString();
        FileConfiguration dataConfig = RoleMasterController.getYmlFile("data/"+uuidString+".yml");
        String selectRote = dataConfig.getString("SelectRole");
        dataConfig.set("RoleList."+selectRote+".Exp", nowExp);
        RoleMasterController.save(dataConfig, "data/"+uuidString+".yml");
    }



    //技能 等級 增加
    public static boolean skillLevelAdd(Player player, int level){
        int nowLevel = RolePlayerGetController.getLevelSkillNow(player);
        int maxLevel = RolePlayerGetController.getLevelSkillMax(player);
        if(nowLevel >= maxLevel){
            return false;
        }

        int addPoint = RolePlayerGetController.getCostPointSkill(nowLevel);
        nowLevel += level;
        int nowPoint = RolePlayerGetController.getPointSkillNow(player);
        nowPoint += addPoint;

        String uuidString = player.getUniqueId().toString();
        FileConfiguration dataConfig = RoleMasterController.getYmlFile("data/"+uuidString+".yml");
        String selectRote = dataConfig.getString("SelectRole");

        dataConfig.set("RoleList."+selectRote+".Level", nowLevel);
        dataConfig.set("RoleList."+selectRote+".Point", nowPoint);
        RoleMasterController.save(dataConfig, "data/"+uuidString+".yml");

        //玩家 技能等級升級 事件
        RoleEventController.levelSkillUp(player);

        return true;
    }



    //設置
    public static void setDefaultValue(Player player){
        Map<String, String> roleValue = new HashMap<>();
        RoleBeen roleBeen = RolePlayerGetController.getRoleBeen(player);
        roleBeen.getSkillBeenMap().forEach((skillName, skillBeen) -> {
            roleValue.put("skill_"+skillName+"_name", skillBeen.getDisplayName());
            roleValue.put("skill_"+skillName+"_level_now", RolePlayerGetController.getSkillLevelNow(player, skillName)+"");
            roleValue.put("skill_"+skillName+"_level_max", skillBeen.getLevelMax()+"");
            roleValue.put("skill_"+skillName+"_value", skillBeen.getValue(player)+"");
            roleValue.put("skill_"+skillName+"_passive", skillBeen.isPassive()+"");
            roleValue.put("skill_"+skillName+"_need_weapons", skillBeen.getNeedWeapons()+"");
        });
        roleValue.put("role_name", roleBeen.getDisplayName());
        roleValue.put("role_icon", roleBeen.getIcon());
        roleValue.put("role_level_now", RolePlayerGetController.getLevelSkillNow(player)+"");
        roleValue.put("role_level_max", roleBeen.getLevelSkillMax()+"");
        roleValue.put("role_exp_now", RolePlayerGetController.getExpSkillNow(player)+"");
        roleValue.put("role_exp_max", RolePlayerGetController.getExpSkillMax(player)+"");
        roleValue.put("role_mana_now", RolePlayerGetController.getManaNow(player)+"");
        roleValue.put("role_mana_max", RolePlayerGetController.getManaMax(player)+"");
        roleValue.put("role_point", RolePlayerGetController.getPointSkillNow(player)+"");

        roleValue.put("select_skill_name", "");
        roleValue.put("select_skill_sp", "0");
        roleValue.put("select_skill_cool_down", "0");
        roleValue.put("select_skill_cast_time", "0");
        roleValue.put("select_skill_need_target", "false");
        roleValue.put("select_skill_value", "0");


        UnrealCoreAPI.setCustomValueMap(player, roleValue);
    }
    //設置
    public static void setDefaultValue(Player player, Map<String, String> roleValue){
        RoleBeen roleBeen = RolePlayerGetController.getRoleBeen(player);
        roleBeen.getSkillBeenMap().forEach((skillName, skillBeen) -> {
            roleValue.put("skill_"+skillName+"_name", skillBeen.getDisplayName());
            roleValue.put("skill_"+skillName+"_level_now", RolePlayerGetController.getSkillLevelNow(player, skillName)+"");
            roleValue.put("skill_"+skillName+"_level_max", skillBeen.getLevelMax()+"");
            roleValue.put("skill_"+skillName+"_value", skillBeen.getValue(player)+"");
            roleValue.put("skill_"+skillName+"_value2", skillBeen.getValue2(player)+"");
            roleValue.put("skill_"+skillName+"_value3", skillBeen.getValue3(player)+"");
            roleValue.put("skill_"+skillName+"_passive", skillBeen.isPassive()+"");
            roleValue.put("skill_"+skillName+"_need_weapons", skillBeen.getNeedWeapons()+"");
        });
        roleValue.put("role_name", roleBeen.getDisplayName());
        roleValue.put("role_level_now", RolePlayerGetController.getLevelSkillNow(player)+"");
        roleValue.put("role_level_max", roleBeen.getLevelSkillMax()+"");
        roleValue.put("role_exp_now", RolePlayerGetController.getExpSkillNow(player)+"");
        roleValue.put("role_exp_max", RolePlayerGetController.getExpSkillMax(player)+"");
        roleValue.put("role_mana_now", RolePlayerGetController.getManaNow(player)+"");
        roleValue.put("role_mana_max", RolePlayerGetController.getManaMax(player)+"");
        roleValue.put("role_point", RolePlayerGetController.getPointSkillNow(player)+"");

        roleValue.put("select_skill_name", "");
        roleValue.put("select_skill_sp", "0");
        roleValue.put("select_skill_cool_down", "0");
        roleValue.put("select_skill_cast_time", "0");
        roleValue.put("select_skill_need_target", "false");
        roleValue.put("select_skill_value", "0");

    }

    //設置
    public static void setValue(Player player, Map<String, String> roleValue){
        RoleBeen roleBeen = RolePlayerGetController.getRoleBeen(player);
        roleBeen.getSkillBeenMap().forEach((skillName, skillBeen) -> {
            roleValue.put("skill_"+skillName+"_level_now", RolePlayerGetController.getSkillLevelNow(player, skillName)+"");
            roleValue.put("skill_"+skillName+"_level_max", skillBeen.getLevelMax()+"");
            roleValue.put("skill_"+skillName+"_value", skillBeen.getValue(player)+"");
            roleValue.put("skill_"+skillName+"_value2", skillBeen.getValue2(player)+"");
            roleValue.put("skill_"+skillName+"_value3", skillBeen.getValue3(player)+"");
        });

        roleValue.put("role_level_now", RolePlayerGetController.getLevelSkillNow(player)+"");
        roleValue.put("role_level_max", roleBeen.getLevelSkillMax()+"");
        roleValue.put("role_exp_now", RolePlayerGetController.getExpSkillNow(player)+"");
        roleValue.put("role_exp_max", RolePlayerGetController.getExpSkillMax(player)+"");
        roleValue.put("role_mana_now", RolePlayerGetController.getManaNow(player)+"");
        roleValue.put("role_mana_max", RolePlayerGetController.getManaMax(player)+"");
        roleValue.put("role_point", RolePlayerGetController.getPointSkillNow(player)+"");

    }

    public static String replace(Player player, String inputString){
        RoleBeen roleBeen = RolePlayerGetController.getRoleBeen(player);

        for(SkillBeen skillBeen : roleBeen.getSkillBeenMap().values()){
            String skillName = skillBeen.getSkillName();
            inputString = inputString.replace("{custom_skill_"+skillName+"_name}", skillBeen.getDisplayName());
            inputString = inputString.replace("{custom_skill_"+skillName+"_level_now}", RolePlayerGetController.getSkillLevelNow(player, skillName)+"");
            inputString = inputString.replace("{custom_skill_"+skillName+"_level_max}", skillBeen.getLevelMax()+"");
        }

        inputString = inputString.replace("{custom_role_name}", roleBeen.getDisplayName());
        inputString = inputString.replace("{custom_role_level_now}", RolePlayerGetController.getLevelSkillNow(player)+"");
        inputString = inputString.replace("{custom_role_level_max}", roleBeen.getLevelSkillMax()+"");
        inputString = inputString.replace("{custom_role_exp_now}", RolePlayerGetController.getExpSkillNow(player)+"");
        inputString = inputString.replace("{custom_role_exp_max}", RolePlayerGetController.getExpSkillMax(player)+"");
        inputString = inputString.replace("{custom_role_point}", RolePlayerGetController.getPointSkillNow(player)+"");
        return inputString;
    }



    public static void setSelectValue(Player player, Map<String, String> roleValue, SkillBeen skillBeen){
        roleValue.put("select_skill_name", skillBeen.getDisplayName());
        roleValue.put("select_skill_sp", skillBeen.getCostMana(player)+"");
        roleValue.put("select_skill_cool_down", skillBeen.getCD(player)+"");
        roleValue.put("select_skill_cast_time", skillBeen.getCastTime(player)+"");
        roleValue.put("select_skill_need_target", skillBeen.isNeedTarget()+"");
        roleValue.put("select_skill_value", skillBeen.getValue(player)+"");
        roleValue.put("select_skill_value2", skillBeen.getValue2(player)+"");
        roleValue.put("select_skill_value3", skillBeen.getValue3(player)+"");

    }

}
