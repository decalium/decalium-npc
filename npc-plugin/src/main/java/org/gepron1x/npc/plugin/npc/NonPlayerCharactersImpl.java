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

import org.gepron1x.npc.api.NonPlayerCharacter;
import org.gepron1x.npc.api.NonPlayerCharacters;
import org.gepron1x.npc.api.NpcBuilder;

import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

public final class NonPlayerCharactersImpl implements NonPlayerCharacters {

	private final ConcurrentMap<Integer, NonPlayerCharacter> loadedNpcMap;
	private final NpcMap map;

	public NonPlayerCharactersImpl(ConcurrentMap<Integer, NonPlayerCharacter> loadedNpcMap, NpcMap map) {

		this.loadedNpcMap = loadedNpcMap;
		this.map = map;
	}

	@Override
	public NpcBuilder create(String identifier) {
		return new NpcBuilderImpl(identifier, map, loadedNpcMap);
	}

	@Override
	public Optional<NonPlayerCharacter> npc(int entityId) {
		return Optional.ofNullable(loadedNpcMap.get(entityId));
	}
}
