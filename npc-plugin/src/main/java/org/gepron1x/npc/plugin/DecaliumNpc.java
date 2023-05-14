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

import cloud.commandframework.arguments.parser.ParserParameters;
import cloud.commandframework.arguments.standard.EnumArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.profile.ProfileProperty;
import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.gepron1x.npc.api.ClickAction;
import org.gepron1x.npc.api.EntityData;
import org.gepron1x.npc.api.NonPlayerCharacter;
import org.gepron1x.npc.api.NonPlayerCharacters;
import org.gepron1x.npc.api.event.NpcLoadedEvent;
import org.gepron1x.npc.plugin.listener.GlowListener;
import org.gepron1x.npc.plugin.listener.InteractListener;
import org.gepron1x.npc.plugin.listener.NameTagListener;
import org.gepron1x.npc.plugin.listener.NpcLoadListener;
import org.gepron1x.npc.plugin.npc.NonPlayerCharactersImpl;
import org.gepron1x.npc.plugin.npc.NpcMap;
import org.gepron1x.npc.plugin.storage.FileNpcStorage;
import org.gepron1x.npc.plugin.storage.NpcStorage;
import org.gepron1x.npc.plugin.storage.serializers.*;
import org.gepron1x.npc.plugin.task.RotationTask;
import org.gepron1x.npc.plugin.tinyprotocol.JoinListener;
import org.gepron1x.npc.plugin.tinyprotocol.NpcListener;
import org.spongepowered.configurate.ConfigurationOptions;
import ru.xezard.glow.GlowAPI;
import ru.xezard.glow.data.glow.Glow;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.UnaryOperator;

public final class DecaliumNpc extends JavaPlugin {


	@Override
	public void onDisable() {

	}

	@Override
	public void onLoad() {

	}

	@Override
	public void onEnable() {
		new GlowAPI(this);
		PaperCommandManager<CommandSender> manager;
		try {
			manager = new PaperCommandManager<>(
					this,
					CommandExecutionCoordinator.simpleCoordinator(),
					UnaryOperator.identity(), UnaryOperator.identity()
			);
		} catch (Exception e) {
			getSLF4JLogger().error("Failed initializing command manager!", e);
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		ConcurrentMap<Integer, NonPlayerCharacter> loaded = new ConcurrentHashMap<>();
		NpcMap map = new NpcMap(new ConcurrentHashMap<>());
		NonPlayerCharacters nonPlayerCharacters = new NonPlayerCharactersImpl(loaded, map);

		File storageFile = new File(getDataFolder(), "npcs");
		storageFile.mkdirs();
		NpcStorage storage = new FileNpcStorage(storageFile, ConfigurationOptions.defaults().serializers(builder -> {
			builder.register(Component.class, new AdventureComponentSerializer(MiniMessage.miniMessage()))
					.register(ProfileProperty.class, new ProfilePropertySerializer())
					.register(ClickAction.class, new ClickActionSerializer())
					.register(EntityData.class, new EntityDataSerializer())
					.register(ChatColor.class, new ChatColorSerializer());
		}));
		storage.loadAll().exceptionally(e -> {
			getSLF4JLogger().error("Error while loading npcs!", e);
			return Collections.emptyList();
		}).join().forEach(map::add);
		NpcLoadListener loadListener = new NpcLoadListener(map, loaded);
		getServer().getPluginManager().registerEvents(loadListener, this);
		NpcListener listener = new NpcListener(this, new Packets1_16(), nonPlayerCharacters);
		getServer().getPluginManager().registerEvents(new JoinListener(listener), this);
		getServer().getPluginManager().registerEvents(new InteractListener(nonPlayerCharacters), this);
		getServer().getPluginManager().registerEvents(new NameTagListener(map), this);
		getServer().getPluginManager().registerEvents(new GlowListener(), this);
		manager.parserRegistry().registerParserSupplier(TypeToken.get(NonPlayerCharacter.class), (params) -> new NpcParser(map));

		new NpcCommand(map, storage).register(manager);
		manager.command(manager.commandBuilder("npc").literal("create").permission("npc.create").argument(StringArgument.of("identifier"))
				.flag(manager.flagBuilder("ai")).flag(manager.flagBuilder("can-die")).flag(manager.flagBuilder("glow").withArgument(EnumArgument.of(ChatColor.class, "glow")))
				.senderType(Player.class).handler(ctx -> {
					Player player = (Player) ctx.getSender();
					NonPlayerCharacter npc = nonPlayerCharacters.create(ctx.get("identifier"))
							.displayName(player.displayName())
							.location(player.getLocation())
							.profileProperties(player.getPlayerProfile().getProperties())
							.action(EquipmentSlot.HAND, e -> e.getPlayer().sendMessage(Component.text("Hi")))
							.editEntity(entity -> {
								entity.setAI(ctx.flags().hasFlag("ai"));
								entity.setInvulnerable(!ctx.flags().hasFlag("can-die"));
							})
							.spawn();
					npc.realEntity().ifPresent(entity -> {
						ctx.flags().<ChatColor>getValue("glow").ifPresent(c -> {
							Glow glow = Glow.builder().color(c).name(NonPlayerCharacter.formatPlayerName(ctx.get("identifier"))).build();
							glow.addHolders(entity);
							glow.display(getServer().getOnlinePlayers());
						});
					});
					storage.save(npc).exceptionally(e -> {
						getSLF4JLogger().error("Error while saving npc:", e);
						return null;
					});
				}));

		manager.command(manager.commandBuilder("npc").literal("reload").permission("npc.admin.reload").handler(ctx -> {
			map.clear();
			loaded.clear();
			storage.loadAll().exceptionally(e -> {
				getSLF4JLogger().error("Error while loading npcs!", e);
				return Collections.emptyList();
			}).join().forEach(npc -> {
				map.add(npc);
				npc.realEntity().ifPresent(entity -> new NpcLoadedEvent(npc, entity).callEvent());
			});
		}));

		getServer().getScheduler().runTaskTimer(this, new RotationTask(loaded, listener), 20, 4);
	}

	public static NamespacedKey createKey(String value) {
		return new NamespacedKey(getPlugin(DecaliumNpc.class), value);
	}
}
