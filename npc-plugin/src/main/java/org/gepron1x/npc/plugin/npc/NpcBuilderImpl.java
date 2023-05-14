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

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Husk;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.gepron1x.npc.api.ClickAction;
import org.gepron1x.npc.api.NonPlayerCharacter;
import org.gepron1x.npc.api.NpcBuilder;
import org.gepron1x.npc.api.event.NpcLoadedEvent;
import org.gepron1x.npc.plugin.NpcKeys;
import org.gepron1x.npc.plugin.storage.BinaryEntityData;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

public final class NpcBuilderImpl implements NpcBuilder {

	private static final ItemStack BUTTON = new ItemStack(Material.OAK_BUTTON);

	private final String identifier;
	private final NpcMap map;
	private final ConcurrentMap<Integer, NonPlayerCharacter> loadedNpcMap;
	private Component displayName = Component.empty();
	private Location location;
	private Set<ProfileProperty> properties = new HashSet<>();
	private Multimap<EquipmentSlot, ClickAction> actions = HashMultimap.create();

	private Consumer<LivingEntity> edit = e -> {};

	public NpcBuilderImpl(final String identifier, NpcMap map, ConcurrentMap<Integer, NonPlayerCharacter> loadedNpcMap) {
		this.identifier = identifier;
		this.map = map;
		this.loadedNpcMap = loadedNpcMap;
	}

	@Override
	public NpcBuilder displayName(Component displayName) {
		this.displayName = displayName;
		return this;
	}

	@Override
	public NpcBuilder location(Location location) {
		this.location = location;
		return this;
	}


	@Override
	public NpcBuilder profileProperties(Collection<ProfileProperty> properties) {
		this.properties.clear();
		this.properties.addAll(properties);
		return this;
	}

	@Override
	public NpcBuilder action(EquipmentSlot slot, ClickAction action) {
		actions.put(slot, action);
		return this;
	}
	@Override
	public NpcBuilder editEntity(Consumer<LivingEntity> consumer) {
		this.edit = consumer;
		return this;
	}


	@Override
	public NonPlayerCharacter spawn() {
		Objects.requireNonNull(location);
		NonPlayerCharacter[] hack = new NonPlayerCharacter[1];
		Zombie entity = location.getWorld().spawn(location, Husk.class, zombie -> {
			zombie.setCustomNameVisible(false);
			Objects.requireNonNull(zombie.getEquipment());
			zombie.setRemoveWhenFarAway(false);
			zombie.setSilent(true);
			zombie.getPersistentDataContainer().set(NpcKeys.IDENTIFIER, PersistentDataType.STRING, identifier);
			edit.accept(zombie);
			hack[0] = new NonPlayerCharacterImpl(identifier, zombie.getUniqueId(), BinaryEntityData.fromEntity(zombie), new HashSet<>(properties), this.displayName, actions);
			loadedNpcMap.put(zombie.getEntityId(), hack[0]);
		});
		map.add(hack[0]);
		new NpcLoadedEvent(hack[0], entity).callEvent();
		return hack[0];
	}
}
