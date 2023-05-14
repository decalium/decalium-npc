package org.gepron1x.npc.api.event;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.gepron1x.npc.api.NonPlayerCharacter;
import org.jetbrains.annotations.NotNull;

public final class NpcLoadedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final NonPlayerCharacter npc;
    private final Entity entity;

    public NpcLoadedEvent(NonPlayerCharacter npc, Entity entity) {

        this.npc = npc;
        this.entity = entity;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public NonPlayerCharacter npc() {
        return npc;
    }

    public Entity entity() { return entity; }
}
