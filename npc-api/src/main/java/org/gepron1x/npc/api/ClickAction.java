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
package org.gepron1x.npc.api;

import com.google.common.base.MoreObjects;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

public interface ClickAction extends Consumer<PlayerInteractAtEntityEvent> {

	enum Executor {
		PLAYER, CONSOLE;
		private final String prefix = "[" + this.name().toLowerCase(Locale.ROOT) + "]";

	}



	static ClickAction command(String command, Executor executor) {
		return new CommandAction(command, executor);
	}

	static ClickAction command(String command) {
		return command(command, Executor.PLAYER);
	}

	static ClickAction console(String command) {
		return command(command, Executor.CONSOLE);
	}

	static ClickAction parse(String string) {
		for(Executor executor : Executor.values()) {
			if(string.startsWith(executor.prefix)) return ClickAction.command(string.substring(executor.prefix.length()).trim());
		}
		return ClickAction.command(string);
	}

	final class CommandAction implements ClickAction {

		private final String command;
		private final Executor executor;

		CommandAction(final String command, final Executor executor) {

			this.command = command;
			this.executor = executor;
		}


		@Override
		public void accept(PlayerInteractAtEntityEvent event) {
			Player player = event.getPlayer();
			if(executor == Executor.PLAYER) Bukkit.dispatchCommand(player, command);
			else Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			CommandAction that = (CommandAction) o;
			return command.equals(that.command) && executor == that.executor;
		}

		@Override
		public int hashCode() {
			return Objects.hash(command, executor);
		}

		@Override
		public String toString() {
			return executor.prefix + " " + command;
		}
	}
}
