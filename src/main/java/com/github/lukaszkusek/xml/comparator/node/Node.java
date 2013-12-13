/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Lukasz Kusek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.github.lukaszkusek.xml.comparator.node;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class Node implements INode {

    private int index;
    private String xPath;
    private String simpleXPath;
    private String name;
    private Node parent;
    private String value;
    private Map<String, String> attributes;
    private Multimap<String, Node> children;

    public Node(int index, String xPath) {
        Preconditions.checkArgument(xPath != null, "Xpath cannot be null.");

        this.index = index;
        this.xPath = xPath;
        this.simpleXPath = convertToSimpleXPath(xPath);
        this.name = extractName(this.xPath);
        this.attributes = Maps.newHashMap();
        this.children = HashMultimap.create();
    }

    protected Node(Node node) {
        this.index = node.index;
        this.xPath = node.xPath;
        this.simpleXPath = node.simpleXPath;
        this.name = node.name;
        this.attributes = node.attributes;
        this.children = node.children;
    }

    private String extractName(String xpath) {
        return FluentIterable.from(Splitter.on("/").split(xpath)).last().get();
    }

    private static String convertToSimpleXPath(String xpath) {
        return xpath.replaceAll("\\[[0-9]+\\]", "");
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void putAttribute(String key, String value) {
        if (key != null) {
            attributes.put(key, normalizeString(value));
        }
    }

    private static String normalizeString(String value) {
        return Optional.ofNullable(value)
                .map(string -> string.trim().replaceAll("\\s+", " "))
                .orElse(null);
    }

    public void addChild(String xpath, Node child) {
        children.put(convertToSimpleXPath(xpath), child);
    }

    public int getIndex() {
        return index;
    }

    public String getXPath() {
        return xPath;
    }

    public String getSimpleXPath() {
        return simpleXPath;
    }

    public String getName() {
        return name;
    }

    public Node getParent() {
        return parent;
    }

    public String getValue() {
        return value;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public Multimap<String, Node> getChildren() {
        return children;
    }

    boolean hasValue() {
        return getValue() != null;
    }

    public Node merge(Node node) {
        Preconditions.checkArgument(getXPath().equals(node.getXPath()), "Cannot merge Nodes with different XPaths.");

        if (node.hasValue()) {
            setValue(node.getValue());
        }

        getAttributes().putAll(node.getAttributes());

        return this;
    }

    public String extractValue(Pattern pattern) {
        String nodeValue = getValue();

        if (pattern != null) {
            Matcher matcher = pattern.matcher(nodeValue);
            if (matcher.find()) {
                nodeValue = matcher.group(1);
            }
        }
        return nodeValue;
    }

    public String getAttribute(String attributeName) {
        return getAttributes().get(attributeName);
    }

    public String extractAttributeValue(String attributeName, Pattern pattern) {
        String attributeValue = getAttribute(attributeName);

        if (pattern != null) {
            Matcher matcher = pattern.matcher(attributeValue);
            if (matcher.find()) {
                attributeValue = matcher.group(1);
            }
        }
        return attributeValue;
    }

    public Set<String> getAttributesNames() {
        return getAttributes().keySet();
    }

    public Set<String> getChildrenXPaths() {
        return getChildren().keySet();
    }

    public Collection<Node> getChildren(String childrenXPath) {
        return getChildren().get(childrenXPath);
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Node node = (Node) o;

        return xPath.equals(node.xPath);
    }

    @Override
    public int hashCode() {
        return xPath.hashCode();
    }
}
