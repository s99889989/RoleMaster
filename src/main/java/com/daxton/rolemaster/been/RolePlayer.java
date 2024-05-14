package com.daxton.rolemaster.been;

import com.daxton.rolemaster.RoleMaster;
import com.daxton.rolemaster.application.ItemFunction;
import com.daxton.rolemaster.application.NumberUtil;
import com.daxton.rolemaster.been.item.attribute.AttributeData;
import com.daxton.rolemaster.been.item.attribute.ItemAttribute;
import com.daxton.rolemaster.been.item.damage.ItemDamage;
import com.daxton.rolemaster.been.role.RoleBeen;
import com.daxton.rolemaster.been.role.SkillBeen;
import com.daxton.rolemaster.controller.RoleHUDController;
import com.daxton.rolemaster.controller.RoleMasterController;
import com.daxton.rolemaster.controller.RolePlayerGetController;
import com.daxton.rolemaster.type.DamageType;
import com.daxton.unrealcore.application.UnrealCoreAPI;
import com.daxton.unrealcore.application.base.StringConversionBasics;

import com.daxton.unrealcore.application.base.YmlFileUtil;
import com.daxton.unrealcore.application.method.SchedulerFunction;

import com.daxton.unrealcore.display.been.module.display.ImageModuleData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


@Getter
@Setter
@ToString
@NoArgsConstructor
public class RolePlayer {

    //玩家
    private Player player;
    //目前魔力
    private int manaNow;
    //最高魔力
    private int manaMax;
    //魔力回復
    private int manaRegeneration;
    //技能CD
    private Map<String, SchedulerFunction.RunTask> skillCoolDown = new HashMap<>();
    //物品屬性
    private Map<String, ItemAttribute> itemAttributeHashMap = new HashMap<>();
    //物品傷害
    private Map<String, ItemDamage> itemDamageMap = new HashMap<>();
    //舊的物品
    private String oldItem = "";
    //箭矢增加向量
    private double arrowVelocity = 0;
    //發射的向量值
    private int arrowVelocityLsat = 0;
    //GUI選擇的技能
    private SkillBeen selectSkillBenn;

    //判斷閃避是否成功
    public boolean getDodgeSuccessful(){
        FileConfiguration attributeConfig = RoleMasterController.getYmlFile("common/attributes.yml");
        String numberString = YmlFileUtil.getString(attributeConfig, "State.Dodge_Chance", "0");
        int probability = getValueInt(numberString);
        Random random = new Random();
        int randomNumber = random.nextInt(101);
        if (randomNumber < probability) {
            return true;
        }
        return false;
    }

    //暴擊倍率
    public double getCriticalPoser(){
        FileConfiguration attributeConfig = RoleMasterController.getYmlFile("common/attributes.yml");
        String criticalChanceString = YmlFileUtil.getString(attributeConfig, "State.Critical_Chance", "0");

        int criticalChance = getValueInt(criticalChanceString);
        Random random = new Random();
        int randomNumber = random.nextInt(101);
        if (randomNumber < criticalChance) {
            String criticalPowerString = YmlFileUtil.getString(attributeConfig, "State.Critical_Power", "0");
            double criticalPower = getValueInt(criticalPowerString);
            return criticalPower / 100;
        }
        return 1;
    }

    //獲取傷害值
    public double getAttack(DamageType damageType, double damage, double damageFinal, double damageItem){

        if(damageType == DamageType.MELEE){
            return damageItem;
        }
        if(damageType == DamageType.MELEEADD){
            return damageItem + getAttackMelee();
        }
        if(damageType == DamageType.MELEEMULTIPLY){
            double baseAttack = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue();
            return (getDamage(baseAttack) + getAttackMelee()) * damage/100;
        }

        if(damageType == DamageType.RANGE){
            return damageItem + getAttackRange();
        }
        if(damageType == DamageType.RANGEADD){
            return damageItem + getAttackRange();
        }
        if(damageType == DamageType.RANGEMULTIPLY){
            return getDamage(getAttackRange()+9) * damage/100;
        }

        if(damageType == DamageType.MAGIC){
            return damageItem + getAttackMagic();
        }
        if(damageType == DamageType.MAGICADD){
            return damageItem + getAttackMagic();
        }
        if(damageType == DamageType.MAGICMULTIPLY){
            return getDamage(getAttackMagic()) * damage/100;
        }

        return damageItem;
    }

    //獲取近戰傷害
    public double getAttackMelee(){
        FileConfiguration attributeConfig = RoleMasterController.getYmlFile("common/attributes.yml");
        String numberString = YmlFileUtil.getString(attributeConfig, "State.Attack_Melee", "0");
        return getValueDouble(numberString);
    }

    //獲取遠距離傷害
    public double getAttackRange(){
        FileConfiguration attributeConfig = RoleMasterController.getYmlFile("common/attributes.yml");
        String numberString = YmlFileUtil.getString(attributeConfig, "State.Attack_Range", "0");
        return getValueDouble(numberString);
    }

    //獲取魔法傷害
    public double getAttackMagic(){
        FileConfiguration attributeConfig = RoleMasterController.getYmlFile("common/attributes.yml");
        String numberString = YmlFileUtil.getString(attributeConfig, "State.Attack_Magic", "0");
        return getValueDouble(numberString);
    }

    public RolePlayer(Player player) {
        this.player = player;
    }

    public void initialization(){
        RoleBeen roleBeen = RolePlayerGetController.getRoleBeen(player);
        if(roleBeen.getSkillBeenMap().isEmpty()){
            selectSkillBenn = new SkillBeen();
        }else {
            Map.Entry<String, SkillBeen> firstEntry = roleBeen.getSkillBeenMap().entrySet().iterator().next();
            selectSkillBenn = firstEntry.getValue();
        }

    }

    //設置物品屬性
    public void setItemAttribute(String item, Attribute attribute, String mark, int amount){
        item = item.toUpperCase();
        if(mark.isEmpty()){
            if(itemAttributeHashMap.containsKey(item)){
                ItemAttribute itemAttribute = itemAttributeHashMap.get(item);
                itemAttribute.removeAttribute(mark);
            }
        }else {

            ItemAttribute itemAttribute = new ItemAttribute();
            if(itemAttributeHashMap.containsKey(item)){
                itemAttribute = itemAttributeHashMap.get(item);
            }else {
                itemAttributeHashMap.put(item, itemAttribute);
            }
            AttributeData attributeData = new AttributeData(attribute, mark, amount);
            itemAttribute.setAttribute(mark, attributeData);

        }
    }

    //移除物品屬性
    public void removeItemAttribute(String item, String mark){
        if(itemAttributeHashMap.containsKey(item)){
            ItemAttribute itemAttribute = itemAttributeHashMap.get(item);
            itemAttribute.removeAttribute(mark);
        }
    }

    //改變主手
    public void changeMainItem(ItemStack itemStack){
        String item = ItemFunction.getItemID(itemStack);
        if(itemAttributeHashMap.containsKey(oldItem)){
            ItemAttribute itemAttribute = itemAttributeHashMap.get(oldItem);
            itemAttribute.removePlayerAttribute(player);
        }
        if(itemAttributeHashMap.containsKey(item)){
            ItemAttribute itemAttribute = itemAttributeHashMap.get(item);
            itemAttribute.setPlayerAttribute(player);
        }
        oldItem = item;
    }

    public double getDamage(double damage){
        String item = ItemFunction.getItemID(player.getInventory().getItemInMainHand());
        if(itemDamageMap.containsKey(item)){
            ItemDamage itemDamage = itemDamageMap.get(item);
            return itemDamage.getDamage(damage);
        }
        return damage;
    }

    //改變主手
    public void removeMainAttribute(){
        if(itemAttributeHashMap.containsKey(oldItem)){
            ItemAttribute itemAttribute = itemAttributeHashMap.get(oldItem);
            itemAttribute.removePlayerAttribute(player);
        }
    }


    //判斷技能是否在CD
    public boolean isCost(String skillName){
        if(skillCoolDown.containsKey(skillName)){
            return true;
        }
        return false;
    }
    //發動技能
    public boolean costSkill(String skillName, int cd, int idx){
        if(!skillCoolDown.containsKey(skillName)){
            ImageModuleData moduleData = (ImageModuleData) RoleHUDController.getModuleData("CD_"+idx);
            int height =  StringConversionBasics.getInt(moduleData.getHeight(), 13);
            moduleData.setTransparent(200+"");
            UnrealCoreAPI.setHUD(player, moduleData);
            AtomicInteger i = new AtomicInteger();
            SchedulerFunction.RunTask runTask =  SchedulerFunction.runTimer(RoleMaster.roleMaster, ()->{
                i.getAndIncrement();

                double setHeight = (double) (cd*10-i.get()) / (cd*10)  * height;
                if(i.get() >= cd*10){
                    skillCoolDown.get(skillName).cancel();
                    moduleData.setTransparent(0+"");
                    moduleData.setHeight(height+"");
                    UnrealCoreAPI.setHUD(player, moduleData);
                    skillCoolDown.remove(skillName);
                }else {
                    moduleData.setHeight(setHeight+"");
                    UnrealCoreAPI.setHUD(player, moduleData);
                }

            }, 2,  2);

            skillCoolDown.put(skillName, runTask);
            return true;
        }else {
            return false;
        }


    }

    //重新設定值
    public void reset(){
        RoleBeen roleBeen = RolePlayerGetController.getRoleBeen(player);
        this.manaMax = roleBeen.getMana(player);
        String uuidString = player.getUniqueId().toString();
        FileConfiguration dataConfig = RoleMasterController.getYmlFile("data/"+uuidString+".yml");
        this.manaNow = YmlFileUtil.getInt(dataConfig, "Mana", 0);
        if(manaNow > manaMax){
            manaNow = manaMax;
        }
        this.manaRegeneration = roleBeen.getManaRegeneration(player);
    }

    //回復
    public void regeneration(){
        boolean b = manaNow < manaMax;
        if(b){
            manaNow += manaRegeneration;
            if(manaNow > manaMax){
                manaNow = manaMax;
            }
            Map<String, String> roleValue = new HashMap<>();
            roleValue.put("role_mana_now", RolePlayerGetController.getManaNow(player)+"");
            roleValue.put("role_mana_max", RolePlayerGetController.getManaMax(player)+"");
            UnrealCoreAPI.setCustomValueMap(player, roleValue);
        }

    }

    //花費魔力
    public boolean costMana(int cost){
        if(manaNow >= cost){
            manaNow -= cost;
            return true;
        }
        return false;
    }
    //回復魔力
    public void regenerationMana(int mana){
        boolean b = manaNow < manaMax;
        if(b){
            manaNow += mana;
            if(manaNow > manaMax){
                manaNow = manaMax;
            }
        }
    }

    public void save(){
        String uuidString = player.getUniqueId().toString();
        FileConfiguration dataConfig = RoleMasterController.getYmlFile("data/"+uuidString+".yml");
        dataConfig.set("Mana", manaNow);
        RoleMasterController.save(dataConfig, "data/"+uuidString+".yml");
    }

    //佔位符轉換
    public int getValueInt(String numberString){
        return (int) getValueDouble(numberString);
    }

    //佔位符轉換
    public double getValueDouble(String numberString){
        RoleBeen roleBeen = RolePlayerGetController.getRoleBeen(player);

        for(SkillBeen skillBeen : roleBeen.getSkillBeenMap().values()){
            String skillName = skillBeen.getSkillName();
            numberString = numberString.replace("{custom_skill_"+skillName+"_name}", skillBeen.getDisplayName());
            numberString = numberString.replace("{custom_skill_"+skillName+"_level_now}", RolePlayerGetController.getSkillLevelNow(player, skillName)+"");
            numberString = numberString.replace("{custom_skill_"+skillName+"_level_max}", skillBeen.getLevelMax()+"");
        }

        numberString = numberString.replace("{custom_role_name}", roleBeen.getDisplayName());
        numberString = numberString.replace("{custom_role_level_now}", RolePlayerGetController.getLevelSkillNow(player)+"");
        numberString = numberString.replace("{custom_role_level_max}", roleBeen.getLevelSkillMax()+"");
        numberString = numberString.replace("{custom_role_exp_now}", RolePlayerGetController.getExpSkillNow(player)+"");
        numberString = numberString.replace("{custom_role_exp_max}", RolePlayerGetController.getExpSkillMax(player)+"");
        numberString = numberString.replace("{custom_role_point}", RolePlayerGetController.getPointSkillNow(player)+"");

        if(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            numberString = PlaceholderAPI.setPlaceholders(player, numberString);
        }

        return NumberUtil.countDouble(numberString);
    }

    //佔位符轉換
    public String getValueString(String numberString){
        RoleBeen roleBeen = RolePlayerGetController.getRoleBeen(player);

        for(SkillBeen skillBeen : roleBeen.getSkillBeenMap().values()){
            String skillName = skillBeen.getSkillName();
            numberString = numberString.replace("{custom_skill_"+skillName+"_name}", skillBeen.getDisplayName());
            numberString = numberString.replace("{custom_skill_"+skillName+"_level_now}", RolePlayerGetController.getSkillLevelNow(player, skillName)+"");
            numberString = numberString.replace("{custom_skill_"+skillName+"_level_max}", skillBeen.getLevelMax()+"");
        }

        numberString = numberString.replace("{custom_role_name}", roleBeen.getDisplayName());
        numberString = numberString.replace("{custom_role_level_now}", RolePlayerGetController.getLevelSkillNow(player)+"");
        numberString = numberString.replace("{custom_role_level_max}", roleBeen.getLevelSkillMax()+"");
        numberString = numberString.replace("{custom_role_exp_now}", RolePlayerGetController.getExpSkillNow(player)+"");
        numberString = numberString.replace("{custom_role_exp_max}", RolePlayerGetController.getExpSkillMax(player)+"");
        numberString = numberString.replace("{custom_role_point}", RolePlayerGetController.getPointSkillNow(player)+"");

        if(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            numberString = PlaceholderAPI.setPlaceholders(player, numberString);
        }
        return numberString;
    }



}
