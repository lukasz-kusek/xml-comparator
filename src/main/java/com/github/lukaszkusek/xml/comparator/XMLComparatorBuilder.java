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
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import com.github.lukaszkusek.xml.comparator.comparators.XMLComparatorStep;
import com.github.lukaszkusek.xml.comparator.comparators.attributes.XMLAttributesComparator;
import com.github.lukaszkusek.xml.comparator.comparators.children.XMLChildrenIgnoringOrderComparator;
import com.github.lukaszkusek.xml.comparator.comparators.order.XMLCheckChildrenOrderComparator;
import com.github.lukaszkusek.xml.comparator.comparators.values.XMLValuesComparator;
import com.github.lukaszkusek.xml.comparator.diff.DifferenceInformation;
import com.github.lukaszkusek.xml.comparator.diff.XMLDiff;
import com.github.lukaszkusek.xml.comparator.document.XMLDocument;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;

public class XMLComparatorBuilder {

    private XMLComparator xmlComparator;

    private String xml1;
    private XMLDocument xmlDocument1;
    private String xml2;

    private XMLDocument xmlDocument2;
    private boolean ignoreNamespaces;
    private Set<String> xPathsToOmit;

    private Map<String, Pattern> valueExtractors;
    private List<XMLComparatorStep> xmlComparatorSteps;

    XMLComparatorBuilder() {
        this.xmlComparator = new XMLComparator();
        this.ignoreNamespaces = true;
    }

    public XMLComparatorBuilder first(String xml1) {
        this.xml1 = xml1;
        return this;
    }

    public XMLComparatorBuilder first(XMLDocument xmlDocument1) {
        this.xmlDocument1 = xmlDocument1;
        return this;
    }

    public XMLComparatorBuilder second(String xml2) {
        this.xml2 = xml2;
        return this;
    }

    public XMLComparatorBuilder second(XMLDocument xmlDocument2) {
        this.xmlDocument2 = xmlDocument2;
        return this;
    }

    public XMLComparatorBuilder ignoreNamespaces(boolean ignoreNamespaces) {
        this.ignoreNamespaces = ignoreNamespaces;
        return this;
    }

    public XMLComparatorBuilder xPathsToOmit(Set<String> xPathsToOmit) {
        this.xPathsToOmit = xPathsToOmit;
        return this;
    }

    public XMLComparatorBuilder xPathsToOmit(String filePath) throws IOException {
        this.xPathsToOmit = ImmutableSet.copyOf(Resources.readLines(new URL(filePath), Charsets.UTF_8));
        return this;
    }

    public XMLComparatorBuilder valueExtractors(Map<String, String> valueExtractors) {
        this.valueExtractors = Maps.transformValues(valueExtractors, Pattern::compile);
        return this;
    }

    public XMLDiff compare() throws TransformerException, IOException {
        prepareParameters();
        prepareComparators();

        xmlComparator.setXmlDocument1(xmlDocument1);
        xmlComparator.setXmlDocument2(xmlDocument2);
        xmlComparator.setXPathsToOmitPredicate(new XPathsToOmitPredicate(xPathsToOmit));
        xmlComparator.setXmlComparatorSteps(xmlComparatorSteps);

        return xmlComparator.compare();
    }

    private void prepareParameters() throws TransformerException, IOException {
        if (xmlDocument1 == null) {
            xmlDocument1 = XMLDocument.fromXML(xml1, ignoreNamespaces);
        }

        if (xmlDocument2 == null) {
            xmlDocument2 = XMLDocument.fromXML(xml2, ignoreNamespaces);
        }

        if (xPathsToOmit == null) {
            xPathsToOmit = ImmutableSet.of();
        }

        if (valueExtractors == null) {
            valueExtractors = ImmutableMap.of();
        }
    }

    private void prepareComparators() {
        xmlComparatorSteps = ImmutableList.of(
                new XMLValuesComparator(valueExtractors),
                new XMLAttributesComparator(valueExtractors),
                new XMLCheckChildrenOrderComparator(),
                new XMLChildrenIgnoringOrderComparator(xmlComparator)
        );
    }

    private static class XPathsToOmitPredicate implements Predicate<DifferenceInformation> {

        private Collection<String> xPathsToOmit;

        private XPathsToOmitPredicate(Collection<String> xPathsToOmit) {
            this.xPathsToOmit = xPathsToOmit;
        }

        @Override
        public boolean test(DifferenceInformation differenceInformation) {
            for (String xPathToOmit : xPathsToOmit) {
                if (differenceInformation.getXPath().contains(xPathToOmit)) {
                    return false;
                }
            }

            return true;
        }
    }
}
