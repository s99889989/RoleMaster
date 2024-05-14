package com.daxton.rolemaster.api.crawl;

import com.daxton.rolemaster.RoleMaster;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

public class Utils {
    public static BlockData BARRIER_BLOCK_DATA;
    public static void revertBlockPacket(Player player, Block block) {
        player.sendBlockChange(block.getLocation(), block.getBlockData());
        Bukkit.getScheduler().runTask(RoleMaster.roleMaster, () -> {
            block.getState().update();
        });
    }

    static {
        BARRIER_BLOCK_DATA = Bukkit.createBlockData(Material.BARRIER);

    }

}
