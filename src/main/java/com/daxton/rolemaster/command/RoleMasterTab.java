package com.daxton.rolemaster.command;

import com.daxton.rolemaster.controller.RoleMasterController;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RoleMasterTab implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> commandList = new ArrayList<>();

        if (args.length == 1){
            commandList = getCommandList(sender, new String[]{"gui", "role", "reload", "skill"}, new String[]{"gui"});
        }

        if (args.length == 2){
            if(args[0].equals("role")){
                commandList = getCommandList(sender, new String[]{"change"}, new String[]{""});
            }
            if(args[0].equals("skill")){
                commandList = getCommandList(sender, new String[]{"point_add"}, new String[]{""});
            }
        }

        if (args.length == 3){
            if(args[0].equals("role")){
                commandList = getCommandList(sender, RoleMasterController.roleBeenMap.keySet().toArray(new String[0]), new String[]{""});
            }
            if(args[0].equals("skill")){
                commandList = getCommandList(sender, Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).toArray(String[]::new), new String[]{""});
            }
        }

        if (args.length == 4){
            if(args[0].equals("role")){
                commandList = getCommandList(sender, Bukkit.getOnlinePlayers().stream().map(player -> player.getName()).toArray(String[]::new), new String[]{""});
            }
            if(args[0].equals("skill")){
                commandList = getCommandList(sender, new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "20", "30", "40", "50", "60", "70", "80", "90"}, new String[]{""});
            }
        }

        return commandList;
    }

    //判斷管理和玩家來回傳使用指令
    public static List<String> getCommandList(CommandSender sender, String[] opCommand, String[] playerCommand){
        if(sender instanceof Player){
            Player player = (Player) sender;
            if(!player.isOp()){
                return Arrays.stream(playerCommand).collect(Collectors.toList());
            }
        }
        return Arrays.stream(opCommand).collect(Collectors.toList());
    }

}
