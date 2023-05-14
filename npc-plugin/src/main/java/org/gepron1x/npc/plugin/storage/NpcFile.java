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
package org.gepron1x.npc.plugin.storage;

import com.destroystokyo.paper.profile.ProfileProperty;
import com.destroystokyo.paper.util.SneakyThrow;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.gepron1x.npc.api.ClickAction;
import org.gepron1x.npc.api.EntityData;
import org.gepron1x.npc.api.NonPlayerCharacter;
import org.gepron1x.npc.plugin.npc.NonPlayerCharacterImpl;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import ru.xezard.glow.data.glow.Glow;
import ru.xezard.glow.data.glow.IGlow;
import ru.xezard.glow.data.glow.manager.GlowsManager;

import java.util.*;
import java.util.stream.Collectors;

public final class NpcFile {
	private final ConfigurationNode node;

	public NpcFile(ConfigurationNode node) {

		this.node = node;
	}

	public static Optional<IGlow> npcGlow(String id) {
		return GlowsManager.getInstance().getGlowByName(NonPlayerCharacter.formatPlayerName(id));
	}


	public NonPlayerCharacter construct() throws SerializationException {
		String id = node.node("id").require(String.class);
		UUID entityId = node.node("uuid").require(UUID.class);
		Component displayName = node.node("display-name").require(Component.class);
		Set<ProfileProperty> propertySet = new HashSet<>(node.node("profile-properties").getList(ProfileProperty.class, Collections.emptyList()));
		EntityData data = node.node("data").require(EntityData.class);

		Multimap<EquipmentSlot, ClickAction> actions = HashMultimap.create();
		ConfigurationNode actionNode = node.node("actions");

		for (ConfigurationNode configurationNode : actionNode.childrenMap().values()) {
			actions.putAll(EquipmentSlot.valueOf(Objects.requireNonNull(configurationNode.key()).toString()),
					configurationNode.getList(String.class, Collections.emptyList()).stream().map(ClickAction::parse).collect(Collectors.toList()));
		}

		ChatColor color = node.node("glow-color").get(ChatColor.class, ChatColor.RESET);
		if(color != ChatColor.RESET) {
			var glow = npcGlow(id).orElseGet(() -> Glow.builder().name(NonPlayerCharacter.formatPlayerName(id)).color(color).build());
			glow.setColor(color);
			glow.display(Bukkit.getOnlinePlayers());
		}

		return new NonPlayerCharacterImpl(id, entityId, data, propertySet, displayName, actions);
	}

	public void save(NonPlayerCharacter npc) throws SerializationException {
		node.node("id").set(npc.identifier());
		node.node("uuid").set(npc.uniqueId());
		node.node("display-name").set(npc.displayName());
		node.node("profile-properties").setList(ProfileProperty.class, new ArrayList<>(npc.profileProperties()));
		node.node("data").set(npc.data());
		var color = npcGlow(npc.identifier()).map(IGlow::getColor).orElse(null);
		if(color != null) node.node("glow-color").set(ChatColor.class, color);
		ConfigurationNode actionsNode = node.node("actions");
		npc.interaction().forEach((slot, value) -> {
			try {
				actionsNode.node(slot.name()).appendListNode().set(ClickAction.class, value);
			} catch (SerializationException e) {
				SneakyThrow.sneaky(e);
			}
		});

	}
}
