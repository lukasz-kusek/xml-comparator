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

package com.github.lukaszkusek.xml.comparator.diff;

import java.util.Set;
import java.util.stream.Collectors;

import com.github.lukaszkusek.xml.comparator.document.XMLDocument;

public class XMLDiff {

    private XMLDocument xmlDocument1;
    private XMLDocument xmlDocument2;
    private DifferenceDetails differenceDetails;

    public XMLDiff(XMLDocument xmlDocument1, XMLDocument xmlDocument2, DifferenceDetails differenceDetails) {
        this.xmlDocument1 = xmlDocument1;
        this.xmlDocument2 = xmlDocument2;
        this.differenceDetails = differenceDetails;
    }

    public XMLDocument getXmlDocument1() {
        return xmlDocument1;
    }

    public XMLDocument getXmlDocument2() {
        return xmlDocument2;
    }

    public int getDifferencesCount() {
        return differenceDetails.getCount();
    }

    public Set<DifferenceInformation> getDifferenceInformationSet() {
        return differenceDetails.getDifferenceInformationSet();
    }

    public Set<String> getXPaths() {
        return getDifferenceInformationSet().stream()
                .map(DifferenceInformation::getXPath)
                .collect(Collectors.toSet());
    }

    public Set<String> getMessages() {
        return getDifferenceInformationSet().stream()
                .map(DifferenceInformation::getMessage)
                .collect(Collectors.toSet());
    }

    public Set<DifferenceInformation> getUniqueDifferenceInformationSet() {
        return differenceDetails.getUniqueDifferenceInformationSet();
    }

    public Set<String> getSimpleXPaths() {
        return getUniqueDifferenceInformationSet().stream()
                .map(DifferenceInformation::getSimpleXPath)
                .collect(Collectors.toSet());
    }
}
