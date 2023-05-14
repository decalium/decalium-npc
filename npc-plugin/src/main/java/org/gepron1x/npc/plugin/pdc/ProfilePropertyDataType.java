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
package org.gepron1x.npc.plugin.pdc;

import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.gepron1x.npc.plugin.DecaliumNpc;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class ProfilePropertyDataType implements PersistentDataType<PersistentDataContainer, ProfileProperty> {

	private static final NamespacedKey KEY = DecaliumNpc.createKey("key");
	private static final NamespacedKey VALUE = DecaliumNpc.createKey("value");
	private static final NamespacedKey SIGNATURE = DecaliumNpc.createKey("signature");
	@Override
	@NotNull
	public Class<PersistentDataContainer> getPrimitiveType() {
		return PersistentDataContainer.class;
	}

	@Override
	public @NotNull Class<ProfileProperty> getComplexType() {
		return ProfileProperty.class;
	}

	@Override
	public @NotNull PersistentDataContainer toPrimitive(@NotNull ProfileProperty profileProperty, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
		PersistentDataContainer container = persistentDataAdapterContext.newPersistentDataContainer();
		container.set(KEY, PersistentDataType.STRING, profileProperty.getName());
		container.set(VALUE, PersistentDataType.STRING, profileProperty.getValue());
		String sig = profileProperty.getSignature();
		if(sig != null) container.set(SIGNATURE, PersistentDataType.STRING, sig);
		return container;
	}

	@Override
	public @NotNull ProfileProperty fromPrimitive(@NotNull PersistentDataContainer persistentDataContainer, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
		String name = Objects.requireNonNull(persistentDataContainer.get(KEY, PersistentDataType.STRING), "name");
		String value = Objects.requireNonNull(persistentDataContainer.get(VALUE, PersistentDataType.STRING), "value");
		String sig = persistentDataContainer.get(SIGNATURE, PersistentDataType.STRING);
		return new ProfileProperty(name, value, sig);
	}
}
