/*
 * SPDX-FileCopyrightText: 2015, Atlassian Pty Ltd
 *
 * SPDX-License-Identifier: BSD-2-Clause
 */

package com.klikli_dev.modonomicon.client.gui.book.markdown.internal.renderer;

import org.commonmark.node.Node;
import org.commonmark.renderer.NodeRenderer;

import java.util.HashMap;
import java.util.Map;

public class NodeRendererMap {

    private final Map<Class<? extends Node>, NodeRenderer> renderers = new HashMap<>(32);

    public void add(NodeRenderer nodeRenderer) {
        for (Class<? extends Node> nodeType : nodeRenderer.getNodeTypes()) {
            // Overwrite existing renderer
            this.renderers.put(nodeType, nodeRenderer);
        }
    }

    public void render(Node node) {
        NodeRenderer nodeRenderer = this.renderers.get(node.getClass());
        if (nodeRenderer != null) {
            nodeRenderer.render(node);
        }
    }
}
