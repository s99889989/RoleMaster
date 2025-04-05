package com.daxton.rolemaster.controller;

import com.daxton.rolemaster.RoleMaster;
import com.daxton.unrealcore.application.base.YmlFileUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class RolePlayerAddController {

    //增加 技能 點數 目前
    public static void addPointSkillNow(Player player, String pointString){
        try {
            int point = Integer.parseInt(pointString);
            addPointSkillNow(player, point);
        }catch (NumberFormatException exception){
            //
        }
    }
    //增加 技能 點數 目前
    public static void addPointSkillNow(Player player, int point){
        String uuidString = player.getUniqueId().toString();
        FileConfiguration dataConfig = RoleMasterController.getYmlFile("data/"+uuidString+".yml");
        String selectRote = dataConfig.getString("SelectRole");
        int nowPoint = YmlFileUtil.getInt(dataConfig, "RoleList."+selectRote+".Point", 0);
        nowPoint += point;
        dataConfig.set("RoleList."+selectRote+".Point", nowPoint);
        RoleMasterController.save(player, dataConfig);
    }

}
