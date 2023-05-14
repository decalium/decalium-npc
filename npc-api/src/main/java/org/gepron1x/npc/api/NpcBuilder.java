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
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Collection;
import java.util.function.Consumer;

public interface NpcBuilder {
	NpcBuilder displayName(Component displayName);
	NpcBuilder location(Location location);

	NpcBuilder profileProperties(Collection<ProfileProperty> properties);

	default NpcBuilder profileProperties(PlayerProfile profile) {
		return profileProperties(profile.getProperties());
	}

	NpcBuilder action(EquipmentSlot slot, ClickAction action);

	NpcBuilder editEntity(Consumer<LivingEntity> consumer);


	NonPlayerCharacter spawn();
}
