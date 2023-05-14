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
package org.gepron1x.npc.plugin.npc;

import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.gepron1x.npc.api.NonPlayerCharacter;
import org.gepron1x.npc.api.NonPlayerCharacters;
import org.gepron1x.npc.plugin.NpcKeys;

import java.util.*;

public final class NpcMap {

	private final Map<String, NonPlayerCharacter> map;

	public NpcMap(Map<String, NonPlayerCharacter> map) {

		this.map = map;
	}

	public Optional<NonPlayerCharacter> get(String name) {
		return Optional.ofNullable(this.map.get(name));
	}

	public Optional<NonPlayerCharacter> get(PersistentDataHolder holder) {
		return Optional.ofNullable(holder.getPersistentDataContainer().get(NpcKeys.IDENTIFIER, PersistentDataType.STRING)).flatMap(this::get);
	}

	public Collection<NonPlayerCharacter> values() {
		return Collections.unmodifiableCollection(map.values());
	}

	public void add(NonPlayerCharacter npc) {
		this.map.put(npc.identifier(), npc);
	}

	public void remove(String id) {
		this.map.remove(id);
	}

	public void clear() {
		this.map.clear();
	}
}
