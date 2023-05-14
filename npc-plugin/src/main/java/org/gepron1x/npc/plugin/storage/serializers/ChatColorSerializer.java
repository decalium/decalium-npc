package org.gepron1x.npc.plugin.storage.serializers;

import com.google.common.base.Enums;
import org.bukkit.ChatColor;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public final class ChatColorSerializer implements TypeSerializer<ChatColor> {
    /**
     * @param type the type of return value required 
     * @param node the node containing serialized data
     * @return
     * @throws SerializationException
     */
    @Override
    public ChatColor deserialize(Type type, ConfigurationNode node) throws SerializationException {
        return Enums.getIfPresent(ChatColor.class, node.require(String.class)).orNull();
    }

    /**
     * @param type the type of the input object 
     * @param obj  the object to be serialized
     * @param node the node to write to
     * @throws SerializationException
     */
    @Override
    public void serialize(Type type, @Nullable ChatColor obj, ConfigurationNode node) throws SerializationException {
        if(obj != null) node.set(obj.name());
    }
}
