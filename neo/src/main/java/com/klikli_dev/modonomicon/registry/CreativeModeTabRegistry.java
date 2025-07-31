/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.registry;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.data.BookDataManager;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenCustomHashSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CreativeModeTabRegistry {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Modonomicon.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MODONOMICON = CREATIVE_MODE_TABS.register(Modonomicon.MOD_ID, () -> CreativeModeTab.builder()
            .icon(() -> ItemRegistry.MODONOMICON_PURPLE.get().getDefaultInstance())
            .title(Component.translatable(ModonomiconConstants.I18n.ITEM_GROUP))
            .build());


    public static void onCreativeModeTabBuildContents(BuildCreativeModeTabContentsEvent event) {
        var tabName = net.neoforged.neoforge.common.CreativeModeTabRegistry.getName(event.getTab());
        if (tabName == null)
            return;

        var modonomiconTab = CreativeModeTabRegistry.MODONOMICON.get();

        BookDataManager.get().getBooks().values().forEach(b -> {
            var bookTab = BuiltInRegistries.CREATIVE_MODE_TAB.get(ResourceLocation.parse(b.getCreativeTab()));

            if (bookTab == event.getTab() || modonomiconTab == event.getTab()
            ) {
                if (b.generateBookItem()) {
                    ItemStack stack = new ItemStack(ItemRegistry.MODONOMICON.get());

                    stack.set(DataComponentRegistry.BOOK_ID.get(), b.getId());

                    if (!event.getParentEntries().contains(stack)) {
                        event.accept(stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    }
                }
            }
        });
    }

}
