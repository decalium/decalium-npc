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

import com.destroystokyo.paper.util.SneakyThrow;
import org.bukkit.Bukkit;
import org.gepron1x.npc.api.NonPlayerCharacter;
import org.gepron1x.npc.plugin.util.Futures;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.loader.ParsingException;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public final class FileNpcStorage implements NpcStorage {


	private final File file;
	private final ConfigurationOptions options;

	public FileNpcStorage(File file, ConfigurationOptions options) {

		this.file = file;
		this.options = options;
	}

	@Override
	public CompletableFuture<List<NonPlayerCharacter>> loadAll() {
		return Arrays.stream(Objects.requireNonNull(file.listFiles())).map(this::load).collect(Futures.allOf());
	}

	private CompletableFuture<NonPlayerCharacter> load(File file) {
		return CompletableFuture.supplyAsync(() -> {
			YamlConfigurationLoader loader = YamlConfigurationLoader.builder().file(file).build();
			try {
				return new NpcFile(loader.load(options)).construct();
			} catch (ParsingException | SerializationException e) {
				SneakyThrow.sneaky(e);
				return null;
			}
		});
	}

	@Override
	public CompletableFuture<NonPlayerCharacter> load(String identifier) {
		return load(new File(file, identifier+".yml"));
	}

	@Override
	public CompletableFuture<?> save(NonPlayerCharacter npc) {
		return CompletableFuture.runAsync(() -> {
			String filename = npc.identifier() + ".yml";
			File save = new File(file, filename);
			try {
				save.createNewFile();
				YamlConfigurationLoader loader = YamlConfigurationLoader.builder().file(save).build();
				ConfigurationNode node = loader.createNode(options);
				new NpcFile(node).save(npc);
				loader.save(node);
			} catch (IOException e) {
				SneakyThrow.sneaky(e);
			}
		});
	}
}
