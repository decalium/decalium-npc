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
package org.gepron1x.npc.api;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.common.collect.Multimap;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface NonPlayerCharacter {

	String identifier();

	int entityId();

	UUID uniqueId();

	EntityData data();


	NonPlayerCharacter spawn(Location location);

	Set<ProfileProperty> profileProperties();


	Optional<LivingEntity> realEntity();

	void on(PlayerInteractAtEntityEvent event);

	Multimap<EquipmentSlot, ClickAction> interaction();

	Component displayName();
	void displayName(Component displayName);

	default PlayerProfile profile() {
		PlayerProfile profile = Bukkit.createProfile(uniqueId(), playerName());
		profile.setProperties(profileProperties());
		return profile;
	}

	default String playerName() {
		return formatPlayerName(identifier());
	}

	static String formatPlayerName(String identifier) {
		return "npc_"+identifier;
	}



}
