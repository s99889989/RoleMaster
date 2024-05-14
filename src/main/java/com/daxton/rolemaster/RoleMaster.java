package com.daxton.rolemaster;

import com.daxton.rolemaster.api.RolePlaceholder;
import com.daxton.rolemaster.command.RoleMasterCommand;
import com.daxton.rolemaster.command.RoleMasterTab;
import com.daxton.rolemaster.controller.RoleMasterController;
import com.daxton.rolemaster.controller.RolePlayerController;
import com.daxton.rolemaster.listener.EquipmentItemListener;
import com.daxton.rolemaster.listener.ResourceListener;
import com.daxton.rolemaster.listener.RoleMasterListener;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class RoleMaster extends JavaPlugin {

    public static RoleMaster roleMaster;

    @Override
    public void onEnable() {
        roleMaster = this;

        RoleMasterController.load();

        Objects.requireNonNull(Bukkit.getPluginCommand("rolemaster")).setExecutor(new RoleMasterCommand());
        Objects.requireNonNull(Bukkit.getPluginCommand("rolemaster")).setTabCompleter(new RoleMasterTab());


        Bukkit.getPluginManager().registerEvents(new RoleMasterListener(), roleMaster);
        Bukkit.getPluginManager().registerEvents(new EquipmentItemListener(), roleMaster);
        if(Bukkit.getServer().getPluginManager().getPlugin("UnrealResource") != null){
            Bukkit.getPluginManager().registerEvents(new ResourceListener(), roleMaster);
        }
        if(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            new RolePlaceholder().register();
        }

        RolePlayerController.start();
    }

    @Override
    public void onDisable() {
        RolePlayerController.stop();
    }

    //發送後台系統訊息(系統)
    public static void sendSystemLogger(String message){
        roleMaster.getLogger().info("System: "+message);
    }
    
    //發送後臺訊息(錯誤)
    public static void sendErrorLogger(String message){
        roleMaster.getLogger().info("Error: "+message);
    }

    //發送後臺訊息(一般)
    public static void sendLogger(String s){
        roleMaster.getLogger().info(s);
    }

    //獲取資源路徑
    public static String getResourceFolder(){
        return roleMaster.getDataFolder()+"/";
    }

}
