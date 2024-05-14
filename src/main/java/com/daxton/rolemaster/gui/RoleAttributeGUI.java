package com.daxton.rolemaster.gui;

import com.daxton.rolemaster.RoleMaster;
import com.daxton.rolemaster.application.CustomValueConvert;
import com.daxton.rolemaster.been.RolePlayer;
import com.daxton.rolemaster.controller.RoleMasterController;
import com.daxton.rolemaster.controller.RolePlayerController;
import com.daxton.rolemaster.controller.RolePlayerGetController;
import com.daxton.unrealcore.application.UnrealCoreAPI;
import com.daxton.unrealcore.application.base.YmlFileUtil;
import com.daxton.unrealcore.application.method.SchedulerFunction;
import com.daxton.unrealcore.common.type.MouseActionType;
import com.daxton.unrealcore.common.type.MouseButtonType;
import com.daxton.unrealcore.display.content.gui.UnrealCoreGUI;
import com.daxton.unrealcore.display.content.module.control.ButtonModule;
import com.daxton.unrealcore.display.content.module.control.ContainerModule;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class RoleAttributeGUI extends UnrealCoreGUI {

    //角色玩家
    private RolePlayer rolePlayer;
    //佔位符列表
    public Map<String, String> customValue = new HashMap<>();
    //更新佔位符
    SchedulerFunction.RunTask runTask;

    public RoleAttributeGUI(String guiName, FileConfiguration fileConfiguration, Player player) {
        super(guiName, fileConfiguration);
        rolePlayer = RolePlayerGetController.getRolePlayer(player);

        //到技能選單
        ButtonModule buttonSkill = (ButtonModule) getModule("ToMenuContainer", "ButtonSkill");
        buttonSkill.onButtonClick((buttonModule, mouseButtonType, mouseActionType) -> {
            if(mouseButtonType == MouseButtonType.Left && mouseActionType == MouseActionType.On){
                FileConfiguration skillConfig = RoleMasterController.getYmlFile("gui/RoleSkillGUI.yml");
                RoleSkillGUI roleSkillGUI = new RoleSkillGUI("RoleSkillGUI", skillConfig, player);
                runTask.cancel();
                UnrealCoreAPI.openGUI(player, roleSkillGUI);
            }
        });

        //佔位符
        applyFunctionToFields(this::placeholder);

        ContainerModule attributeList = (ContainerModule) getModule("AttributeList");
        attributeList.getModuleComponentsMap().forEach((attrModuleName, moduleComponents) -> {

            ButtonModule attributeUpButton = (ButtonModule) attributeList.getModule(attrModuleName, "AttributeUpButton");
            String attrName = YmlFileUtil.getString(fileConfiguration, attributeUpButton.getFilePath()+".Attribute", "Null");

            //升級屬性花費點數
            int costPoint = RolePlayerGetController.getAttributeCost(player, attrName);
            //目前點數
            int nowPoint = RolePlayerGetController.getPointBaseNow(player);
            if(nowPoint < costPoint){
                attributeUpButton.setTransparent(0);
                attributeUpButton.setClickTransparent(0);
                attributeUpButton.setHoverTransparent(0);
            }

            attributeUpButton.onButtonClick((buttonModule, mouseButtonType, mouseActionType) -> {
                if(mouseButtonType == MouseButtonType.Left && mouseActionType == MouseActionType.On){

                    RolePlayerController.basePointAdd(player, attrName);
                    attributeList.getModuleComponentsMap().forEach((attrModuleName1, moduleComponents1) -> {
                        String attrName1 = YmlFileUtil.getString(fileConfiguration, attributeUpButton.getFilePath()+".Attribute", "Null");

                        upAttrPoint(player, attrName1, attrModuleName1);
                    });
                    upDate();

                }
            });
        });
    }

    public void upAttrPoint(Player player, String attrName, String attrModuleName){

        ButtonModule attributeUpButton = (ButtonModule) getModule("AttributeList", attrModuleName, "AttributeUpButton");

        //升級屬性花費點數
        int costPoint = RolePlayerGetController.getAttributeCost(player, attrName);
        //目前點數
        int nowPoint = RolePlayerGetController.getPointBaseNow(player);
        if(nowPoint < costPoint){
            attributeUpButton.setTransparent(0);
            attributeUpButton.setClickTransparent(0);
            attributeUpButton.setHoverTransparent(0);
        }else {
            attributeUpButton.setTransparent(255);
            attributeUpButton.setClickTransparent(255);
            attributeUpButton.setHoverTransparent(255);
        }

    }


    @Override
    public void opening() {
        placeholderChange();
    }

    @Override
    public void close() {
        runTask.cancel();
    }

    public void placeholderChange(){

        runTask = SchedulerFunction.runTimer(RoleMaster.roleMaster,()->{
            Map<String, String> customValueMap = new HashMap<>();
            if(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
                customValue.forEach((content, contentChange) -> {
                    String value = PlaceholderAPI.setPlaceholders(getPlayer(), "%"+content+"%");
                    customValueMap.put(contentChange, value);
                });
            }
//            RolePlayerController.setValue(getPlayer(), roleValue);
//            customValueMap.putAll(roleValue);
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
