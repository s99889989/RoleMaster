package com.daxton.rolemaster.been.role;


import com.daxton.rolemaster.RoleMaster;
import com.daxton.rolemaster.application.NumberUtil;
import com.daxton.rolemaster.controller.RoleMasterController;
import com.daxton.rolemaster.controller.RolePlayerController;
import com.daxton.unrealcore.application.base.YmlFileUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class SkillBeen {

    //技能文件名稱
    private String skillName = "Null";
    //最高等級
    private int levelMax = 10;
    //顯示名稱
    private String displayName = "";
    //圖標路徑
    private String icon = "";
    //說明
    private List<String> directions = new ArrayList<>();
    //需要目標
    private boolean needTarget = true;
    //技能發動距離
    private String targetDistance = "";
    //技能冷卻時間
    private String coolDown = "";
    //施法時間
    private String castTime = "";
    //消耗魔力
    private String mana = "";
    //值1
    private String value = "";
    //值2
    private String value2 = "";
    //值3
    private String value3 = "";
    //執行次數
    private String times = "1";
    //執行間隔
    private String interval = "1";
    //動作
    private List<String> actionList = new ArrayList<>();
    //被動技能
    private boolean passive = false;
    //需求武器
    private List<String> needWeapons = new ArrayList<>();

    public SkillBeen(String roleName, String skillName) {
        this.skillName = skillName;
        FileConfiguration skillConfig = RoleMasterController.getYmlFile("role/"+roleName+"/skill/"+skillName+".yml");
        this.levelMax = YmlFileUtil.getInt(skillConfig, "MaxLevel", 10);
        this.displayName = YmlFileUtil.getString(skillConfig, "DisplayName", "無");
        this.icon = YmlFileUtil.getString(skillConfig, "Icon", "");
        this.directions = YmlFileUtil.getStringList(skillConfig, "Directions", "無");
        this.needTarget = YmlFileUtil.getBoolean(skillConfig, "NeedTarget", true);
        this.targetDistance = YmlFileUtil.getString(skillConfig, "TargetDistance", "10");
        this.coolDown = YmlFileUtil.getString(skillConfig, "CoolDown", "0");
        this.castTime = YmlFileUtil.getString(skillConfig, "CastTime", "0");
        this.mana = YmlFileUtil.getString(skillConfig, "Mana", "0");
        this.value = YmlFileUtil.getString(skillConfig, "Value", "0");
        this.value2 = YmlFileUtil.getString(skillConfig, "Value2", "0");
        this.value3 = YmlFileUtil.getString(skillConfig, "Value3", "0");
        this.times = YmlFileUtil.getString(skillConfig, "Times", "1");
        this.interval = YmlFileUtil.getString(skillConfig, "Interval", "1");
        this.actionList = YmlFileUtil.getStringList(skillConfig, "Action", "");
        this.passive = YmlFileUtil.getBoolean(skillConfig, "Passive", false);
        this.needWeapons = YmlFileUtil.getStringList(skillConfig, "NeedWeapons", "");

    }

    public List<String> replaceSkill(List<String> inputList, Player player){
        List<String> textList = new ArrayList<>();
        inputList.forEach(s -> {
            String replaceString = s.replace("{custom_skill_name}", "{custom_skill_"+this.skillName+"_name}");
            replaceString = replaceString.replace("{custom_skill_level_now}", "{custom_skill_"+this.skillName+"_level_now}");
            replaceString = replaceString.replace("{custom_skill_level_max}", "{custom_skill_"+this.skillName+"_level_max}");
            textList.add(replaceString);
        });

        return textList;
    }

    //獲取使用魔力
    public int getCostMana(Player player) {
        String value = RolePlayerController.replace(player, this.mana);
        if(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            value = PlaceholderAPI.setPlaceholders(player, value);
        }
        return NumberUtil.countInteger(value);
    }

    //獲取CD
    public int getCD(Player player) {
        String value = RolePlayerController.replace(player, this.coolDown);
        if(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            value = PlaceholderAPI.setPlaceholders(player, value);

        }

        return NumberUtil.countInteger(value);
    }

    //獲取詠唱時間
    public int getCastTime(Player player) {
        String value = RolePlayerController.replace(player, this.castTime);
        if(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            value = PlaceholderAPI.setPlaceholders(player, value);
        }
        return NumberUtil.countInteger(value);
    }

    //獲取執行次數
    public int getTimes(Player player){
        String value = RolePlayerController.replace(player, this.times);
        if(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            value = PlaceholderAPI.setPlaceholders(player, value);
        }
        int times = NumberUtil.countInteger(value);
        if(times < 1){
            times = 1;
        }
        return times;
    }

    //獲取執行間隔
    public int getInterval(Player player){
        String value = RolePlayerController.replace(player, this.interval);
        if(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            value = PlaceholderAPI.setPlaceholders(player, value);
        }
        int times = NumberUtil.countInteger(value);
        if(times < 1){
            times = 1;
        }
        return times;
    }

    //獲取值
    public int getValue(Player player){
        String value = RolePlayerController.replace(player, this.value);
        if(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            value = PlaceholderAPI.setPlaceholders(player, value);
        }
        return NumberUtil.countInteger(value);
    }
    //獲取值
    public int getValue2(Player player){
        String value = RolePlayerController.replace(player, this.value2);
        if(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            value = PlaceholderAPI.setPlaceholders(player, value);
        }
        return NumberUtil.countInteger(value);
    }
    //獲取值
    public int getValue3(Player player){
        String value = RolePlayerController.replace(player, this.value3);
        if(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            value = PlaceholderAPI.setPlaceholders(player, value);
        }
        return NumberUtil.countInteger(value);
    }
    //獲取距離
    public int getTargetDistance(Player player){
        String value = RolePlayerController.replace(player, this.targetDistance);
        if(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            value = PlaceholderAPI.setPlaceholders(player, value);
        }
        return NumberUtil.countInteger(value);
    }

    //獲取CD
    public String getDirectionsString(Player player) {
        StringBuilder string = new StringBuilder();
        for(String s : getDirections(player)){
            string.append(s);
        }
        return string.toString();
    }


    //獲取CD
    public List<String> getDirections(Player player) {
        List<String> stringList = new ArrayList<>();
        this.directions.forEach(s -> {
            String replaceString = RolePlayerController.replace(player, s);
            if(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
                replaceString = PlaceholderAPI.setPlaceholders(player, replaceString);
            }
            stringList.add(replaceString);
        });

        return stringList;
    }

}
