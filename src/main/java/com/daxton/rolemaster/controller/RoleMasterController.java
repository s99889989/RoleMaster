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
        createConfig();

        getRoleNameList().forEach(roleName -> {
            RoleBeen roleBeen = new RoleBeen(roleName);
            roleBeenMap.put(roleName, roleBeen);
        });

        PluginUtil.resourceCopy2(RoleMaster.unrealCorePlugin.getJavaPlugin(), "MythicMobs/Skills/RoleMaster.yml", "plugins/MythicMobs/Skills/RoleMaster.yml", false);

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

        SchedulerFunction.runLater(RoleMaster.unrealCorePlugin.getJavaPlugin(), ()->{
            RolePlayerController.setDefaultValue(player);
        }, 20);

    }

    //獲取所有角色名稱
    public static List<String> getRoleNameList(){
        File file = new File(RoleMaster.unrealCorePlugin.getResourceFolder()+"role");

        List<String> stringList = new ArrayList<>();
        if(file.exists() && file.list() != null){
            stringList.addAll(Arrays.asList(Objects.requireNonNull(file.list())));
        }

        return stringList;
    }

    //獲取所有角色技能名稱
    public static List<String> getSkillList(String roleName){
        File file = new File(RoleMaster.unrealCorePlugin.getResourceFolder()+"role/"+roleName+"/skill");

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
    public static void save(Player player, FileConfiguration fileConfiguration){
        String uuidString = player.getUniqueId().toString();
        save(fileConfiguration, "data/"+uuidString+".yml");
    }

    //存檔
    public static void save(FileConfiguration fileConfiguration, String path){
        File file = new File(RoleMaster.unrealCorePlugin.getResourceFolder(), path);
        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //從插件預設路徑獲取YML檔案
    public static List<FileConfiguration> getYmlFileList(String path){
        return YmlFileUtil.findFileConfiguration(RoleMaster.unrealCorePlugin.getResourceFolder()+path);

    }
    //從插件預設路徑獲取YML檔案
    public static FileConfiguration getYmlFile(String path){

        File file = new File(RoleMaster.unrealCorePlugin.getResourceFolder(), path);


        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return YamlConfiguration.loadConfiguration(file);
    }

    //建立設定檔
    public static void createConfig(){

        File file = new File(RoleMaster.unrealCorePlugin.getResourceFolder(), "data");
        if(!file.exists()){
            file.mkdir();
        }
        //Command
        PluginUtil.resourceCopy(RoleMaster.unrealCorePlugin.getJavaPlugin(), "common/attributes.yml", false);
        PluginUtil.resourceCopy(RoleMaster.unrealCorePlugin.getJavaPlugin(), "common/base.yml", false);
        PluginUtil.resourceCopy(RoleMaster.unrealCorePlugin.getJavaPlugin(), "common/event.yml", false);
        PluginUtil.resourceCopy(RoleMaster.unrealCorePlugin.getJavaPlugin(), "common/level.yml", false);
        //GUI
        PluginUtil.resourceCopy(RoleMaster.unrealCorePlugin.getJavaPlugin(), "gui/RoleAttributeGUI.yml", false);
        PluginUtil.resourceCopy(RoleMaster.unrealCorePlugin.getJavaPlugin(), "gui/RoleSkillGUI.yml", false);
        //HUD
        PluginUtil.resourceCopy(RoleMaster.unrealCorePlugin.getJavaPlugin(), "hud/skill.yml", false);
        PluginUtil.resourceCopy(RoleMaster.unrealCorePlugin.getJavaPlugin(), "hud/state.yml", false);
        //Role Archer
        PluginUtil.resourceCopy(RoleMaster.unrealCorePlugin.getJavaPlugin(), "role/Archer/skill/Anklesnare.yml", false);
        PluginUtil.resourceCopy(RoleMaster.unrealCorePlugin.getJavaPlugin(), "role/Archer/skill/ArrowShower.yml", false);
        PluginUtil.resourceCopy(RoleMaster.unrealCorePlugin.getJavaPlugin(), "role/Archer/skill/ChargeArrow.yml", false);
        PluginUtil.resourceCopy(RoleMaster.unrealCorePlugin.getJavaPlugin(), "role/Archer/skill/DoubleStrafing.yml", false);
        PluginUtil.resourceCopy(RoleMaster.unrealCorePlugin.getJavaPlugin(), "role/Archer/skill/ElementArrow.yml", false);
        PluginUtil.resourceCopy(RoleMaster.unrealCorePlugin.getJavaPlugin(), "role/Archer/skill/OwlsEye.yml", false);
        PluginUtil.resourceCopy(RoleMaster.unrealCorePlugin.getJavaPlugin(), "role/Archer/skill/VulturesEye.yml", false);
        PluginUtil.resourceCopy(RoleMaster.unrealCorePlugin.getJavaPlugin(), "role/Archer/Main.yml", false);
        //Role Default
        PluginUtil.resourceCopy(RoleMaster.unrealCorePlugin.getJavaPlugin(), "role/Default/skill/FirstAid.yml", false);
        PluginUtil.resourceCopy(RoleMaster.unrealCorePlugin.getJavaPlugin(), "role/Default/skill/Slam.yml", false);
        PluginUtil.resourceCopy(RoleMaster.unrealCorePlugin.getJavaPlugin(), "role/Default/Main.yml", false);
        //Role Magician
        PluginUtil.resourceCopy(RoleMaster.unrealCorePlugin.getJavaPlugin(), "role/Magician/skill/ColdBolt.yml", false);
        PluginUtil.resourceCopy(RoleMaster.unrealCorePlugin.getJavaPlugin(), "role/Magician/skill/FireBolt.yml", false);
        PluginUtil.resourceCopy(RoleMaster.unrealCorePlugin.getJavaPlugin(), "role/Magician/skill/FrostDiver.yml", false);
        PluginUtil.resourceCopy(RoleMaster.unrealCorePlugin.getJavaPlugin(), "role/Magician/skill/IncreaseSpiritualPower.yml", false);
        PluginUtil.resourceCopy(RoleMaster.unrealCorePlugin.getJavaPlugin(), "role/Magician/skill/LightningBolt.yml", false);
        PluginUtil.resourceCopy(RoleMaster.unrealCorePlugin.getJavaPlugin(), "role/Magician/skill/RingOfFlame.yml", false);
        PluginUtil.resourceCopy(RoleMaster.unrealCorePlugin.getJavaPlugin(), "role/Magician/skill/SoulStrike.yml", false);
        PluginUtil.resourceCopy(RoleMaster.unrealCorePlugin.getJavaPlugin(), "role/Magician/Main.yml", false);
        //Role SwordMan
        PluginUtil.resourceCopy(RoleMaster.unrealCorePlugin.getJavaPlugin(), "role/Swordman/skill/Bash.yml", false);
        PluginUtil.resourceCopy(RoleMaster.unrealCorePlugin.getJavaPlugin(), "role/Swordman/skill/Endure.yml", false);
        PluginUtil.resourceCopy(RoleMaster.unrealCorePlugin.getJavaPlugin(), "role/Swordman/skill/IncreaseRecuperativePower.yml", false);
        PluginUtil.resourceCopy(RoleMaster.unrealCorePlugin.getJavaPlugin(), "role/Swordman/skill/MagnumBreak.yml", false);
        PluginUtil.resourceCopy(RoleMaster.unrealCorePlugin.getJavaPlugin(), "role/Swordman/skill/Provoke.yml", false);
        PluginUtil.resourceCopy(RoleMaster.unrealCorePlugin.getJavaPlugin(), "role/Swordman/skill/SwordMastery.yml", false);
        PluginUtil.resourceCopy(RoleMaster.unrealCorePlugin.getJavaPlugin(), "role/Swordman/Main.yml", false);
    }

}
