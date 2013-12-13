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

package com.github.lukaszkusek.xml.comparator.document;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Map;

import com.github.lukaszkusek.xml.comparator.node.Node;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.io.CharStreams;

public class XMLDocument {

    private Node rootNode;

    private XMLDocument(String xml, boolean ignoreNamespace) throws TransformerException, IOException {
        rootNode =
                findRootNode(
                        buildNodeTree(
                                createNodeMap(
                                        createNodeMultiMap(
                                                extractXPathLines(xml, ignoreNamespace)))));
    }

    private Collection<XPathLine> extractXPathLines(String xml, boolean ignoreNamespace)
            throws IOException, TransformerException {

        return CharStreams.readLines(
                new StringInputSupplier(
                        new StringReader(
                                XMLToXPathsTransformer.translate(xml))),
                new XPathLineProcessor(ignoreNamespace));
    }

    private Multimap<String, Node> createNodeMultiMap(Collection<XPathLine> xPathLines) {
        return Multimaps.transformValues(
                FluentIterable.from(xPathLines).index(XPathLine::getXPath),
                xPathLine -> {
                    Node node = new Node(xPathLine.getIndex(), xPathLine.getXPath());
                    node.setValue(xPathLine.getValue());
                    node.putAttribute(xPathLine.getAttributeName(), xPathLine.getAttributeValue());

                    return node;
                });
    }

    private Map<String, Node> createNodeMap(Multimap<String, Node> xpathToNodeMultimap) {
        return Maps.newHashMap(
                Maps.transformEntries(
                        xpathToNodeMultimap.asMap(),
                        (key, nodes) -> nodes.stream().reduce(Node::merge).get()));
    }

    private Collection<Node> buildNodeTree(Map<String, Node> xpathToNodeMap) {
        xpathToNodeMap.forEach(
                (xPath, child) -> {
                    String parentKey = extractParentXPath(xPath);

                    if (hasParent(parentKey)) {
                        Node parent = xpathToNodeMap.get(parentKey);

                        linkChildWithParent(parent, child, xPath);
                    }
                });

        return xpathToNodeMap.values();
    }

    private Node findRootNode(Collection<Node> nodes) {
        return FluentIterable.from(nodes).filter(isRoot()).first().get();
    }

    private Predicate<Node> isRoot() {
        return node -> node.getParent() == null;
    }

    private void linkChildWithParent(Node parent, Node child, String childKey) {
        child.setParent(parent);

        parent.addChild(childKey, child);
    }

    private boolean hasParent(String parentKey) {
        return !parentKey.isEmpty();
    }

    private String extractParentXPath(String xPath) {
        return xPath.replaceAll("/[^/]+$", "");
    }

    public static XMLDocument fromXML(String xml, boolean ignoreNamespace) throws TransformerException, IOException {
        return new XMLDocument(xml, ignoreNamespace);
    }

    public static XMLDocument fromXML(String xml) throws TransformerException, IOException {
        return new XMLDocument(xml, true);
    }

    public Node getRootNode() {
        return rootNode;
    }

}
