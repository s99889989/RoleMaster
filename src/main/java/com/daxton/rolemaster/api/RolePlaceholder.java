package com.daxton.rolemaster.api;

import com.daxton.rolemaster.been.RolePlayer;
import com.daxton.rolemaster.been.role.RoleBeen;
import com.daxton.rolemaster.been.role.SkillBeen;
import com.daxton.rolemaster.controller.RoleMasterController;
import com.daxton.rolemaster.controller.RolePlayerGetController;
import com.daxton.unrealcore.application.base.YmlFileUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RolePlaceholder extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "role";
    }

    @Override
    public @NotNull String getAuthor() {
        return "s99889989";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }
    // 添加你的自定义占位符逻辑
    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        RoleBeen roleBeen = RolePlayerGetController.getRoleBeen(player);
        //角色名稱
        if (identifier.equalsIgnoreCase("displayname")) {
            return roleBeen.getDisplayName();
        }
        //角色圖標路徑
        if (identifier.equalsIgnoreCase("icon")) {
            return roleBeen.getIcon();
        }
        //角色基礎等級(目前)
        if (identifier.equalsIgnoreCase("base_level_now")) {
            return RolePlayerGetController.getLevelBaseNow(player)+"";
        }
        //角色基礎等級(上限)
        if (identifier.equalsIgnoreCase("base_level_max")) {
            return RolePlayerGetController.getLevelBaseMax(player)+"";
        }
        //角色基礎經驗(目前)
        if (identifier.equalsIgnoreCase("base_exp_now")) {
            return RolePlayerGetController.getExpBaseNow(player)+"";
        }
        //角色基礎經驗(上限)
        if (identifier.equalsIgnoreCase("base_exp_max")) {
            return RolePlayerGetController.getExpBaseMax(player)+"";
        }
        //角色基礎點數
        if (identifier.equalsIgnoreCase("base_point")) {
            return RolePlayerGetController.getPointBaseNow(player)+"";
        }
        //角色職業等級(目前)
        if (identifier.equalsIgnoreCase("skill_level_now")) {
            return RolePlayerGetController.getLevelSkillNow(player)+"";
        }
        //角色職業等級(上限)
        if (identifier.equalsIgnoreCase("skill_level_max")) {
            return roleBeen.getLevelSkillMax()+"";
        }
        //角色技能經驗(目前)
        if (identifier.equalsIgnoreCase("skill_exp_now")) {
            return RolePlayerGetController.getExpSkillNow(player)+"";
        }
        //角色技能經驗(上限)
        if (identifier.equalsIgnoreCase("skill_exp_max")) {
            return RolePlayerGetController.getExpSkillMax(player)+"";
        }
        //角色技能點數
        if (identifier.equalsIgnoreCase("skill_point")) {
            return RolePlayerGetController.getPointSkillNow(player)+"";
        }
        //魔量(目前)
        if (identifier.equalsIgnoreCase("mana_now")) {
            return RolePlayerGetController.getManaNow(player)+"";
        }
        //魔量(上限)
        if (identifier.equalsIgnoreCase("mana_max")) {
            return RolePlayerGetController.getManaMax(player)+"";
        }
        //魔量(回復)
        if (identifier.equalsIgnoreCase("mana_reg")) {
            return RolePlayerGetController.getManaRegeneration(player)+"";
        }

        //屬性
        FileConfiguration attrConfig = RoleMasterController.getYmlFile("common/attributes.yml");
        for(String name : YmlFileUtil.sectionList(attrConfig, "Attributes")){
            //角色基礎屬性(目前)
            if (identifier.equalsIgnoreCase("base_attr_"+name+"_now")) {
                return RolePlayerGetController.getAttributeBaseNow(player, name)+"";
            }
            //角色基礎屬性(上限)
            if (identifier.equalsIgnoreCase("base_attr_"+name+"_max")) {
                return RolePlayerGetController.getAttributeBaseMax(name)+"";
            }
            //角色基礎屬性(升級花費)
            if (identifier.equalsIgnoreCase("base_attr_"+name+"_cost")) {
                return RolePlayerGetController.getAttributeCost(player, name)+"";
            }
        }


        RolePlayer rolePlayer = RolePlayerGetController.getRolePlayer(player);
        SkillBeen selectSkillBeen = rolePlayer.getSelectSkillBenn();
        //選擇的技能訊息
        if (identifier.equalsIgnoreCase("select_skill_name")) {
            return selectSkillBeen.getDisplayName();
        }
        if (identifier.equalsIgnoreCase("select_skill_level_now")) {
            return RolePlayerGetController.getSkillLevelNow(player, selectSkillBeen.getSkillName())+"";
        }
        if (identifier.equalsIgnoreCase("select_skill_level_max")) {
            return selectSkillBeen.getLevelMax()+"";
        }
        if (identifier.equalsIgnoreCase("select_skill_mana")) {
            return selectSkillBeen.getCostMana(player)+"";
        }
        if (identifier.equalsIgnoreCase("select_skill_cd")) {
            return selectSkillBeen.getCD(player)+"";
        }
        if (identifier.equalsIgnoreCase("select_skill_ct")) {
            return selectSkillBeen.getCastTime(player)+"";
        }
        if (identifier.equalsIgnoreCase("select_skill_nt")) {
            return selectSkillBeen.isNeedTarget()+"";
        }
        if (identifier.equalsIgnoreCase("select_skill_distance")) {
            return selectSkillBeen.getTargetDistance(player)+"";
        }
        if (identifier.equalsIgnoreCase("select_skill_passive")) {
            return selectSkillBeen.isPassive()+"";
        }
        if (identifier.equalsIgnoreCase("select_skill_need_weapons")) {
            return selectSkillBeen.getNeedWeapons()+"";
        }

        for(SkillBeen skillBeen : roleBeen.getSkillBeenMap().values()){
            String skillName = skillBeen.getSkillName();
            //技能顯示名稱
            if (identifier.equalsIgnoreCase("skill_"+skillName+"_name")) {
                return skillBeen.getDisplayName();
            }
            //技能等級(目前)
            if (identifier.equalsIgnoreCase("skill_"+skillName+"_level_now")) {
                return RolePlayerGetController.getSkillLevelNow(player, skillName)+"";
            }
            //技能等級(上限)
            if (identifier.equalsIgnoreCase("skill_"+skillName+"_level_max")) {
                return skillBeen.getLevelMax()+"";
            }
            //技能SP消耗
            if (identifier.equalsIgnoreCase("skill_"+skillName+"_mana")) {
                return skillBeen.getCostMana(player)+"";
            }
            //技能CD時間
            if (identifier.equalsIgnoreCase("skill_"+skillName+"_cd")) {
                return skillBeen.getCD(player)+"";
            }
            //技能詠唱時間
            if (identifier.equalsIgnoreCase("skill_"+skillName+"_ct")) {
                return skillBeen.getCastTime(player)+"";
            }
            //技能是否需要目標
            if (identifier.equalsIgnoreCase("skill_"+skillName+"_nt")) {
                return skillBeen.isNeedTarget()+"";
            }
            //技能距離
            if (identifier.equalsIgnoreCase("skill_"+skillName+"_distance")) {
                return skillBeen.getTargetDistance(player)+"";
            }
            //技能是否被動
            if (identifier.equalsIgnoreCase("skill_"+skillName+"_passive")) {
                return skillBeen.isPassive()+"";
            }
            //技能主手需求
            if (identifier.equalsIgnoreCase("skill_"+skillName+"_need_weapons")) {
                return skillBeen.getNeedWeapons()+"";
            }
        }

        return "";
    }
}
