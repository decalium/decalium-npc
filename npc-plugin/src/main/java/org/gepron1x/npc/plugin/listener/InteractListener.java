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
package org.gepron1x.npc.plugin.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.gepron1x.npc.api.NonPlayerCharacters;

public final class InteractListener implements Listener {

	private final NonPlayerCharacters nonPlayerCharacters;

	public InteractListener(NonPlayerCharacters nonPlayerCharacters) {

		this.nonPlayerCharacters = nonPlayerCharacters;
	}

	@EventHandler
	public void on(PlayerInteractAtEntityEvent event) {
		nonPlayerCharacters.npc(event.getRightClicked()).ifPresent(npc -> npc.on(event));
	}
}
