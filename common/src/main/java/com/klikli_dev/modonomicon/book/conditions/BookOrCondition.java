/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.conditions;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Condition;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BookOrCondition extends BookCondition {

    protected BookCondition[] children;

    protected List<Component> tooltips;

    public BookOrCondition(Component component, BookCondition[] children) {
        super(component);
        if (children == null || children.length == 0)
            throw new IllegalArgumentException("OrCondition must have at least one child.");

        this.children = children;
    }

    public static BookOrCondition fromJson(ResourceLocation conditionParentId, JsonObject json, HolderLookup.Provider provider) {
        var children = new ArrayList<BookCondition>();
        for (var j : GsonHelper.getAsJsonArray(json, "children")) {
            if (!j.isJsonObject())
                throw new JsonSyntaxException("Condition children must be an array of JsonObjects.");
            children.add(BookCondition.fromJson(conditionParentId, j.getAsJsonObject(), provider));
        }
        var tooltip = tooltipFromJson(json, provider);
        return new BookOrCondition(tooltip, children.toArray(new BookCondition[children.size()]));
    }

    public static BookOrCondition fromNetwork(RegistryFriendlyByteBuf buffer) {
        var tooltip = buffer.readBoolean() ? ComponentSerialization.STREAM_CODEC.decode(buffer) : null;
        var childCount = buffer.readVarInt();
        var children = new BookCondition[childCount];
        for (var i = 0; i < childCount; i++) {
            children[i] = BookCondition.fromNetwork(buffer);
        }
        return new BookOrCondition(tooltip, children);
    }

    @Override
    public ResourceLocation getType() {
        return Condition.OR;
    }

    @Override
    public boolean requiresMultiPassUnlockTest() {
        return Arrays.stream(this.children).anyMatch(BookCondition::requiresMultiPassUnlockTest);
    }

    public BookCondition[] children() {
        return this.children;
    }

    @Override
    public void toNetwork(RegistryFriendlyByteBuf buffer) {
        buffer.writeBoolean(this.tooltip != null);
        if (this.tooltip != null) {
            ComponentSerialization.STREAM_CODEC.encode(buffer, this.tooltip);
        }
        buffer.writeVarInt(this.children.length);
        for (var child : this.children) {
            BookCondition.toNetwork(child, buffer);
        }
    }

    @Override
    public boolean test(BookConditionContext context, Player player) {
        for (var child : this.children) {
            if (child.test(context, player))
                return true;
        }
        return false;
    }

    @Override
    public boolean testOnLoad() {
        for (var child : this.children) {
            if (child.testOnLoad())
                return true;
        }
        return false;
    }

    @Override
    public List<Component> getTooltip(Player player, BookConditionContext context) {
        if (this.tooltips == null) {
            this.tooltips = new ArrayList<>();
        }

        this.tooltips.clear(); //should not cache because e.g. advancement condition tooltips can change
        if (this.tooltip != null)
            this.tooltips.add(this.tooltip);
        for (var child : this.children) {
            this.tooltips.addAll(child.getTooltip(player, context));
        }

        return this.tooltips;
    }
}
