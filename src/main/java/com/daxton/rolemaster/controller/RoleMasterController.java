package com.daxton.rolemaster.controller;

import com.daxton.rolemaster.RoleMaster;
import com.daxton.rolemaster.been.role.RoleBeen;
import com.daxton.rolemaster.gui.RoleAttributeGUI;
import com.daxton.rolemaster.gui.RoleSkillGUI;
import com.daxton.unrealcore.application.UnrealCoreAPI;
import com.daxton.unrealcore.application.base.PluginUtil;
import com.daxton.unrealcore.application.base.YmlFileUtil;
import com.daxton.unrealcore.application.method.SchedulerFunction;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class RoleMasterController {


    public static Map<String, RoleBeen> roleBeenMap = new HashMap<>();

    //讀取
    public static void load(){
        //建立設定檔
        PluginUtil.CreateConfig(RoleMaster.roleMaster);

        getRoleNameList().forEach(roleName -> {
            RoleBeen roleBeen = new RoleBeen(roleName);
            roleBeenMap.put(roleName, roleBeen);
        });

        PluginUtil.resourceCopy2(RoleMaster.roleMaster, "MythicMobs/Skills/RoleMaster.yml", "plugins/MythicMobs/Skills/RoleMaster.yml", false);

//        RoleMaster.sendLogger("路徑: "+RoleMaster.getResourceFolder());

        RoleHUDController.load();

    }

    //重新讀取設定
    public static void reload(){
        roleBeenMap.clear();
        load();
        RoleHUDController.reload();
    }

    //打開GUI
    public static void openGUI(Player player){
        FileConfiguration attributeConfig = getYmlFile("gui/RoleAttributeGUI.yml");
        RoleAttributeGUI roleAttributeGUI = new RoleAttributeGUI("RoleAttributeGUI", attributeConfig, player);
        UnrealCoreAPI.openGUI(player, roleAttributeGUI);
//        FileConfiguration skillConfig = getYmlFile("gui/RoleSkillGUI.yml");
//        RoleSkillGUI roleSkillGUI = new RoleSkillGUI("RoleSkillGUI", skillConfig, player);
//        UnrealCoreAPI.openGUI(player, roleSkillGUI);
    }

    //玩家登入
    public static void login(Player player){
        String uuidString = player.getUniqueId().toString();

        FileConfiguration fileConfiguration = getYmlFile("data/"+uuidString+".yml");

        //角色
        YmlFileUtil.set(fileConfiguration, "SelectRole", "Default");  //Default Archer
        //基本等級
        YmlFileUtil.set(fileConfiguration, "Base.Level", 0);
        //基本經驗
        YmlFileUtil.set(fileConfiguration, "Base.Exp", 0);
        //基本點數
        YmlFileUtil.set(fileConfiguration, "Base.Point", 0);
        //屬性
        FileConfiguration attrConfig = getYmlFile("common/attributes.yml");
        YmlFileUtil.sectionList(attrConfig, "Attributes").forEach(name -> {
            int value =  YmlFileUtil.getInt(fileConfiguration, "Base.Attributes."+name+".Base", 0);
            YmlFileUtil.set(fileConfiguration, "Base.Attributes."+name, value);
        });
        //技能綁定
        YmlFileUtil.set(fileConfiguration, "BindSkill.1", "");
        YmlFileUtil.set(fileConfiguration, "BindSkill.2", "");
        YmlFileUtil.set(fileConfiguration, "BindSkill.3", "");
        YmlFileUtil.set(fileConfiguration, "BindSkill.4", "");
        YmlFileUtil.set(fileConfiguration, "BindSkill.5", "");
        YmlFileUtil.set(fileConfiguration, "BindSkill.6", "");

        RoleBeen roleBeen = RolePlayerGetController.getRoleBeen(player);
        int mana = roleBeen.getMana(player);
        //魔量
        YmlFileUtil.set(fileConfiguration, "Mana", mana);

        roleBeenMap.forEach((roleName, roleBeenNow) -> {
            //技能等級
            YmlFileUtil.set(fileConfiguration, "RoleList."+roleName+".Level", 0);
            //技能點數
            YmlFileUtil.set(fileConfiguration, "RoleList."+roleName+".Point", 0);
            //技能經驗
            YmlFileUtil.set(fileConfiguration, "RoleList."+roleName+".Exp", 0);

            roleBeenNow.getSkillBeenMap().forEach((skillName, skillBeen) -> {
                //技能 指定 等級
                YmlFileUtil.set(fileConfiguration, "RoleList."+roleName+".Skill."+skillName, 0);  //0
            });
        });

        save(fileConfiguration, "data/"+uuidString+".yml");

        SchedulerFunction.runLater(RoleMaster.roleMaster, ()->{
            RolePlayerController.setDefaultValue(player);
        }, 20);

    }

    //獲取所有角色名稱
    public static List<String> getRoleNameList(){
        File file = new File(RoleMaster.getResourceFolder()+"role");

        List<String> stringList = new ArrayList<>();
        if(file.exists() && file.list() != null){
            stringList.addAll(Arrays.asList(Objects.requireNonNull(file.list())));
        }

        return stringList;
    }

    //獲取所有角色技能名稱
    public static List<String> getSkillList(String roleName){
        File file = new File(RoleMaster.getResourceFolder()+"role/"+roleName+"/skill");

        List<String> stringList = new ArrayList<>();
        if(file.exists() && file.list() != null){
            for(String name : Objects.requireNonNull(file.list())){
                if (name.endsWith(".yml")) {
                    name = name.replace(".yml", "");
                    stringList.add(name);
                }
            }
        }

        return stringList;
    }

    //存檔
    public static void save(FileConfiguration fileConfiguration, String path){
        File file = new File(RoleMaster.getResourceFolder(), path);
        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //從插件預設路徑獲取YML檔案
    public static List<FileConfiguration> getYmlFileList(String path){
        return YmlFileUtil.findFileConfiguration(RoleMaster.getResourceFolder()+path);

    }
    //從插件預設路徑獲取YML檔案
    public static FileConfiguration getYmlFile(String path){

        File file = new File(RoleMaster.getResourceFolder(), path);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }

}
