package org.gepron1x.npc.plugin.tinyprotocol;

import net.minecraft.server.v1_16_R3.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_16_R3.Scoreboard;
import net.minecraft.server.v1_16_R3.ScoreboardTeam;
import net.minecraft.server.v1_16_R3.ScoreboardTeamBase;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class JoinListener implements Listener {

    private final TinyProtocol protocol;

    public JoinListener(TinyProtocol protocol) {

        this.protocol = protocol;
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        Scoreboard scoreboard = new Scoreboard();
        ScoreboardTeam team = new ScoreboardTeam(scoreboard, "npc_team");
        team.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.NEVER);
        protocol.sendPacket(event.getPlayer(), new PacketPlayOutScoreboardTeam(team, 0));
    }
}
