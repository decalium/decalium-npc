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
import com.google.common.collect.Multimap;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.gepron1x.npc.api.ClickAction;
import org.gepron1x.npc.api.EntityData;
import org.gepron1x.npc.api.NonPlayerCharacter;
import org.gepron1x.npc.api.event.NpcLoadedEvent;
import org.gepron1x.npc.plugin.NameTag;
import org.gepron1x.npc.plugin.storage.BinaryEntityData;

import java.util.*;

public final class NonPlayerCharacterImpl implements NonPlayerCharacter {

	private final String identifier;
	private UUID uniqueId;

	private final EntityData data;
	private final Set<ProfileProperty> properties;
	private final Component displayName;
	private final Multimap<EquipmentSlot, ClickAction> clickActions;

	public NonPlayerCharacterImpl(String identifier,
								  UUID uniqueId,
								  EntityData data,
								  Set<ProfileProperty> properties,
								  Component displayName,
								  Multimap<EquipmentSlot, ClickAction> clickActions) {
		this.identifier = identifier;
		this.uniqueId = uniqueId;
		this.data = data;
		this.properties = properties;
		this.displayName = displayName;
		this.clickActions = clickActions;
	}

	@Override
	public String identifier() {
		return this.identifier;
	}

	@Override
	public int entityId() {
		return realEntity().map(LivingEntity::getEntityId).orElseThrow(() -> new IllegalStateException("Entity not loaded"));
	}

	@Override
	public UUID uniqueId() {
		return uniqueId;
	}


	@Override
	public NonPlayerCharacter spawn(Location location) {
		Entity entity = data.deserialize(location.getWorld());
		new NpcLoadedEvent(this, entity).callEvent();
		this.uniqueId = entity.getUniqueId();
		entity.spawnAt(location);
		return this;
	}

	@Override
	public Set<ProfileProperty> profileProperties() {
		return properties;
	}

	@Override
	public Optional<LivingEntity> realEntity() {
		return Optional.ofNullable(Bukkit.getEntity(this.uniqueId)).map(LivingEntity.class::cast);
	}

	@Override
	public EntityData data() {
		return data;
	}


	@Override
	public void on(PlayerInteractAtEntityEvent event) {
		clickActions.get(event.getHand()).forEach(a -> a.accept(event));
	}

	@Override
	public Multimap<EquipmentSlot, ClickAction> interaction() {
		return this.clickActions;
	}

	@Override
	public Component displayName() {
		return this.displayName;
	}

	@Override
	public void displayName(Component displayName) {
		realEntity().ifPresent(e -> new NameTag(e).update(displayName));
	}
}
