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

package com.github.lukaszkusek.xml.comparator.comparators.children.cost;

import java.util.Collection;

import com.github.lukaszkusek.xml.comparator.XMLComparator;
import com.github.lukaszkusek.xml.comparator.diff.DifferenceCode;
import com.github.lukaszkusek.xml.comparator.diff.DifferenceDetails;
import com.github.lukaszkusek.xml.comparator.node.INode;
import com.github.lukaszkusek.xml.comparator.node.Node;
import com.google.common.collect.Lists;

public class CostCalculator {

    private XMLComparator xmlComparator;

    public CostCalculator(XMLComparator xmlComparator) {
        this.xmlComparator = xmlComparator;
    }

    public CostMatrix compareChildren(Collection<Node> children1, Collection<Node> children2) {
        int size1 = children1.size();
        int size2 = children2.size();

        int max = Math.max(size1, size2);

        Collection<INode> nullableChildren1 = fillUpWithNullNodes(children1, max - size1);
        Collection<INode> nullableChildren2 = fillUpWithNullNodes(children2, max - size2);

        return createChildrenComparisonMatrix(nullableChildren1, nullableChildren2);
    }

    private Collection<INode> fillUpWithNullNodes(Collection<Node> children, int childrenToAddCount) {
        Collection<INode> nullableChildren = Lists.newArrayList();
        nullableChildren.addAll(children);

        for (int i = 0; i < childrenToAddCount; i++) {
            nullableChildren.add(new NullNode());
        }

        return nullableChildren;
    }

    private CostMatrix createChildrenComparisonMatrix(
            Collection<INode> nullableChildren1, Collection<INode> nullableChildren2) {

        CostMatrix childrenComparisonMatrix = CostMatrix.create(nullableChildren1, nullableChildren2);

        nullableChildren1.forEach(child1 ->
                nullableChildren2.forEach(child2 ->
                        childrenComparisonMatrix.put(child1, child2, compare(child1, child2))));

        return childrenComparisonMatrix;
    }

    private DifferenceDetails compare(INode node1, INode node2) {
        if (node1.isNull() && node2.isNull()) {
            throw new RuntimeException("node1 && node2 cannot be NullNode at the same time.");
        }

        if (node1.isNull()) {
            return DifferenceDetails.of(null, (Node) node2, DifferenceCode.MISSING_ELEMENT_IN_FIRST_DOCUMENT);
        }

        if (node2.isNull()) {
            return DifferenceDetails.of((Node) node1, null, DifferenceCode.MISSING_ELEMENT_IN_SECOND_DOCUMENT);
        }

        return xmlComparator.compare((Node) node1, (Node) node2);
    }
}
