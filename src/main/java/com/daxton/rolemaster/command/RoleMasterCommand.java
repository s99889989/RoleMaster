package com.daxton.rolemaster.command;

import com.daxton.rolemaster.RoleMaster;
import com.daxton.rolemaster.api.entity.EntityPack;
import com.daxton.rolemaster.controller.RoleMasterController;
import com.daxton.rolemaster.controller.RolePlayerAddController;
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
        Player useCommandPlayer = null;
        if(sender instanceof Player){
            useCommandPlayer = (Player) sender;
        }

        if (args.length == 1){

            if(args[0].equalsIgnoreCase("gui")){
                if(useCommandPlayer != null){
                    RoleMasterController.openGUI(useCommandPlayer);
                }
            }


        }
        if(useCommandPlayer != null){
            if(!useCommandPlayer.isOp()){
                return true;
            }
        }

        if (args.length == 1){
            if(args[0].equalsIgnoreCase("reload")){
                if(useCommandPlayer != null){

                    if(!useCommandPlayer.isOp()){
                        return true;
                    }
                    useCommandPlayer.sendMessage("[RoleMaster] Reload");
                }
                RoleMaster.unrealCorePlugin.sendSystemLogger("Reload");
                RoleMasterController.reload();
                return true;
            }
        }

        if(args.length == 4){
            if(args[0].equalsIgnoreCase("role")){
                Player targetPlayer = Bukkit.getPlayer(args[3]);
                String roleName = args[2];
                if(targetPlayer != null){
                    RolePlayerController.changeRole(targetPlayer, roleName);
                }
            }
            if(args[0].equalsIgnoreCase("skill")){
                if(args[1].equalsIgnoreCase("point_add")){
                    Player targetPlayer = Bukkit.getPlayer(args[2]);
                    String amount = args[3];
                    if(targetPlayer != null){

                        RolePlayerAddController.addPointSkillNow(targetPlayer, amount);
                        if(useCommandPlayer != null){
                            useCommandPlayer.sendMessage("[RoleMaster] "+targetPlayer.getDisplayName()+" 增加 "+amount+" 點技能點數!");
                        }
                    }
                }
            }
        }

        return false;
    }
}
