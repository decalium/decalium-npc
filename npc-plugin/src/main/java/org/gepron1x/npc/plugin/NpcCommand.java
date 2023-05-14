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

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.keys.CloudKey;
import cloud.commandframework.keys.SimpleCloudKey;
import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.gepron1x.npc.api.NonPlayerCharacter;
import org.gepron1x.npc.plugin.npc.NpcMap;
import org.gepron1x.npc.plugin.storage.NpcStorage;

import java.util.Arrays;
import java.util.Objects;

public final class NpcCommand {

	private static final CloudKey<NonPlayerCharacter> NPC = SimpleCloudKey.of("npc", TypeToken.get(NonPlayerCharacter.class));
	private final NpcMap map;
	private final NpcStorage storage;




	public NpcCommand(NpcMap map, NpcStorage storage) {

		this.map = map;
		this.storage = storage;
	}

	public void register(CommandManager<CommandSender> manager) {
		Command.Builder<CommandSender> builder = manager.commandBuilder("npc");
		manager.command(builder.literal("name").argument(manager.argumentBuilder(NonPlayerCharacter.class, "npc")).senderType(Player.class)
				.argument(StringArgument.greedyFlagYielding("name")).permission("npc.displayname").senderType(Player.class).handler(this::displayName));
		manager.command(builder.literal("equipment").argument(manager.argumentBuilder(NonPlayerCharacter.class, "npc")).permission("npc.equipment").handler(this::equipment));
		manager.command(builder.literal("respawn")
				.argument(manager.argumentBuilder(NonPlayerCharacter.class, "npc"))
				.permission("npc.respawn")
				.senderType(Player.class)
				.handler(this::respawn));
	}


	private void displayName(CommandContext<CommandSender> ctx) {
		NonPlayerCharacter npc = ctx.get("npc");
		npc.displayName(MiniMessage.miniMessage().deserialize(ctx.get("name")));
		storage.save(npc);
	}

	private void equipment(CommandContext<CommandSender> ctx) {
		Player player = (Player) ctx.getSender();
		NonPlayerCharacter npc = ctx.get("npc");
		npc.realEntity().ifPresent(e -> {
			EntityEquipment eq = Objects.requireNonNull(e.getEquipment());
			EntityEquipment pq = Objects.requireNonNull(player.getEquipment());
			for(EquipmentSlot slot : EquipmentSlot.values()) {
				eq.setItem(slot, pq.getItem(slot));
			}
		});
	}

	private void respawn(CommandContext<CommandSender> ctx) {
		Player player = (Player) ctx.getSender();
		NonPlayerCharacter npc = ctx.get("npc");
		npc.realEntity().ifPresent(Entity::remove);
		map.add(npc.spawn(player.getLocation()));
		storage.save(npc);
	}

}
