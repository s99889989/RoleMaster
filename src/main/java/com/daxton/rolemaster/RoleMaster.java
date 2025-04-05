package com.daxton.rolemaster;

import com.daxton.rolemaster.api.RolePlaceholder;
import com.daxton.rolemaster.command.RoleMasterCommand;
import com.daxton.rolemaster.command.RoleMasterTab;
import com.daxton.rolemaster.controller.RoleMasterController;
import com.daxton.rolemaster.controller.RolePlayerController;
import com.daxton.rolemaster.listener.EquipmentItemListener;
import com.daxton.rolemaster.listener.ResourceListener;
import com.daxton.rolemaster.listener.RoleMasterListener;

import com.daxton.unrealcore.UnrealCorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class RoleMaster extends JavaPlugin {

    public static UnrealCorePlugin unrealCorePlugin;

    @Override
    public void onEnable() {
        unrealCorePlugin = new UnrealCorePlugin(this);

        RoleMasterController.load();

        Objects.requireNonNull(Bukkit.getPluginCommand("rolemaster")).setExecutor(new RoleMasterCommand());
        Objects.requireNonNull(Bukkit.getPluginCommand("rolemaster")).setTabCompleter(new RoleMasterTab());


        Bukkit.getPluginManager().registerEvents(new RoleMasterListener(), this);
        Bukkit.getPluginManager().registerEvents(new EquipmentItemListener(), this);
        if(Bukkit.getServer().getPluginManager().getPlugin("UnrealResource") != null){
            Bukkit.getPluginManager().registerEvents(new ResourceListener(), this);
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


}
