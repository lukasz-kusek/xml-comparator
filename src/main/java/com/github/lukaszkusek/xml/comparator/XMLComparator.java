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

package com.github.lukaszkusek.xml.comparator;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

import com.github.lukaszkusek.xml.comparator.comparators.XMLComparatorStep;
import com.github.lukaszkusek.xml.comparator.diff.DifferenceCode;
import com.github.lukaszkusek.xml.comparator.diff.DifferenceDetails;
import com.github.lukaszkusek.xml.comparator.diff.DifferenceInformation;
import com.github.lukaszkusek.xml.comparator.diff.XMLDiff;
import com.github.lukaszkusek.xml.comparator.document.XMLDocument;
import com.github.lukaszkusek.xml.comparator.node.Node;
import org.apache.commons.lang.ObjectUtils;

public class XMLComparator {

    private XMLDocument xmlDocument1;
    private XMLDocument xmlDocument2;

    private Predicate<DifferenceInformation> xPathsToOmitPredicate;

    private List<XMLComparatorStep> xmlComparatorSteps;

    XMLComparator() {
    }

    public static XMLComparatorBuilder builder() {
        return new XMLComparatorBuilder();
    }

    public static XMLDiff compare(String xml1, String xml2) throws TransformerException, IOException {
        return builder().first(xml1).second(xml2).compare();
    }

    public static XMLDiff compare(XMLDocument xmlDocument1, XMLDocument xmlDocument2) throws TransformerException, IOException {
        return builder().first(xmlDocument1).second(xmlDocument2).compare();
    }

    XMLDiff compare() throws TransformerException, IOException {
        Node rootNode1 = xmlDocument1.getRootNode();
        Node rootNode2 = xmlDocument2.getRootNode();

        DifferenceDetails differenceDetails;

        if (ObjectUtils.equals(rootNode1.getXPath(), rootNode2.getXPath())) {
            differenceDetails = compare(rootNode1, rootNode2);
        } else {
            differenceDetails = DifferenceDetails.of(rootNode1, rootNode2, DifferenceCode.DIFFERENT_ROOT_NODE);
        }

        return new XMLDiff(xmlDocument1, xmlDocument2, differenceDetails.filter(xPathsToOmitPredicate));
    }

    public DifferenceDetails compare(Node node1, Node node2) {
        return xmlComparatorSteps.stream()
                .map(comparator -> comparator.compare(node1, node2))
                .reduce(new DifferenceDetails(), DifferenceDetails::putAll);
    }

    void setXmlDocument1(XMLDocument xmlDocument1) {
        this.xmlDocument1 = xmlDocument1;
    }

    void setXmlDocument2(XMLDocument xmlDocument2) {
        this.xmlDocument2 = xmlDocument2;
    }

    void setXPathsToOmitPredicate(Predicate<DifferenceInformation> xPathsToOmitPredicate) {
        this.xPathsToOmitPredicate = xPathsToOmitPredicate;
    }

    void setXmlComparatorSteps(List<XMLComparatorStep> xmlComparatorSteps) {
        this.xmlComparatorSteps = xmlComparatorSteps;
    }
}
