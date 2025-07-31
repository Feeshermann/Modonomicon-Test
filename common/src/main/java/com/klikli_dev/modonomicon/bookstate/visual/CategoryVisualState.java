/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.bookstate.visual;

import com.klikli_dev.modonomicon.util.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Optional;

public class CategoryVisualState {

    public static final Codec<CategoryVisualState> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.unboundedMap(ResourceLocation.CODEC, EntryVisualState.CODEC).fieldOf("entryStates").forGetter((state) -> state.entryStates),
            Codec.FLOAT.fieldOf("scrollX").forGetter((state) -> state.scrollX),
            Codec.FLOAT.fieldOf("scrollY").forGetter((state) -> state.scrollY),
            Codec.FLOAT.fieldOf("targetZoom").forGetter((state) -> state.targetZoom),
            ResourceLocation.CODEC.optionalFieldOf("openEntry").forGetter((state) -> Optional.ofNullable(state.openEntry)),
            Codec.INT.fieldOf("openPagesIndex").forGetter((state) -> state.openPagesIndex)
    ).apply(instance, CategoryVisualState::new));

    public Map<ResourceLocation, EntryVisualState> entryStates;

    public float scrollX;
    public float scrollY;
    public float targetZoom;

    public ResourceLocation openEntry;

    /**
     * For categories in index mode
     */
    public int openPagesIndex;

    public CategoryVisualState() {
        this(Object2ObjectMaps.emptyMap(), 0, 0, 0.7f, Optional.empty(), 0);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public CategoryVisualState(Map<ResourceLocation, EntryVisualState> entryStates, float scrollX, float scrollY, float targetZoom, Optional<ResourceLocation> openEntry, int openPagesIndex) {
        this.entryStates = new Object2ObjectOpenHashMap<>(entryStates);
        this.scrollX = scrollX;
        this.scrollY = scrollY;
        this.targetZoom = targetZoom;
        this.openEntry = openEntry.orElse(null);
    }
}
