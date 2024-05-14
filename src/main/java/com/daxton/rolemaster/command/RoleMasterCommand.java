package com.daxton.rolemaster.command;

import com.daxton.rolemaster.RoleMaster;
import com.daxton.rolemaster.api.entity.EntityPack;
import com.daxton.rolemaster.controller.RoleMasterController;
import com.daxton.rolemaster.controller.RolePlayerController;
import com.daxton.unrealcore.application.method.SchedulerFunction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RoleMasterCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1){
            if(args[0].equalsIgnoreCase("reload")){
                if(sender instanceof Player){
                    Player player = (Player) sender;
                    if(!player.isOp()){
                        return true;
                    }
                    player.sendMessage("[RoleMaster] Reload");
                }
                RoleMaster.sendSystemLogger("Reload");
                RoleMasterController.reload();
                return true;
            }
            if(args[0].equalsIgnoreCase("gui")){
                if(sender instanceof Player){
                    Player player = (Player) sender;
                    RoleMasterController.openGUI(player);
                }
            }
            if(args[0].equalsIgnoreCase("test")){
                if(sender instanceof Player){
                    Player player = (Player) sender;

                }

            }
        }
        if(sender instanceof Player){
            Player player = (Player) sender;
            if(!player.isOp()){
                return true;
            }
            player.sendMessage("[RoleMaster] Reload");
        }
//        if(args.length == 2){
//            if(args[0].equalsIgnoreCase("test")){
//                if(sender instanceof Player){
//                    Player player = (Player) sender;
//                    EntityPack.setSwim(player, args[1]);
//                }
//            }
//        }
        if(args.length == 4){
            if(args[0].equalsIgnoreCase("role")){
                Player player = Bukkit.getPlayer(args[3]);
                String roleName = args[2];
                if(player != null){
                    RolePlayerController.changeRole(player, roleName);
                }
            }
        }

        return false;
    }
}
