package com.daxton.rolemaster.gui;

import com.daxton.rolemaster.RoleMaster;
import com.daxton.rolemaster.application.CustomValueConvert;
import com.daxton.rolemaster.been.RolePlayer;
import com.daxton.rolemaster.been.role.SkillBeen;
import com.daxton.rolemaster.controller.*;
import com.daxton.unrealcore.application.UnrealCoreAPI;
import com.daxton.unrealcore.application.method.SchedulerFunction;
import com.daxton.unrealcore.common.type.MouseActionType;
import com.daxton.unrealcore.common.type.MouseButtonType;
import com.daxton.unrealcore.display.content.gui.UnrealCoreGUI;
import com.daxton.unrealcore.display.content.module.control.ButtonModule;
import com.daxton.unrealcore.display.content.module.control.ContainerModule;
import com.daxton.unrealcore.display.content.module.display.ImageModule;
import com.daxton.unrealcore.display.content.module.display.TextModule;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoleSkillGUI extends UnrealCoreGUI {


    //佔位符列表
    public Map<String, String> customValue = new HashMap<>();
    //角色佔位符
    public Map<String, String> roleValue = new HashMap<>();
    //更新佔位符
    SchedulerFunction.RunTask runTask;
    //技能模塊
    private ContainerModule skillModule;
    //角色玩家
    private RolePlayer rolePlayer;

    public RoleSkillGUI(String guiName, FileConfiguration fileConfiguration, Player player) {
        super(guiName, fileConfiguration);
        rolePlayer = RolePlayerGetController.getRolePlayer(player);

        //到屬性選單
        ButtonModule buttonAttribute = (ButtonModule) getModule("ToMenuContainer", "ButtonAttribute");
        buttonAttribute.onButtonClick((buttonModule, mouseButtonType, mouseActionType) -> {
            if(mouseButtonType == MouseButtonType.Left && mouseActionType == MouseActionType.On){
                FileConfiguration attributeConfig = RoleMasterController.getYmlFile("gui/RoleAttributeGUI.yml");
                RoleAttributeGUI roleAttributeGUI = new RoleAttributeGUI("RoleAttributeGUI", attributeConfig, player);
                runTask.cancel();
                UnrealCoreAPI.openGUI(player, roleAttributeGUI);
            }
        });
        customValue.clear();
        roleValue.clear();
        //佔位符
        applyFunctionToFields(this::placeholder);
        //技能模塊
        this.skillModule = (ContainerModule) getModule("SkillList", "SkillContainer");
        removeModule("SkillList", "SkillContainer");
        //設置技能列表
        setSkillList(player);
        //設置綁定列表
        setBindList(player);

        RolePlayerController.setDefaultValue(player, roleValue);

    }

    //設置技能列表
    public void setSkillList(Player player){
        //技能列表
        ContainerModule skillListModule = (ContainerModule) getModule("SkillList");
        List<SkillBeen> skillBeenList = RolePlayerGetController.getSkillList(player);

        int height = 0;

        for(int i = 0 ;i < skillBeenList.size() ; i++){
            SkillBeen skillBeen = skillBeenList.get(i);

            ContainerModule skillModule = this.skillModule.copy();
            skillModule.setModuleID("Skill_"+i);
            int y = 5+i*(skillModule.getHeight()+5);
            height = y +skillModule.getHeight()+5;
            skillModule.setY(5+i*(skillModule.getHeight()+5));
            //圖標
            ImageModule iconImageModule = (ImageModule) skillModule.getModule("IconImage");
            List<String> imageList = new ArrayList<>();
            imageList.add(skillBeen.getIcon());
            iconImageModule.setImage(imageList);
            //技能名稱  等級
            TextModule textModule = (TextModule) skillModule.getModule("Text");
            textModule.setText(skillBeen.replaceSkill(textModule.getText(), player));

            //選擇
            ButtonModule selectModule = (ButtonModule) skillModule.getModule("SelectButton");
            selectModule.onButtonClick((buttonModule, mouseButtonType, mouseActionType) -> {
                if(mouseButtonType == MouseButtonType.Left && mouseActionType == MouseActionType.On){
                    rolePlayer.setSelectSkillBenn(skillBeen);
                    selectSkill(player, skillBeen);
                }
            });

            //升級技能
            if(!RolePlayerController.selectSkillCanUp(player, skillBeen)){
                skillModule.removeModule("LevelUpButton");
            }else {
                ButtonModule levelUpButton = (ButtonModule) skillModule.getModule("LevelUpButton");
                int finalI = i;
                levelUpButton.onButtonClick((buttonModule, mouseButtonType, mouseActionType) -> {
                    if(mouseButtonType == MouseButtonType.Left && mouseActionType == MouseActionType.On){
                        RolePlayerController.selectSkillUp(player, skillBeen);
                        RoleSkillController.useSkill(player, skillBeen);
                        if(!RolePlayerController.selectSkillCanUp(player, skillBeen)){
                            removeModule("SkillList", "Skill_"+ finalI, "LevelUpButton");
                            upDate();
                        }
//                        setSkillList(player);
//                        upDate();
                    }
                });
            }

            skillListModule.addModule(skillModule);
        }
        skillListModule.setActualHeight(height);
    }

    //設置綁定列表
    public void setBindList(Player player){
        ContainerModule bindContainer = (ContainerModule) getModule("BindContainer");
        for(int i = 1 ;i <= 6 ; i++){
            String bindSkillName = RolePlayerGetController.getBindSKill(player, i);
            if(!bindSkillName.isEmpty()){
                ImageModule bindImage = (ImageModule) bindContainer.getModule("Bind_"+i);
                SkillBeen skillBeen = RolePlayerGetController.getSkillBeen(player, bindSkillName);
                if(skillBeen != null){
                    List<String> stringList = new ArrayList<>();
                    stringList.add(skillBeen.getIcon());
                    bindImage.setImage(stringList);
                    bindImage.setTransparent(255);
                }

            }


            ButtonModule selectButton = (ButtonModule) bindContainer.getModule("Select_"+i);
            int finalI = i;
            selectButton.onButtonClick((buttonModule, mouseButtonType, mouseActionType) -> {
                if(mouseActionType == MouseActionType.On){
                    //設置綁定技能
                    if(mouseButtonType == MouseButtonType.Left){
                        SkillBeen selectSkillBenn = this.rolePlayer.getSelectSkillBenn();
                        if(selectSkillBenn != null){
                            int nowLevel = RolePlayerGetController.getSkillLevelNow(player, selectSkillBenn.getSkillName());
                            if(!selectSkillBenn.isPassive() && nowLevel > 0){
                                RolePlayerController.setBindSKill(player, finalI, selectSkillBenn.getSkillName());

                                RoleHUDController.setDefaultHUD(player);

                                ImageModule bindImage = (ImageModule) getModule("BindContainer", "Bind_"+finalI);

                                List<String> stringList = new ArrayList<>();
                                stringList.add(selectSkillBenn.getIcon());
                                bindImage.setImage(stringList);
                                bindImage.setTransparent(255);
                                upDate();
                            }
                        }
                    }
                    //取消綁定技能
                    if(mouseButtonType == MouseButtonType.Right){
                        RolePlayerController.setBindSKill(player, finalI, "");

                        RoleHUDController.setDefaultHUD(player);

                        ImageModule bindImage = (ImageModule) getModule("BindContainer", "Bind_"+finalI);
                        bindImage.setTransparent(0);
                        upDate();
                    }
                }

            });
//            String bindSkillName = RolePlayerController.getBindSKill(player, i);
//            RoleMaster.sendLogger(bindSkillName);
        }
    }

    //選擇技能
    public void selectSkill(Player player, SkillBeen skillBeen){
        TextModule textModule = (TextModule) getModule("SkillDirections", "Directions");
        textModule.setText(skillBeen.getDirections());
        RolePlayerController.setSelectValue(player, roleValue, skillBeen);
        upDate();

    }


    @Override
    public void opening() {
        placeholderChange();
    }

    @Override
    public void close() {
        runTask.cancel();
    }

    //更新佔位符
    public void placeholderChange(){

        runTask = SchedulerFunction.runTimer(RoleMaster.unrealCorePlugin.getJavaPlugin(),()->{
            Map<String, String> customValueMap = new HashMap<>();
            if(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
                customValue.forEach((content, contentChange) -> {
                    String value = PlaceholderAPI.setPlaceholders(getPlayer(), "%"+content+"%");
                    customValueMap.put(contentChange, value);
                });
            }
            RolePlayerController.setValue(getPlayer(), roleValue);
            customValueMap.putAll(roleValue);
            UnrealCoreAPI.setCustomValueMap(getPlayer(), customValueMap);
        }, 0, 20);

    }

    //把佔位符%%改成{}並存到Map來更新內容
    public String placeholder(String content){
        if(content.startsWith("{") && content.endsWith("}")){
            CustomValueConvert.valueNBT(content, customValue);
        }
        return CustomValueConvert.value(content, customValue);
    }

}
