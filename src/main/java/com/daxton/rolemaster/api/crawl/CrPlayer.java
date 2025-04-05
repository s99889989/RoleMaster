package com.daxton.rolemaster.api.crawl;

import com.daxton.rolemaster.RoleMaster;

import com.daxton.rolemaster.skill.RolePostMechanic;
import com.daxton.unrealcore.application.method.SchedulerFunction;
import lombok.Getter;
import lombok.Setter;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;


@Getter
@Setter
public class CrPlayer {

    private final Player player;
    private final String uuidString;
    private Block barrierBlock;
    private SchedulerFunction.RunTask moveTask;

    private boolean craw = false;
    public CrPlayer(Player player) {
        this.player = player;
        this.uuidString = this.player.getUniqueId().toString();
    }

    public void startCrawling() {
        if(craw){
            return;
        }
        if(this.player == null){
            RolePostMechanic.crPlayerMap.remove(this.uuidString);
            return;
        }
        craw = true;
        this.barrierBlock = this.player.getLocation().getBlock();
//        this.player.setSwimming(true);

        this.moveTask = SchedulerFunction.runTimer(RoleMaster.unrealCorePlugin.getJavaPlugin(), ()->{
            if(this.player == null){
                this.moveTask.cancel();
                RolePostMechanic.crPlayerMap.remove(this.uuidString);
            }
            Block blockAbovePlayer = this.player.getLocation().add(0.0, 1.5, 0.0).getBlock();
            if (!this.barrierBlock.equals(blockAbovePlayer)) {
                this.replaceBarrier(blockAbovePlayer);
            }

        }, 0L, 1L);
    }

    public void replaceBarrier(Block blockAbovePlayer) {
        Utils.revertBlockPacket(this.player, this.barrierBlock);
        Utils.revertBlockPacket(this.player, this.barrierBlock.getLocation().subtract(0.0, 2.0, 0.0).getBlock());
        this.barrierBlock = blockAbovePlayer;
        if (blockAbovePlayer.isPassable()) {
            this.player.sendBlockChange(blockAbovePlayer.getLocation(), Utils.BARRIER_BLOCK_DATA);
        }

    }

    public void stopCrawling() {
        if(!craw){
            return;
        }
        if(this.player == null){
            RolePostMechanic.crPlayerMap.remove(this.uuidString);
            return;
        }
        craw = false;
        this.player.setSwimming(false);
        if (this.barrierBlock != null) {
            Utils.revertBlockPacket(this.player, this.barrierBlock);
            Utils.revertBlockPacket(this.player, this.barrierBlock.getLocation().subtract(0.0, 2.0, 0.0).getBlock());
//            nmsPacketManager.removeFakeBlocks(this.player);
        }

        if (this.moveTask != null) {
            this.moveTask.cancel();
        }

    }

}
