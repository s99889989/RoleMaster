package com.daxton.rolemaster.nms.v1_16;
import java.util.UUID;

import com.daxton.rolemaster.nms.NmsPackets;
import net.minecraft.server.v1_16_R3.DataWatcher;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_16_R3.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_16_R3.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
public class NmsPackets_v1_16 implements NmsPackets {

    private final int blockId = -8854;
    private final int floorBlockId = -8855;
    private final DataWatcher dataWatcher;

    public NmsPackets_v1_16() {
        World world = (World)Bukkit.getWorlds().get(0);
        FallingBlock fallingBlock = (FallingBlock)world.spawnEntity(new Location(world, 0.0, 0.0, 0.0), EntityType.FALLING_BLOCK);
        fallingBlock.setGravity(false);
        this.dataWatcher = ((CraftEntity)fallingBlock).getHandle().getDataWatcher();
        fallingBlock.remove();
    }

    public void spawnFakeBlocks(Player player, Block block, Block floorBlock, Material fakeFloorMaterial) {
//        this.getClass();
//        PacketPlayOutSpawnEntity spawnBlockPacket = new PacketPlayOutSpawnEntity(-8854, UUID.randomUUID(), (double)block.getX() + 0.5, (double)block.getY(), (double)block.getZ() + 0.5, 0.0F, 0.0F, EntityTypes.FALLING_BLOCK, net.minecraft.server.v1_16_R3.Block.getCombinedId(((CraftBlock)block).getNMS()), new Vec3D(0.0, 1.0, 0.0));
//        this.getClass();
//        PacketPlayOutEntityMetadata blockMetadataPacket = new PacketPlayOutEntityMetadata(-8854, this.dataWatcher, true);
//        this.getClass();
//        PacketPlayOutSpawnEntity spawnBlockPacket2 = new PacketPlayOutSpawnEntity(-8855, UUID.randomUUID(), (double)floorBlock.getX() + 0.5, (double)((float)floorBlock.getY() + 0.001F), (double)floorBlock.getZ() + 0.5, 90.0F, 0.0F, EntityTypes.FALLING_BLOCK, net.minecraft.server.v1_16_R3.Block.getCombinedId(((CraftBlock)floorBlock).getNMS()), new Vec3D(0.0, 1.0, 0.0));
//        this.getClass();
//        PacketPlayOutEntityMetadata blockMetadataPacket2 = new PacketPlayOutEntityMetadata(-8855, this.dataWatcher, true);
//        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(spawnBlockPacket);
//        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(blockMetadataPacket);
//        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(spawnBlockPacket2);
//        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(blockMetadataPacket2);
        player.sendBlockChange(floorBlock.getLocation(), fakeFloorMaterial == null ? Bukkit.createBlockData(Material.STONE) : Bukkit.createBlockData(fakeFloorMaterial));
    }

    public void removeFakeBlocks(Player player) {
        int[] var10002 = new int[1];
        this.getClass();
        var10002[0] = -8854;
        PacketPlayOutEntityDestroy destroyOldBlockPacket = new PacketPlayOutEntityDestroy(var10002);
        var10002 = new int[1];
        this.getClass();
        var10002[0] = -8854 - 1;
        PacketPlayOutEntityDestroy destroyOldBlockPacket2 = new PacketPlayOutEntityDestroy(var10002);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(destroyOldBlockPacket);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(destroyOldBlockPacket2);
    }

}
