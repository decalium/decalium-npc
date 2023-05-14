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
package org.gepron1x.npc.plugin.util;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collector;
import java.util.ArrayList;

public final class Futures {

	public static <T> Collector<CompletableFuture<T>, ArrayList<CompletableFuture<T>>, CompletableFuture<List<T>>> allOf() {
		return Collector.of(ArrayList::new, ArrayList::add, (left, right) -> { left.addAll(right); return left; },
				list -> CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).thenApply(ignored -> {
			List<T> result = new ArrayList<>(list.size());
			for(CompletableFuture<T> future : list) result.add(future.join());
			return result;
		}));
	}
}
