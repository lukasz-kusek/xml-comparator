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

import static org.assertj.core.api.Assertions.assertThat;

import javax.xml.transform.TransformerException;
import java.io.IOException;

import com.github.lukaszkusek.xml.comparator.diff.DifferenceInformation;
import com.github.lukaszkusek.xml.comparator.diff.XMLDiff;
import com.github.lukaszkusek.xml.comparator.util.ResourceReader;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;

public class ReadmeExamplesTest {

    @Test
    public void theSameExample() throws IOException, TransformerException {
        // given
        String xml1 = ResourceReader.getFileContent("Readme/theSame1.xml");
        String xml2 = ResourceReader.getFileContent("Readme/theSame2.xml");

        // when
        XMLDiff xmlDiff = XMLComparator.compare(xml1, xml2);

        // then
        assertThat(xmlDiff.getDifferencesCount()).isEqualTo(0);
    }

    @Test
    public void attributesDifferentOrderExample() throws IOException, TransformerException {
        // given
        String xml1 = ResourceReader.getFileContent("Readme/attributesDifferentOrder1.xml");
        String xml2 = ResourceReader.getFileContent("Readme/attributesDifferentOrder2.xml");

        // when
        XMLDiff xmlDiff = XMLComparator.compare(xml1, xml2);

        // then
        assertThat(xmlDiff.getDifferencesCount()).isEqualTo(0);
    }

    @Test
    public void elementsDifferentOrderSameTypeExample() throws IOException, TransformerException {
        // given
        String xml1 = ResourceReader.getFileContent("Readme/elementsDifferentOrderSameType1.xml");
        String xml2 = ResourceReader.getFileContent("Readme/elementsDifferentOrderSameType2.xml");

        // when
        XMLDiff xmlDiff = XMLComparator.compare(xml1, xml2);

        // then
        assertThat(xmlDiff.getDifferencesCount()).isEqualTo(0);
    }

    @Test
    public void bestMatchExample() throws IOException, TransformerException {
        // given
        String xml1 = ResourceReader.getFileContent("Readme/bestMatch1.xml");
        String xml2 = ResourceReader.getFileContent("Readme/bestMatch2.xml");

        // when
        XMLDiff xmlDiff = XMLComparator.compare(xml1, xml2);

        // then
        assertThat(xmlDiff.getDifferencesCount()).isEqualTo(1);

        DifferenceInformation differenceInformation = xmlDiff.getDifferenceInformationSet().iterator().next();
        assertThat(differenceInformation.getXPath()).isEqualTo("/xml/element[2]/@value");
        assertThat(differenceInformation.getSimpleXPath()).isEqualTo("/xml/element/@value");
        assertThat(differenceInformation.getMessage()).isEqualTo("Attribute value differs. First: def. Second: ghi");
    }

    @Test
    public void valueExtractorsExample() throws IOException, TransformerException {
        // given
        String xml1 = ResourceReader.getFileContent("Readme/valueExtractors1.xml");
        String xml2 = ResourceReader.getFileContent("Readme/valueExtractors2.xml");

        // when
        XMLDiff xmlDiff = XMLComparator.builder()
                .first(xml1)
                .second(xml2)
                .valueExtractors(ImmutableMap.of("/xml/element/@value", "[^:]*:(.*)"))
                .compare();

        // then
        assertThat(xmlDiff.getDifferencesCount()).isEqualTo(0);
    }

    @Test
    public void ignoreNamespacesExample() throws IOException, TransformerException {
        // given
        String xml1 = ResourceReader.getFileContent("Readme/ignoreNamespaces1.xml");
        String xml2 = ResourceReader.getFileContent("Readme/ignoreNamespaces2.xml");

        // when
        XMLDiff xmlDiff = XMLComparator.builder()
                .first(xml1)
                .second(xml2)
                .ignoreNamespaces(true)
                .compare();

        // then
        assertThat(xmlDiff.getDifferencesCount()).isEqualTo(0);
    }

    @Test
    public void ignoreXPathsExample() throws IOException, TransformerException {
        // given
        String xml1 = ResourceReader.getFileContent("Readme/ignoreXPaths1.xml");
        String xml2 = ResourceReader.getFileContent("Readme/ignoreXPaths2.xml");

        // when
        XMLDiff xmlDiff = XMLComparator.builder()
                .first(xml1)
                .second(xml2)
                .xPathsToOmit(ImmutableSet.of("element/@ignored"))
                .compare();

        // then
        assertThat(xmlDiff.getDifferencesCount()).isEqualTo(0);
    }

    @Test
    public void uniqueDifferenceInformationExample() throws IOException, TransformerException {
        // given
        String xml1 = ResourceReader.getFileContent("Readme/uniqueDifferenceInformation1.xml");
        String xml2 = ResourceReader.getFileContent("Readme/uniqueDifferenceInformation2.xml");

        // when
        XMLDiff xmlDiff = XMLComparator.compare(xml1, xml2);

        // then
        assertThat(xmlDiff.getXPaths()).containsOnly(
                "/xml/element[1]/@value",
                "/xml/element[2]/@value"
        );

        assertThat(xmlDiff.getSimpleXPaths()).containsOnly(
                "/xml/element/@value"
        );
    }
}
