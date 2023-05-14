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
package org.gepron1x.npc.plugin.listener;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.entity.Entity;
import org.gepron1x.npc.plugin.NameTag;
import org.gepron1x.npc.plugin.npc.NpcMap;
import org.spigotmc.event.entity.EntityDismountEvent;

public final class NameTagListener implements Listener {

	private final NpcMap npcMap;

	public NameTagListener(NpcMap npcMap) {

		this.npcMap = npcMap;
	}

	@EventHandler
	public void on(EntityDismountEvent event) {
		npcMap.get(event.getEntity()).ifPresent(npc -> {
			if(new NameTag(event.getEntity()).isNameTag(event.getDismounted())) {
				event.setCancelled(true);
			}
		});
	}

	@EventHandler
	public void on(EntityDeathEvent event) {
		npcMap.get(event.getEntity()).ifPresent(npc -> {
			NameTag tag = new NameTag(event.getEntity());
			event.getEntity().getPassengers().stream().filter(tag::isNameTag).forEach(Entity::remove);
		});
	}

	@EventHandler
	public void on(EntityAddToWorldEvent event) {
	}
}
