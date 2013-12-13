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

package com.github.lukaszkusek.xml.comparator.comparators.values;

import java.util.Map;
import java.util.regex.Pattern;

import com.github.lukaszkusek.xml.comparator.comparators.XMLComparatorStep;
import com.github.lukaszkusek.xml.comparator.diff.DifferenceCode;
import com.github.lukaszkusek.xml.comparator.diff.DifferenceDetails;
import com.github.lukaszkusek.xml.comparator.node.Node;
import org.apache.commons.lang.ObjectUtils;

public class XMLValuesComparator implements XMLComparatorStep {

    private Map<String, Pattern> valueExtractors;

    public XMLValuesComparator(Map<String, Pattern> valueExtractors) {
        this.valueExtractors = valueExtractors;
    }

    @Override
    public DifferenceDetails compare(Node node1, Node node2) {
        Pattern pattern = valueExtractors.get(node1.getXPath());

        String node1Value = node1.extractValue(pattern);
        String node2Value = node2.extractValue(pattern);

        if (!ObjectUtils.equals(node1Value, node2Value)) {
            return DifferenceDetails.of(
                    node1,
                    node2,
                    DifferenceCode.DIFFERENT_VALUE);
        }

        return DifferenceDetails.empty();
    }

}
