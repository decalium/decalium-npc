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

import com.destroystokyo.paper.profile.CraftPlayerProfile;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_16_R3.CraftParticle;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.gepron1x.npc.api.NonPlayerCharacter;
import org.gepron1x.npc.plugin.tinyprotocol.InternalPlayer;
import org.gepron1x.npc.plugin.tinyprotocol.Packets;
import org.joor.Reflect;

public final class Packets1_16 implements Packets {

	

	private EntityPlayer createEntityPlayer(NonPlayerCharacter npc) {
		EntityLiving realEntity = npc.realEntity().map(CraftLivingEntity.class::cast).map(CraftLivingEntity::getHandle).get();
		MinecraftServer server = realEntity.getMinecraftServer();
		WorldServer world = (WorldServer) realEntity.getWorld();
		EntityPlayer player = new EntityPlayer(server, world, ((CraftPlayerProfile) npc.profile()).getGameProfile(), new PlayerInteractManager(world));
		Reflect.on(player).set("id", realEntity.getId());
		IChatBaseComponent displayName = PaperAdventure.asVanilla(npc.displayName());
		player.listName = displayName;
		player.adventure$displayName = npc.displayName();
		Vec3D pos = realEntity.getPositionVector();
		player.setUUID(realEntity.getUniqueID());
		player.setLocation(pos.x, pos.y, pos.z, player.yaw, player.pitch);
		player.passengers.add(createNameTag(player, displayName));
		return player;
	}

	private EntityAreaEffectCloud createNameTag(EntityPlayer player, IChatBaseComponent name) {
		EntityAreaEffectCloud effectCloud = new EntityAreaEffectCloud(player.world, player.locX(), player.locY(), player.locZ());
		effectCloud.setParticle(CraftParticle.toNMS(Particle.TOWN_AURA));
		effectCloud.setCustomName(name);
		effectCloud.setWaitTime(0);
		effectCloud.setRadius(0);
		effectCloud.setDuration(Integer.MAX_VALUE);
		return effectCloud;
	}

	@Override
	public InternalPlayer player(NonPlayerCharacter npc) {
		return new InternalPlayer1_16(createEntityPlayer(npc), npc);
	}
}
