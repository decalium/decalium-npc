/*
 * decalium-npc
 * Copyright © 2023 George Pronyuk <https://vk.com/gpronyuk>
 *
 * decalium-npc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * decalium-npc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with decalium-npc. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */
package org.gepron1x.npc.plugin;

import com.mojang.authlib.GameProfile;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.papermc.paper.adventure.PaperAdventure;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.craftbukkit.v1_16_R3.CraftParticle;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftMagicNumbers;
import org.gepron1x.npc.api.NonPlayerCharacter;
import org.gepron1x.npc.plugin.tinyprotocol.InternalPlayer;
import org.bukkit.Particle;
import org.joor.Reflect;

import java.util.List;

import static org.joor.Reflect.on;

public final class InternalPlayer1_16 implements InternalPlayer {


	public static final byte SKIN_PARTS = 0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40;

	private final EntityPlayer entityPlayer;
	private final NonPlayerCharacter npc;

	public InternalPlayer1_16(EntityPlayer entityPlayer, NonPlayerCharacter npc) {

		this.entityPlayer = entityPlayer;
		this.npc = npc;
	}
	@Override
	public Object spawnPacket() {
		PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(entityPlayer);
		on(spawn).set("a", npc.entityId());
		return spawn;
	}

	@Override
	public Object metadataPacket() {
		entityPlayer.getDataWatcher().set(new DataWatcherObject<>(16, DataWatcherRegistry.a), SKIN_PARTS);
		return new PacketPlayOutEntityMetadata(npc.entityId(), entityPlayer.getDataWatcher(), true);
	}

	@Override
	public Object playerInfoPacket() {
		return new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer);
	}

	@Override
	public Object removePlayerPacket() {
		return new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer);
	}

	@Override
	public void sendDisplayName(Channel channel) {


		ChannelPipeline pipeline = channel.pipeline();
		Entity name = entityPlayer.passengers.get(0);
		pipeline.writeAndFlush(new PacketPlayOutSpawnEntity(name));
		pipeline.writeAndFlush(new PacketPlayOutEntityMetadata(name.getId(), name.getDataWatcher(), true));
		pipeline.writeAndFlush(new PacketPlayOutMount(entityPlayer));
	}
}
