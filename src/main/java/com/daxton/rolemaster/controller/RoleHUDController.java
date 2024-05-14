package com.daxton.rolemaster.controller;

import com.daxton.rolemaster.RoleMaster;
import com.daxton.rolemaster.been.role.SkillBeen;
import com.daxton.unrealcore.application.UnrealCoreAPI;
import com.daxton.unrealcore.display.been.module.ModuleData;
import com.daxton.unrealcore.display.been.module.display.ImageModuleData;
import com.daxton.unrealcore.display.been.module.display.TextModuleData;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//HUD
public class RoleHUDController {

    public static Map<String, ModuleData> moduleDataMap = new HashMap<>();
    public static List<ModuleData> moduleDataList = new ArrayList<>();

    public static void load(){

        moduleDataList.clear();
        moduleDataMap.clear();

        RoleMasterController.getYmlFileList("hud").forEach(hudConfig -> {
            moduleDataList =  UnrealCoreAPI.getHUDList("", hudConfig);
        });

        moduleDataList.forEach(moduleData -> moduleDataMap.put(moduleData.getModuleID(), moduleData));
    }

    public static ModuleData getModuleData(String moduleID){
        return moduleDataMap.get(moduleID);
    }

    public static void reload(){
        Bukkit.getOnlinePlayers().forEach(player -> {
            UnrealCoreAPI.removeHUD(player, moduleDataList);
            setDefaultHUD(player);
        });
    }

    public static void setDefaultHUD(Player player){
        List<ModuleData> sendModuleDataList = new ArrayList<>(moduleDataList);
        sendModuleDataList.forEach(moduleData -> {
            String moduleID = moduleData.getModuleID();
            if(moduleData instanceof ImageModuleData){
                ImageModuleData imageModuleData = (ImageModuleData) moduleData;
                if(moduleID.startsWith("Bind_")){
                    String iString = moduleID.replace("Bind_", "");
                    try {
                        int i = Integer.parseInt(iString);

                        String bindSkillName = RolePlayerGetController.getBindSKill(player, i);
                        if(!bindSkillName.isEmpty()){
                            SkillBeen skillBeen = RolePlayerGetController.getSkillBeen(player, bindSkillName);
                            if(skillBeen != null){
                                List<String> stringList = new ArrayList<>();
                                stringList.add(skillBeen.getIcon());
                                imageModuleData.setImage(stringList);
                                imageModuleData.setTransparent(255+"");
                            }
                        }else {
                            imageModuleData.setTransparent(0+"");
                        }

                    }catch (NumberFormatException exception){
                        //
                    }

                }
            }

            if(moduleData instanceof TextModuleData){

            }

        });

        UnrealCoreAPI.setHUD(player, sendModuleDataList);
    }





}
