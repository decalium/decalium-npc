package org.gepron1x.npc.plugin.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ru.xezard.glow.GlowAPI;
import ru.xezard.glow.data.glow.Glow;
import ru.xezard.glow.data.glow.manager.GlowsManager;

public final class GlowListener implements Listener {





    @EventHandler
    public void on(PlayerJoinEvent event) {
        GlowsManager.getInstance().getGlows().forEach(glow -> {
            glow.display(event.getPlayer());
        });
    }

}
