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

package com.github.lukaszkusek.xml.comparator.comparators.children;

import java.util.Collection;
import java.util.Set;

import com.github.lukaszkusek.xml.comparator.XMLComparator;
import com.github.lukaszkusek.xml.comparator.comparators.XMLComparatorStep;
import com.github.lukaszkusek.xml.comparator.comparators.children.cost.CostCalculator;
import com.github.lukaszkusek.xml.comparator.comparators.children.cost.CostMatrix;
import com.github.lukaszkusek.xml.comparator.comparators.children.cost.minimum.HungarianMinimumCostAssignmentCalculator;
import com.github.lukaszkusek.xml.comparator.comparators.children.cost.minimum.MinimumCostAssignmentCalculator;
import com.github.lukaszkusek.xml.comparator.diff.DifferenceDetails;
import com.github.lukaszkusek.xml.comparator.node.Node;
import com.google.common.collect.Sets;

public class XMLChildrenIgnoringOrderComparator implements XMLComparatorStep {

    private final CostCalculator costCalculator;
    private final MinimumCostAssignmentCalculator minimumCostAssignmentCalculator = new HungarianMinimumCostAssignmentCalculator();

    public XMLChildrenIgnoringOrderComparator(XMLComparator xmlComparator) {
        this.costCalculator = new CostCalculator(xmlComparator);
    }

    @Override
    public DifferenceDetails compare(Node node1, Node node2) {
        return getChildrenKeysToIterate(node1, node2).stream()
                .map(childrenXPath -> compareChildrenWithTheSameXpath(node1, node2, childrenXPath))
                .reduce(new DifferenceDetails(), DifferenceDetails::putAll);
    }

    private Set<String> getChildrenKeysToIterate(Node node1, Node node2) {
        return Sets.union(node1.getChildrenXPaths(), node2.getChildrenXPaths());
    }

    private DifferenceDetails compareChildrenWithTheSameXpath(Node node1, Node node2, String childrenXPath) {
        Collection<Node> children1 = node1.getChildren(childrenXPath);
        Collection<Node> children2 = node2.getChildren(childrenXPath);

        if (children1.isEmpty() && children2.isEmpty()) {
            return DifferenceDetails.empty();
        }

        CostMatrix childrenComparisonMatrix = costCalculator.compareChildren(children1, children2);
        childrenComparisonMatrix.findMinimumCostAssignment(minimumCostAssignmentCalculator);

        return childrenComparisonMatrix.getDifferenceDetails();
    }

}
