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
package org.gepron1x.npc.plugin.storage;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.gepron1x.npc.api.EntityData;

import java.util.Base64;

public final class BinaryEntityData implements EntityData {

	private final byte[] bytes;

	public BinaryEntityData(byte[] bytes) {

		this.bytes = bytes;
	}

	@SuppressWarnings("deprecation")
	public Entity deserialize(World world) {
		return Bukkit.getUnsafe().deserializeEntity(bytes, world);
	}

	public byte[] data() {
		return this.bytes;
	}
	@Override
	public String base64() {
		return Base64.getEncoder().encodeToString(bytes);
	}

	public static BinaryEntityData fromBase64(String s) {
		return new BinaryEntityData(Base64.getDecoder().decode(s));
	}

	@SuppressWarnings("deprecation")
	public static BinaryEntityData fromEntity(Entity entity) {
		return new BinaryEntityData(Bukkit.getUnsafe().serializeEntity(entity));
	}
}
