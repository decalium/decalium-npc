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
package org.gepron1x.npc.plugin.storage.serializers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Objects;

public class AdventureComponentSerializer implements TypeSerializer<Component> {

	private final ComponentSerializer<Component, ? extends Component, String> serializer;

	public AdventureComponentSerializer(ComponentSerializer<Component, ? extends Component, String> serializer) {

		this.serializer = serializer;
	}
	/**
	 * @param type the type of return value required 
	 * @param node the node containing serialized data
	 * @return
	 * @throws SerializationException
	 */
	@Override
	public Component deserialize(Type type, ConfigurationNode node) throws SerializationException {

		return serializer.deserialize(node.require(String.class));
	}

	/**
	 * @param type the type of the input object 
	 * @param obj  the object to be serialized
	 * @param node the node to write to
	 * @throws SerializationException
	 */
	@Override
	public void serialize(Type type, @Nullable Component obj, ConfigurationNode node) throws SerializationException {
		if(obj != null) node.set(serializer.serialize(obj));
	}
}
