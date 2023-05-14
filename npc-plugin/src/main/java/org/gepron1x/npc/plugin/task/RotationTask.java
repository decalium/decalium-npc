package org.gepron1x.npc.plugin.task;

import net.minecraft.server.v1_16_R3.PacketPlayOutEntity;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityHeadRotation;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.gepron1x.npc.api.NonPlayerCharacter;
import org.gepron1x.npc.plugin.tinyprotocol.TinyProtocol;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class RotationTask implements Runnable {


    private final ConcurrentMap<Integer, NonPlayerCharacter> npcs;
    private final TinyProtocol protocol;

    public RotationTask(ConcurrentMap<Integer, NonPlayerCharacter> npcs, TinyProtocol protocol) {

        this.npcs = npcs;
        this.protocol = protocol;
    }
    /**
     *
     */
    @Override
    public void run() {
        for(NonPlayerCharacter npc : npcs.values()) {
            npc.realEntity().ifPresent(entity -> {
                entity.getLocation().getNearbyPlayers(16).forEach(player -> {
                    Location npcLocation = entity.getLocation();
                    Location location = npcLocation.setDirection(player.getLocation().subtract(npcLocation).toVector());
                    float yaw = location.getYaw();
                    float pitch = location.getPitch();
                    protocol.sendPacket(player, new PacketPlayOutEntity.PacketPlayOutEntityLook(entity.getEntityId(), (byte) ((yaw%360.)*256/360), (byte) ((pitch%360.)*256/360), false));
                    protocol.sendPacket(player, new PacketPlayOutEntityHeadRotation(((CraftEntity) entity).getHandle(), (byte) ((yaw%360.)*256/360)));
                });
            });
        }
    }
}
