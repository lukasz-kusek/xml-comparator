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

import com.github.lukaszkusek.xml.comparator.diff.XMLDiff;
import com.github.lukaszkusek.xml.comparator.document.XMLDocument;
import com.github.lukaszkusek.xml.comparator.util.ResourceReader;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;

public class XMLComparatorTest {

    @Test
    public void shouldFailComparingFileWithAndWithoutNamespace() throws TransformerException, IOException {
        // given
        String xml1 = ResourceReader.getFileContent("withNamespaces1.xml");
        String xml2 = ResourceReader.getFileContent("withoutNamespaces.xml");

        // when
        XMLDiff diff = XMLComparator.builder()
                .first(xml1)
                .second(xml2)
                .ignoreNamespaces(false)
                .compare();

        // then
        assertThat(diff.getDifferencesCount()).isEqualTo(1);
        assertThat(diff.getMessages())
                .containsOnly("First root node: /sch:Preference is different from second root node: /Preference");
    }

    @Test
    public void shouldCompareFileWithAndWithoutNamespaces() throws TransformerException, IOException {
        // given
        String xml1 = ResourceReader.getFileContent("withNamespaces1.xml");
        String xml2 = ResourceReader.getFileContent("withoutNamespaces.xml");

        // when
        XMLDiff diff = XMLComparator.compare(xml1, xml2);

        // then
        assertThat(diff.getDifferencesCount()).isEqualTo(6);
        assertThat(diff.getXPaths())
                .containsOnly(
                        "/Preference/Airport[3]/@Level",
                        "/Preference/Cabin[1]/Info/@Vendor",
                        "/Preference/Airline[1]",
                        "/Preference/Airport[1]/@Exclude",
                        "/Preference/Aggregator[2]",
                        "/Preference/Meal/Info/@MealService");
    }

    @Test
    public void shouldCompareBothFilesWithNamespacesIgnoringNamespaces() throws TransformerException, IOException {
        // given
        String xml1 = ResourceReader.getFileContent("withNamespaces1.xml");
        String xml2 = ResourceReader.getFileContent("withNamespaces2.xml");

        // when
        XMLDiff diff = XMLComparator.compare(xml1, xml2);

        // then
        assertThat(diff.getDifferencesCount()).isEqualTo(3);
        assertThat(diff.getXPaths())
                .containsOnly(
                        "/Preference/Aggregator[2]",
                        "/Preference/Airport[3]/@Level",
                        "/Preference/Airport[1]/@Exclude"
                );
    }

    @Test
    public void shouldCompareBothFilesWithNamespacesNotIgnoringNamespaces() throws TransformerException, IOException {
        // given
        String xml1 = ResourceReader.getFileContent("withNamespaces1.xml");
        String xml2 = ResourceReader.getFileContent("withNamespaces2.xml");

        // when
        XMLDiff diff = XMLComparator.builder()
                .first(xml1)
                .second(xml2)
                .ignoreNamespaces(false)
                .compare();

        // then
        assertThat(diff.getDifferencesCount()).isEqualTo(3);
        assertThat(diff.getXPaths())
                .containsOnly(
                        "/sch:Preference/sch:Aggregator[2]",
                        "/sch:Preference/sch:Airport[3]/@Level",
                        "/sch:Preference/sch:Airport[1]/@Exclude"
                );
    }

    @Test
    public void shouldExtractValueUsingExtractorPatternAndResultInIdentical() throws TransformerException, IOException {
        // given
        String xml1 = ResourceReader.getFileContent("withTimestamp1.xml");
        String xml2 = ResourceReader.getFileContent("withTimestamp2.xml");

        // when
        XMLDiff diff = XMLComparator.builder()
                .first(xml1)
                .second(xml2)
                .valueExtractors(ImmutableMap.of("/Response/Error", "[^:]*:(.*)"))
                .compare();

        // then
        assertThat(diff.getDifferencesCount()).isEqualTo(0);
    }

    @Test
    public void shouldCompareXMLs() throws TransformerException, IOException {
        // given
        String xml1 = ResourceReader.getFileContent("toCompare1.xml");
        String xml2 = ResourceReader.getFileContent("toCompare2.xml");

        // when
        XMLDiff diff = XMLComparator.compare(xml1, xml2);

        // then
        assertThat(diff.getDifferencesCount()).isEqualTo(11);
        assertThat(diff.getXPaths())
                .containsOnly(
                        "/Response/Profiles/ProfileInfo/Profile/Customer/PersonName/GivenName/text()",
                        "/Response/Profiles/ProfileInfo/Profile/Customer/PersonName/Surname/text()",
                        "/Response/Profiles/ProfileInfo/Profile/Customer/Telephone[1]",
                        "/Response/Profiles/ProfileInfo/Profile/Customer/Telephone[2]/@PhoneType",
                        "/Response/Profiles/ProfileInfo/Profile/Customer/EmployeeInfo/@CompanyProfileId",
                        "/Response/Profiles/ProfileInfo/Profile/Customer/EmployeeInfo/@EmployeeTitle",
                        "/Response/Profiles/ProfileInfo/Profile/Customer/EmployeeInfo/@CompanyName",
                        "/Response/Profiles/ProfileInfo/Profile/TPA_ProfileExtensions/CustLoyaltyTotals/@AccountType",
                        "/Response/Profiles/ProfileInfo/Profile/TPA_ProfileExtensions/SpecialDate/@Description",
                        "/Response/Profiles/ProfileInfo/Profile/TPA_ProfileExtensions/CustDefinedData",
                        "/Response/Profiles/ProfileInfo/Profile/TPA_ProfileExtensions/VITCustomer"
                );
    }

    @Test
    public void shouldReturnSourceXMLDocumentsInXMLDiff() throws TransformerException, IOException {
        // given
        XMLDocument xml1 = XMLDocument.fromXML(ResourceReader.getFileContent("toCompare1.xml"));
        XMLDocument xml2 = XMLDocument.fromXML(ResourceReader.getFileContent("toCompare2.xml"));

        // when
        XMLDiff diff = XMLComparator.compare(xml1, xml2);

        // then
        assertThat(diff.getXmlDocument1()).isSameAs(xml1);
        assertThat(diff.getXmlDocument2()).isSameAs(xml2);
    }

}
