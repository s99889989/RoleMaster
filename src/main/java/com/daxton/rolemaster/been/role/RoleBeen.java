package com.daxton.rolemaster.been.role;


import com.daxton.rolemaster.application.NumberUtil;
import com.daxton.rolemaster.controller.RoleMasterController;
import com.daxton.rolemaster.controller.RolePlayerController;
import com.daxton.rolemaster.controller.RolePlayerGetController;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class RoleBeen {

    //角色文件名稱
    private String roleName;
    //顯示名稱
    private String displayName = "";
    //圖標路徑
    private String icon = "";
    //最高基礎等級
    private int levelBaseMax = 99;
    //最高技能等級
    private int levelSkillMax = 10;
    //魔力
    private String mana = "10";
    //魔力回復
    private String manaRegeneration = "1";
    //技能列表
    private Map<String, SkillBeen> skillBeenMap = new HashMap<>();


    public RoleBeen(String roleName) {
        this.roleName = roleName;
        FileConfiguration roleConfig = RoleMasterController.getYmlFile("role/"+roleName+"/Main.yml");
        this.displayName = YmlFileUtil.getString(roleConfig, "DisplayName", "");
        this.icon = YmlFileUtil.getString(roleConfig, "Icon", "");
        this.levelBaseMax = YmlFileUtil.getInt(roleConfig, "LevelMax.Base", 99);
        this.levelSkillMax = YmlFileUtil.getInt(roleConfig, "LevelMax.Skill", 10);
        this.mana = YmlFileUtil.getString(roleConfig, "State.Mana", "50");
        this.manaRegeneration = YmlFileUtil.getString(roleConfig, "State.ManaRegeneration", "5");

        RoleMasterController.getSkillList(roleName).forEach(skillName->{
            SkillBeen skillBeen = new SkillBeen(roleName, skillName);
            skillBeenMap.put(skillName, skillBeen);
        });
    }

    public List<String> replace(List<String> inputList, Player player){
        List<String> textList = new ArrayList<>();

        inputList.forEach(s -> {
            String replaceString = s.replace("{role_name}", ""+this.displayName);
            replaceString = replaceString.replace("{role_level_now}", ""+RolePlayerGetController.getLevelSkillNow(player));
            replaceString = replaceString.replace("{role_level_max}", this.levelSkillMax +"");
            replaceString = replaceString.replace("{role_exp_now}", ""+RolePlayerGetController.getExpSkillNow(player));
            replaceString = replaceString.replace("{role_exp_max}", ""+ RolePlayerGetController.getExpSkillMax(player));
            replaceString = replaceString.replace("{role_point}", ""+RolePlayerGetController.getPointSkillNow(player));
            textList.add(replaceString);
        });

        return textList;
    }
    //獲取職業SP
    public int getMana(Player player){

        String value = RolePlayerController.replace(player, this.mana);
        if(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            value = PlaceholderAPI.setPlaceholders(player, value);
        }
        return NumberUtil.countInteger(value);
    }

    //獲取職業SP
    public int getManaRegeneration(Player player){

        String value = RolePlayerController.replace(player, this.manaRegeneration);
        if(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            value = PlaceholderAPI.setPlaceholders(player, value);
        }
        return NumberUtil.countInteger(value);
    }

}
