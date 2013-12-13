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

package com.github.lukaszkusek.xml.comparator.comparators.attributes;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.github.lukaszkusek.xml.comparator.comparators.XMLComparatorStep;
import com.github.lukaszkusek.xml.comparator.diff.DifferenceCode;
import com.github.lukaszkusek.xml.comparator.diff.DifferenceDetails;
import com.github.lukaszkusek.xml.comparator.node.Node;
import com.google.common.collect.Sets;
import org.apache.commons.lang.ObjectUtils;

public class XMLAttributesComparator implements XMLComparatorStep {

    private Map<String, Pattern> valueExtractors;

    public XMLAttributesComparator(Map<String, Pattern> valueExtractors) {
        this.valueExtractors = valueExtractors;
    }

    @Override
    public DifferenceDetails compare(Node node1, Node node2) {
        return getAttributesNamesToIterate(node1, node2).stream()
                .map(attributeName -> compareAttributesWithTheSameName(node1, node2, attributeName))
                .reduce(new DifferenceDetails(), DifferenceDetails::putAll);
    }

    private DifferenceDetails compareAttributesWithTheSameName(Node node1, Node node2, String attributeName) {
        Pattern pattern = valueExtractors.get(node1.getXPath() + "/@" + attributeName);

        String attribute1Value = node1.extractAttributeValue(attributeName, pattern);
        String attribute2Value = node2.extractAttributeValue(attributeName, pattern);

        if (!ObjectUtils.equals(attribute1Value, attribute2Value)) {
            return DifferenceDetails.of(
                    node1,
                    node2,
                    attributeName,
                    DifferenceCode.DIFFERENT_ATTRIBUTE_VALUE);
        }

        return DifferenceDetails.empty();
    }

    private Set<String> getAttributesNamesToIterate(Node node1, Node node2) {
        return Sets.union(node1.getAttributesNames(), node2.getAttributesNames());
    }

}
