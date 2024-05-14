package com.daxton.rolemaster.listener;


import com.daxton.rolemaster.been.RolePlayer;
import com.daxton.rolemaster.controller.RolePlayerController;
import com.daxton.rolemaster.controller.RolePlayerGetController;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class EquipmentItemListener implements Listener {

    //玩家登入
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();


        RolePlayer rolePlayer = RolePlayerGetController.getRolePlayer(player);
        if(rolePlayer == null){
            return;
        }
        rolePlayer.changeMainItem(player.getInventory().getItemInMainHand());
    }

    //玩家登出
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        RolePlayer rolePlayer = RolePlayerGetController.getRolePlayer(player);
        if(rolePlayer == null){
            return;
        }
        rolePlayer.removeMainAttribute();
    }

    //切換手上位置
    @EventHandler
    public void onItemSwitch(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        ItemStack itemStackNew = player.getInventory().getItem(event.getNewSlot());

        RolePlayer rolePlayer = RolePlayerGetController.getRolePlayer(player);
        if(rolePlayer == null){
            return;
        }
        rolePlayer.changeMainItem(itemStackNew);
    }

    //關閉GUI
    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();


        RolePlayer rolePlayer = RolePlayerGetController.getRolePlayer(player);
        if(rolePlayer == null){
            return;
        }
        rolePlayer.changeMainItem(player.getInventory().getItemInMainHand());
    }



}
