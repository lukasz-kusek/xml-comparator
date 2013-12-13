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

package com.github.lukaszkusek.xml.comparator.comparators.order;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.lukaszkusek.xml.comparator.comparators.XMLComparatorStep;
import com.github.lukaszkusek.xml.comparator.diff.DifferenceCode;
import com.github.lukaszkusek.xml.comparator.diff.DifferenceDetails;
import com.github.lukaszkusek.xml.comparator.node.Node;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang.ObjectUtils;

public class XMLCheckChildrenOrderComparator implements XMLComparatorStep {

    @Override
    public DifferenceDetails compare(Node node1, Node node2) {
        List<String> xPaths1 = getSortedChildrenXPaths(node1);
        List<String> xPaths2 = getSortedChildrenXPaths(node2);

        Set<String> commonXPaths = intersection(xPaths1, xPaths2);

        List<String> commonXPaths1 = filter(xPaths1, commonXPaths);
        List<String> commonXPaths2 = filter(xPaths2, commonXPaths);

        List<XPathPair> xPaths = zip(commonXPaths1, commonXPaths2);

        return xPaths.stream()
                .map(xPathPair -> {
                    if (!ObjectUtils.equals(xPathPair.xPath1, xPathPair.xPath2)) {
                        return DifferenceDetails.of(
                                firstChildWithGivenXPath(node1, xPathPair.xPath1),
                                firstChildWithGivenXPath(node2, xPathPair.xPath2),
                                DifferenceCode.INCORRECT_ORDER);
                    }

                    return DifferenceDetails.empty();
                })
                .reduce(new DifferenceDetails(), DifferenceDetails::putAll);
    }

    private static class XpathWithIndex {
        private String xPath;
        private int index;

        private XpathWithIndex(String xPath, int index) {
            this.xPath = xPath;
            this.index = index;
        }
    }

    private List<String> getSortedChildrenXPaths(Node node) {
        return Maps.transformEntries(
                node.getChildren().asMap(),
                (xpath, nodes) -> new XpathWithIndex(xpath, first(nodes).getIndex()))
                .values()
                .stream()
                .sorted((xpath1, xpath2) -> Integer.compare(xpath1.index, xpath2.index))
                .map(xPathWithIndex -> xPathWithIndex.xPath)
                .collect(Collectors.toList());
    }

    private Node first(Collection<Node> nodes) {
        return FluentIterable.from(nodes).first().get();
    }

    private Sets.SetView<String> intersection(List<String> list1, List<String> list2) {
        return Sets.intersection(Sets.<String>newHashSet(list1), Sets.<String>newHashSet(list2));
    }

    private List<String> filter(List<String> xPaths, Set<String> commonXPaths) {
        return xPaths.stream().filter(commonXPaths::contains).collect(Collectors.toList());
    }

    private static class XPathPair {
        private String xPath1;
        private String xPath2;

        public XPathPair(String xPath1, String xPath2) {
            this.xPath1 = xPath1;
            this.xPath2 = xPath2;
        }
    }

    private List<XPathPair> zip(List<String> xPaths1, List<String> xPaths2) {
        Iterator<String> xPathIterator1 = xPaths1.iterator();
        Iterator<String> xPathIterator2 = xPaths2.iterator();
        List<XPathPair> zippedXPaths = Lists.newArrayList();

        while (xPathIterator1.hasNext() && xPathIterator2.hasNext()) {
            zippedXPaths.add(new XPathPair(xPathIterator1.next(), xPathIterator2.next()));
        }

        return zippedXPaths;
    }

    private Node firstChildWithGivenXPath(Node node1, String xPath) {
        return first(node1.getChildren().get(xPath));
    }
}
