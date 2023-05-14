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
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.destroystokyo.paper.profile.CraftPlayerProfile;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.gepron1x.npc.api.NonPlayerCharacter;
import org.gepron1x.npc.api.event.NpcLoadedEvent;
import org.gepron1x.npc.plugin.NameTag;
import org.gepron1x.npc.plugin.NpcKeys;
import org.gepron1x.npc.plugin.npc.NpcMap;
import ru.xezard.glow.data.glow.manager.GlowsManager;

import java.util.concurrent.ConcurrentMap;

public class NpcLoadListener implements Listener {


	private final NpcMap npcMap;
	private final ConcurrentMap<Integer, NonPlayerCharacter> loadedNpcs;

	public NpcLoadListener(NpcMap npcMap, ConcurrentMap<Integer, NonPlayerCharacter> loadedNpcs) {

		this.npcMap = npcMap;
		this.loadedNpcs = loadedNpcs;
	}

	@EventHandler
	public void on(EntityAddToWorldEvent event) {
		if(!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity entity = (LivingEntity) event.getEntity();
		PersistentDataContainer container = entity.getPersistentDataContainer();
		String identifier = container.get(NpcKeys.IDENTIFIER, PersistentDataType.STRING);
		if(identifier == null) return;
		npcMap.get(identifier).ifPresent(npc -> {
			new NpcLoadedEvent(npc, entity).callEvent();
		});
	}

	@EventHandler
	public void on(NpcLoadedEvent event) {
		loadedNpcs.put(event.entity().getEntityId(), event.npc());
		new NameTag(event.entity()).update(event.npc().displayName());
		GlowsManager.getInstance().getGlowByName(event.npc().playerName()).ifPresent(glow -> glow.addHolders(event.entity()));
	}


	@EventHandler
	public void on(EntityRemoveFromWorldEvent event) {
		loadedNpcs.remove(event.getEntity().getEntityId());
	}
}
