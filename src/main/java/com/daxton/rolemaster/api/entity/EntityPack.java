package com.daxton.rolemaster.api.entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.daxton.rolemaster.RoleMaster;
import com.daxton.rolemaster.api.crawl.CrPlayer;
import com.daxton.rolemaster.nms.NMSVersion;
import com.daxton.rolemaster.nms.v1_16.NmsPackets_v1_16;
import com.google.common.collect.Lists;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.syncher.DataWatcher;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.*;

public class EntityPack {

    public static int entityID = -8854;

    public static int entityID2 = -8855;

    public static CrPlayer crPlayer;

    public static void spawn3(Player player){
        if(crPlayer == null){
            crPlayer = new CrPlayer(player);
        }
        if(crPlayer.isCraw()){
            crPlayer.stopCrawling();
        }else {
            crPlayer.startCrawling();
        }
//        if(nmsPacketsV116 == null){
//            nmsPacketsV116 = new NmsPackets_v1_16();
//
//
//        }
//        Block blockAbovePlayer = player.getLocation().add(0.0, 1.5, 0.0).getBlock();
//        Block floorBlock = blockAbovePlayer.getLocation().subtract(0.0, 2.0, 0.0).getBlock();
//        nmsPacketsV116.spawnFakeBlocks(player, blockAbovePlayer, floorBlock, Material.AIR);

    }

    public static void spawn2(Player player, Location location){
//        if(dataWatcher == null){
//            World world = (World)Bukkit.getWorlds().get(0);
//            FallingBlock fallingBlock = (FallingBlock)world.spawnEntity(new Location(world, 0.0, 0.0, 0.0), EntityType.FALLING_BLOCK);
//            fallingBlock.setGravity(false);
//            dataWatcher = ((CraftEntity)fallingBlock).getHandle().an();
//
//            fallingBlock.remove();
//        }


        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SPAWN_ENTITY);
        packet.getModifier().writeDefaults();
        packet.getIntegers().write(0, entityID2);
        packet.getUUIDs().write(0, UUID.randomUUID());
        packet.getEntityTypeModifier().write(0, EntityType.FALLING_BLOCK);
        packet.getDoubles().write(0, location.getX());
        packet.getDoubles().write(1, location.getY());
        packet.getDoubles().write(2, location.getZ());

        packet.getIntegers().write(4, 1);

        sendPack(packet);

//        if(dataWatcher != null){
//            PacketPlayOutEntityMetadata blockMetadataPacket = new PacketPlayOutEntityMetadata(-8854, dataWatcher.b());
//            ((CraftPlayer)player).getHandle().c.a(blockMetadataPacket);
//        }
//        gravity();
    }

    public static void spawn(Player player, Location location){
//        if(dataWatcher == null){
//            World world = (World)Bukkit.getWorlds().get(0);
//            FallingBlock fallingBlock = (FallingBlock)world.spawnEntity(new Location(world, 0.0, 0.0, 0.0), EntityType.FALLING_BLOCK);
//            fallingBlock.setGravity(false);
//            dataWatcher = ((CraftEntity)fallingBlock).getHandle().an();
//
//            fallingBlock.remove();
//        }


        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SPAWN_ENTITY);
        packet.getModifier().writeDefaults();
        packet.getIntegers().write(0, entityID);
        packet.getUUIDs().write(0, UUID.randomUUID());
        packet.getEntityTypeModifier().write(0, EntityType.FALLING_BLOCK);
        packet.getDoubles().write(0, location.getX());
        packet.getDoubles().write(1, location.getY());
        packet.getDoubles().write(2, location.getZ());

        packet.getIntegers().write(4, 1);

        sendPack(packet);

//        if(dataWatcher != null){
//            PacketPlayOutEntityMetadata blockMetadataPacket = new PacketPlayOutEntityMetadata(-8854, dataWatcher.b());
//            ((CraftPlayer)player).getHandle().c.a(blockMetadataPacket);
//        }
//        gravity();
    }

    public static void gravity(){
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);
        packet.getModifier().writeDefaults();
        packet.getIntegers().write(0, entityID);

        WrappedDataWatcher metadata = new WrappedDataWatcher();


        Optional<?> opt = Optional.of(WrappedChatComponent.fromChatMessage("TEST")[0].getHandle());

        metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true)), opt);
        metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, WrappedDataWatcher.Registry.get(Boolean.class)), true);
        metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(5, WrappedDataWatcher.Registry.get(Boolean.class)), false);

        sendMetadata(metadata, packet);
    }

    //設置是否可見
    public static void setSwim(LivingEntity livingEntity, String text) {
        int intValue = Integer.parseInt(text.substring(2), 16);
        byte byteValue = (byte) intValue;
        RoleMaster.unrealCorePlugin.sendLogger("設置狀態");
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);
        packet.getIntegers().write(0, livingEntity.getEntityId());
        WrappedDataWatcher metadata = new WrappedDataWatcher();
        WrappedDataWatcher.Serializer serializer = WrappedDataWatcher.Registry.get(Byte.class);
        metadata.setObject(0, serializer, byteValue); //(byte) (0x20)
        packet.getWatchableCollectionModifier().write(0, metadata.getWatchableObjects());
        sendMetadata(metadata, packet);
    }

    //發送元數據
    public static void sendMetadata(WrappedDataWatcher metadata, PacketContainer packet){
        if(NMSVersion.compareNMSVersion("1.19.3")){
            final List<WrappedDataValue> wrappedDataValueList = Lists.newArrayList();
            metadata.getWatchableObjects().stream().filter(Objects::nonNull).forEach(entry -> {
                final WrappedDataWatcher.WrappedDataWatcherObject dataWatcherObject = entry.getWatcherObject();
                wrappedDataValueList.add(new WrappedDataValue(dataWatcherObject.getIndex(), dataWatcherObject.getSerializer(), entry.getRawValue()));
            });

            packet.getDataValueCollectionModifier().write(0, wrappedDataValueList);
        }else {
            packet.getWatchableCollectionModifier().write(0, metadata.getWatchableObjects());
        }
        sendPack(packet);
    }

    //發送封包
    public static void sendPack(PacketContainer packet){
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        List<Player> playerList = new ArrayList<>(onlinePlayers);
        playerList.forEach(player -> {
            ProtocolLibrary.getProtocolManager().sendServerPacket( player, packet );
        });

    }

}
