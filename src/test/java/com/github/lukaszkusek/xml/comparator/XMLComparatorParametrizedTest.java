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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.github.lukaszkusek.xml.comparator.diff.DifferenceInformation;
import com.github.lukaszkusek.xml.comparator.diff.XMLDiff;
import com.github.lukaszkusek.xml.comparator.util.ResourceReader;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.xml.sax.SAXException;

@RunWith(value = Parameterized.class)
public class XMLComparatorParametrizedTest {

    private String xml1;
    private String xml2;
    private List<String> expectedUniqueDifferencesXPaths;
    private int expectedFilteredDifferenceDetailsSize;
    private int expectedDifferenceDetailsSize;

    public XMLComparatorParametrizedTest(
            String testNumber, List<String> expectedUniqueDifferencesXPaths,
            int expectedFilteredDifferenceDetailsSize, int expectedDifferenceDetailsSize) throws IOException {

        this.xml1 = ResourceReader.getFileContent("XMLDiff/" + testNumber + "-first.xml");
        this.xml2 = ResourceReader.getFileContent("XMLDiff/" + testNumber + "-second.xml");
        this.expectedUniqueDifferencesXPaths = expectedUniqueDifferencesXPaths;
        this.expectedFilteredDifferenceDetailsSize = expectedFilteredDifferenceDetailsSize;
        this.expectedDifferenceDetailsSize = expectedDifferenceDetailsSize;
    }

    @Parameterized.Parameters(name = "{0}-first.xml comparing {0}-second.xml")
    public static java.util.Collection<Object[]> parameters() {
        //@formatter:off
        return Arrays
                .asList(new Object[][]{
                        {"1", ImmutableList.of(
                                "/Response/Profile/Customer/@Gender",
                                "/Response/Profile/Customer/EmployeeInfo",
                                "/Response/Profile/Customer/PaymentForm/PaymentCard/@ExpireDate",
                                "/Response/Profile/PrefCollections/PrefCollection/AirlinePref",
                                "/Response/Profile/TPA_ProfileExtensions/CustLoyaltyTotals",
                                "/Response/Profile/TPA_ProfileExtensions/Access",
                                "/Response/Profile/TPA_ProfileExtensions/VITCustomer",
                                "/Response/UniqueID/CompanyName/@CodeContext",
                                "/Response/UniqueID/CompanyName/@TravelSector"),
                                7,
                                13},

                        {"2", ImmutableList.of(
                                "/Response/Profile/Customer/Address/@IsAddressNew",
                                "/Response/Profile/Customer/Address/Addressee",
                                "/Response/Profile/Customer/CustLoyalty/@TravelSector",
                                "/Response/Profile/Customer/Email/@DefaultInd",
                                "/Response/Profile/Customer/Email/@VIT_LineType",
                                "/Response/Profile/Customer/Email/@VIT_OrderNmbr",
                                "/Response/Profile/Customer/PersonName/@VIT_LineType",
                                "/Response/Profile/Customer/PersonName/@VIT_OrderNmbr",
                                "/Response/Profile/Customer/Telephone/@DefaultInd",
                                "/Response/Profile/Customer/Telephone/@VIT_LineType",
                                "/Response/Profile/Customer/Telephone/@VIT_OrderNmbr",
                                "/Response/Profile/TPA_ProfileExtensions/CustLoyaltyTotals/@MilesToExpire",
                                "/Response/Profile/TPA_ProfileExtensions/CustLoyaltyTotals/@PreferenceRank",
                                "/Response/Profile/TPA_ProfileExtensions/CustLoyaltyTotals/@TierLevelIndicator2",
                                "/Response/Profile/TPA_ProfileExtensions/ProfileStatus",
                                "/Response/Profile/TPA_ProfileExtensions/Access",
                                "/Response/UniqueID/CompanyName/@CodeContext",
                                "/Response/UniqueID/CompanyName/@TravelSector"),
                                17,
                                18},

                        {"3", ImmutableList.of(
                                "/Response/Profile/Customer/@Gender",
                                "/Response/Profile/Customer/CustLoyalty/@TravelSector",
                                "/Response/Profile/Customer/Email/@VIT_LineType",
                                "/Response/Profile/Customer/Email/@VIT_OrderNmbr",
                                "/Response/Profile/Customer/PersonName/@VIT_LineType",
                                "/Response/Profile/Customer/PersonName/@VIT_OrderNmbr",
                                "/Response/Profile/Customer/Telephone/@DefaultInd",
                                "/Response/Profile/Customer/Telephone/@VIT_LineType",
                                "/Response/Profile/Customer/Telephone/@VIT_OrderNmbr",
                                "/Response/Profile/TPA_ProfileExtensions/CustLoyaltyTotals",
                                "/Response/Profile/TPA_ProfileExtensions/ProfileStatus",
                                "/Response/Profile/TPA_ProfileExtensions/Access",
                                "/Response/UniqueID/CompanyName/@CodeContext",
                                "/Response/UniqueID/CompanyName/@TravelSector"),
                                14,
                                16},

                        {"4", ImmutableList.of(
                                "/Response/Profile/Customer/Address",
                                "/Response/Profile/Customer/Telephone"),
                                2,
                                2},

                        {"5", ImmutableList.of(
                                "/Response/Profile/@CreateDateTime",
                                "/Response/Profile/@LastModifyDateTime",
                                "/Response/Profile/Customer/@Gender",
                                "/Response/Profile/Customer/Address/AddressLine",
                                "/Response/Profile/Customer/Address/CityName/@ContextCode",
                                "/Response/Profile/Customer/Email/@DefaultInd",
                                "/Response/Profile/PrefCollections/PrefCollection",
                                "/Response/Profile/TPA_ProfileExtensions/ProfileStatus",
                                "/Response/UniqueID/@Instance",
                                "/Response/UniqueID/CompanyName/@CodeContext",
                                "/Response/UniqueID/CompanyName/@TravelSector",
                                "/Response/UniqueID/CompanyName/text()"),
                                11,
                                12},

                        {"6", ImmutableList.of(
                                "/Response/CustDefinedData/@Name"),
                                60,
                                60},

                        {"7", ImmutableList.of(
                                "/Response/CustDefinedData"),
                                132,
                                132}
                });
        //@formatter:on
    }

    @Test
    public void shouldCompareXMLs() throws TransformerException, IOException, SAXException {
        // given

        // when
        XMLDiff diff = XMLComparator.compare(xml1, xml2);

        // then
        assertContainsOnly(diff, expectedUniqueDifferencesXPaths);
    }

    @Test
    public void shouldReturnAllDifferenceDetails() throws TransformerException, IOException, SAXException {
        // given

        // when
        XMLDiff diff = XMLComparator.compare(xml1, xml2);

        // then
        assertThat(diff.getDifferenceInformationSet()).hasSize(expectedDifferenceDetailsSize);
    }

    @Test
    public void shouldFilterXPaths() throws TransformerException, IOException, SAXException {
        // given
        XMLDiff diff = XMLComparator.builder()
                .first(xml1)
                .second(xml2)
                .xPathsToOmit(
                        ImmutableSet.of(
                                "PaymentCard/@ExpireDate",
                                "@Gender",
                                "TPA_ProfileExtensions/Access"))
                .compare();

        // when
        Set<DifferenceInformation> filteredDifferenceInformationSet = diff.getDifferenceInformationSet();

        // then
        assertThat(filteredDifferenceInformationSet).hasSize(expectedFilteredDifferenceDetailsSize);
    }

    @Test
    public void shouldFilterXPathsReadFromFile() throws TransformerException, IOException, SAXException {
        // given
        XMLDiff diff = XMLComparator.builder()
                .first(xml1)
                .second(xml2)
                .xPathsToOmit(ResourceReader.getURL("XMLDiff/xPathsToOmit.txt").toString())  //todo
                .compare();

        // when
        Set<DifferenceInformation> filteredDifferenceInformationSet = diff.getDifferenceInformationSet();

        // then
        assertThat(filteredDifferenceInformationSet).hasSize(expectedFilteredDifferenceDetailsSize);
    }

    private void assertContainsOnly(XMLDiff xmlDiff, List<String> expectedDifferencesXPaths) {
        assertThat(xmlDiff.getUniqueDifferenceInformationSet().size()).isEqualTo(expectedDifferencesXPaths.size());

        Collection<String> xmlDiffXPaths = xmlDiff.getSimpleXPaths();

        expectedDifferencesXPaths.forEach(expectedDiffXPath -> {
            assertThat(xmlDiffXPaths).contains(expectedDiffXPath);
        });
    }

}
