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
package org.gepron1x.npc.plugin.tinyprotocol;

import com.destroystokyo.paper.profile.PlayerProfile;
import io.netty.channel.Channel;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.gepron1x.npc.api.NonPlayerCharacter;
import org.gepron1x.npc.api.NonPlayerCharacters;
import org.gepron1x.npc.plugin.InternalPlayer1_16;
import org.joor.Reflect;
import ru.xezard.glow.data.glow.manager.GlowsManager;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.joor.Reflect.*;

public class NpcListener extends TinyProtocol {

	private final Packets packets;
	private final NonPlayerCharacters nonPlayerCharacters;


	/**
	 * Construct a new instance of TinyProtocol, and start intercepting packets for all connected clients and future clients.
	 * <p>
	 * You can construct multiple instances per plugin.
	 *
	 * @param plugin - the plugin.
	 */
	public NpcListener(Plugin plugin, Packets packets, NonPlayerCharacters nonPlayerCharacters) {
		super(plugin);
		this.packets = packets;
		this.nonPlayerCharacters = nonPlayerCharacters;
	}

	@Override
	public Object onPacketOutAsync(Player receiver, Channel channel, Object packet) {
		Reflect reflect = on(packet);
		if(packet instanceof PacketPlayOutSpawnEntityLiving) {
			int id = reflect.field("a").get();
			Optional<NonPlayerCharacter> optNpc = nonPlayerCharacters.npc(id);
			if(!optNpc.isPresent()) return packet;
			NonPlayerCharacter npc = optNpc.get();
			InternalPlayer player = packets.player(npc);
			sendPacket(channel, player.playerInfoPacket());
			plugin.getServer().getScheduler().runTaskLater(plugin, () -> sendPacket(receiver, player.removePlayerPacket()), 60);
			sendPacket(channel, player.spawnPacket());
			// sendPacket(channel, scoreboard(npc));
			player.sendDisplayName(channel);
			return player.metadataPacket();
		} else if(packet instanceof PacketPlayOutEntityMetadata) {
			int id = reflect.field("a").get();
			nonPlayerCharacters.npc(id).ifPresent($ -> {
				List<DataWatcher.Item<?>> list = on(packet).field("b").get();
				list.removeIf(i -> {
					int index = i.a().a();
					return index >= 14 && index <= 19;
				});
				list.add(new DataWatcher.Item<>(new DataWatcherObject<>(16, DataWatcherRegistry.a), InternalPlayer1_16.SKIN_PARTS));
			});
		} else if(packet instanceof PacketPlayOutScoreboardTeam) {
			String team = reflect.field("a").get();
			if(team.startsWith("npc_")) {
				reflect.set("e", ScoreboardTeamBase.EnumNameTagVisibility.NEVER.e);
				Collection<String> names = reflect.get("h");
				names.add(team); // team name is equivalent to npc profile's name
			}
		}
		return packet;
	}

	public PacketPlayOutScoreboardTeam scoreboard(NonPlayerCharacter npc) {

		Scoreboard scoreboard = new Scoreboard();
		ScoreboardTeam team = npc.realEntity().flatMap(GlowsManager.getInstance()::getGlowByEntity).map(glow -> {
			ScoreboardTeam t = new ScoreboardTeam(scoreboard, npc.playerName() + "_team");
			t.setColor(CraftChatMessage.getColor(glow.getColor()));
			return t;
		}).orElseGet(() -> new ScoreboardTeam(scoreboard, "scoreboard_team"));
		team.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.NEVER);
		return team.getColor() == EnumChatFormat.RESET ? new PacketPlayOutScoreboardTeam(team, List.of(npc.playerName()), 3) : new PacketPlayOutScoreboardTeam(team, 0);
	}

	@Override
	public Object onPacketInAsync(Player sender, Channel channel, Object packet) {
		return super.onPacketInAsync(sender, channel, packet);
	}
}
