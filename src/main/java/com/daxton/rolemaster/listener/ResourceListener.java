package com.daxton.rolemaster.listener;


import com.daxton.rolemaster.controller.RoleHUDController;
import com.daxton.unrealcore.communication.event.PlayerConnectionSuccessfulEvent;

import com.daxton.unrealresource.event.UnrealResourceLoadFinishEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ResourceListener implements Listener {

    @EventHandler//當玩家資源加載成功
    public void onPlayerJoin(UnrealResourceLoadFinishEvent event) {
        Player player = event.getPlayer();
        RoleHUDController.setDefaultHUD(player);
    }

}
