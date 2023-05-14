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

import net.kyori.adventure.text.Component;
import org.bukkit.Particle;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;

public final class NameTag {


	private final Entity entity;

	public NameTag(Entity entity) {

		this.entity = entity;
	}

	public void update(Component displayName) {
			entity.getPassengers().stream().filter(this::isNameTag).findAny().orElseGet(() -> create(entity)).customName(displayName);
	}
	private AreaEffectCloud create(Entity entity) {
		AreaEffectCloud cloud = entity.getLocation().getWorld().spawn(entity.getLocation(), AreaEffectCloud.class, a -> {
			a.setRadius(0);
			a.setParticle(Particle.TOWN_AURA);
			a.setWaitTime(0);
			a.setDuration(Integer.MAX_VALUE);
			a.setCustomNameVisible(true);

			a.getPersistentDataContainer().set(NpcKeys.NAME_TAG, PersistentDataType.BYTE, (byte)1);
		});
		entity.addPassenger(cloud);
		return cloud;
	}

	public boolean isNameTag(Entity entity) {
		return entity instanceof AreaEffectCloud && entity.getPersistentDataContainer().has(NpcKeys.NAME_TAG, PersistentDataType.BYTE);
	}
}
