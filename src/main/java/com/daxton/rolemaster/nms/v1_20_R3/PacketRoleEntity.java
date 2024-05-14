package com.daxton.rolemaster.nms.v1_20_R3;


import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.daxton.rolemaster.RoleMaster;
import com.daxton.rolemaster.nms.NMSVersion;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
@Setter
@ToString
public class PacketRoleEntity {

    //生物ID
    private int entityID;
    //UUID
    private UUID uuid;
    //座標
    private Location location;
    //仰角
    private float pitch;
    //偏航角
    private float yaw;
    //加上玩家仰角
    private boolean caster_pitch = false;
    //加上玩家偏航角
    private boolean caster_yaw = false;

    public PacketRoleEntity(Location location) {
        this.entityID = (int)(Math.random() * Integer.MAX_VALUE);
        this.uuid = UUID.randomUUID();
        this.location = location.clone();
        this.pitch = this.location.getPitch()*-1;
        this.yaw = this.location.getYaw();
    }
    //建立
    public void spawn(){
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SPAWN_ENTITY);
        packet.getModifier().writeDefaults();
        packet.getIntegers().write(0, this.entityID);
        packet.getUUIDs().write(0, uuid);
        packet.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
        packet.getDoubles().write(0, location.getX());
        packet.getDoubles().write(1, location.getY());
        packet.getDoubles().write(2, location.getZ());
        if(this.caster_pitch){
            packet.getBytes().write(0, rotToByte(this.pitch));
        }
        if(this.caster_yaw){
            packet.getBytes().write(1, rotToByte(this.yaw));
        }

        sendPack(packet);

    }


    //傳送目標
    public void teleport(Location inputLocation){
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_TELEPORT);
        packet.getIntegers().write(0, entityID);

        packet.getDoubles().write(0, inputLocation.getX());
        packet.getDoubles().write(1, inputLocation.getY());
        packet.getDoubles().write(2, inputLocation.getZ());
        if(this.caster_pitch){
            packet.getBytes().write(1, rotToByte(this.pitch));
        }
        if(this.caster_yaw){
            packet.getBytes().write(0, rotToByte(this.yaw));
        }
        sendPack(packet);
    }

    //刪除
    public void remove(){
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        if(NMSVersion.compareNMSVersion("1.17")){
            List<Integer> intList = new ArrayList<>();
            intList.add(entityID);
            packet.getIntLists().write(0, intList);
        }else {
            packet.getIntegerArrays().write(0, new int[]{ entityID });
        }
        sendPack(packet);
    }

    public byte rotToByte(float var0) {
        return (byte)((int)(var0 * 256.0F / 360.0F));
    }
    //設置顯示名稱
    public void setCustomName(String text, Boolean always){
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);
        packet.getModifier().writeDefaults();
        packet.getIntegers().write(0, entityID);

        WrappedDataWatcher metadata = new WrappedDataWatcher();
        Optional<?> opt = Optional.of(WrappedChatComponent.fromChatMessage(text)[0].getHandle());

        metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true)), opt);
        metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, WrappedDataWatcher.Registry.get(Boolean.class)), always);

        sendMetadata(metadata, packet);
    }
    //獲取UUID字串
    public String getUUIDString(){
        return this.uuid.toString();
    }



    //設置是否可見
    public void setVisible(boolean visible) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);
        packet.getIntegers().write(0, entityID);
        WrappedDataWatcher metadata = new WrappedDataWatcher();
        WrappedDataWatcher.Serializer serializer = WrappedDataWatcher.Registry.get(Byte.class);
        if(visible){

            metadata.setObject(0, serializer, (byte) (0x20));
        }else {

            metadata.setObject(0, serializer, (byte) (0x20));

        }
        packet.getWatchableCollectionModifier().write(0, metadata.getWatchableObjects());
        sendMetadata(metadata, packet);
    }

    //發送元數據
    public void sendMetadata(WrappedDataWatcher metadata, PacketContainer packet){
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
