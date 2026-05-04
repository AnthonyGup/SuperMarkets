package com.supermarkets.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DotGenerator {
    private final String treeName;
    private final String treeTitle;
    private final StringBuilder dotContent;
    private int nodeCount;

    public DotGenerator(String name, String title) {
        this.treeName = sanitizeName(name);
        this.treeTitle = title;
        this.dotContent = new StringBuilder();
        this.nodeCount = 0;
        initializeDot();
    }

    public void addNode(String nodeId, String label, String shape, String color) {
        dotContent.append("    node_").append(nodeId)
                .append(" [label=\"").append(escapeLabel(label))
                .append("\", shape=").append(shape)
                .append(", style=filled, fillcolor=").append(color)
                .append("];\n");
        nodeCount++;
    }

    public void addEdge(String fromNode, String toNode, String label) {
        dotContent.append("    node_").append(fromNode)
                .append(" -> node_").append(toNode);
        if (label != null && !label.isEmpty()) {
            dotContent.append(" [label=\"").append(escapeLabel(label)).append("\"]");
        }
        dotContent.append(";\n");
    }

    public void addCustom(String custom) {
        dotContent.append(custom);
    }

    public String toDotString() {
        dotContent.append("}\n");
        return dotContent.toString();
    }

    public int getNodeCount() {
        return nodeCount;
    }

    private void initializeDot() {
        dotContent.append("digraph ").append(treeName).append(" {\n");
        dotContent.append("    rankdir=TB;\n");
        dotContent.append("    bgcolor=white;\n");
        dotContent.append("    node [fontname=\"Arial\", fontsize=10, shape=box];\n");
        dotContent.append("    edge [fontname=\"Arial\", fontsize=9];\n");
        dotContent.append("    label=\"").append(escapeLabel(treeTitle)).append("\";\n");
        dotContent.append("    labelloc=top;\n\n");
    }

    private String escapeLabel(String label) {
        if (label == null) return "";
        return label.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n");
    }

    private String sanitizeName(String name) {
        if (name == null) return "tree";
        return name.replaceAll("[^a-zA-Z0-9_]", "_");
    }

    public static class Builder {
        private final String treeName;
        private String title = "";
        private Map<String, String> globalNodeAttrs = new HashMap<>();

        public Builder(String name) {
            this.treeName = sanitizeName(name);
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder nodeAttr(String key, String value) {
            this.globalNodeAttrs.put(key, value);
            return this;
        }

        public DotGenerator build() {
            return new DotGenerator(treeName, title);
        }

        private static String sanitizeName(String name) {
            if (name == null) return "tree";
            return name.replaceAll("[^a-zA-Z0-9_]", "_");
        }
    }
}
