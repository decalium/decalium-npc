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
package org.gepron1x.npc.plugin;

import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.exceptions.parsing.ParserException;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.gepron1x.npc.api.NonPlayerCharacter;
import org.gepron1x.npc.plugin.npc.NpcMap;

import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public final class NpcParser implements ArgumentParser<CommandSender, NonPlayerCharacter> {

	private final NpcMap map;

	public NpcParser(NpcMap map) {
		this.map = map;
	}
	/**
	 * @param commandContext Command context 
	 * @param inputQueue     The queue of arguments
	 * @return
	 */
	@Override
	public @NonNull ArgumentParseResult<@NonNull NonPlayerCharacter> parse(@NonNull CommandContext<@NonNull CommandSender> commandContext, @NonNull Queue<@NonNull String> inputQueue) {
		String input = inputQueue.peek();
		if(input == null) return ArgumentParseResult.failure(new NoInputProvidedException(NpcParser.class, commandContext));
		inputQueue.remove();
		return map.get(input).map(ArgumentParseResult::success).orElseGet(() -> ArgumentParseResult.failure(new IllegalArgumentException("No npc found")));
	}

	/**
	 * @param commandContext Command context 
	 * @param input          Input string
	 * @return
	 */
	@Override
	public @NonNull List<@NonNull String> suggestions(@NonNull CommandContext<CommandSender> commandContext, @NonNull String input) {
		return map.values().stream().map(NonPlayerCharacter::identifier).collect(Collectors.toList());
	}
}
