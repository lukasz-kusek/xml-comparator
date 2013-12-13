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

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import com.github.lukaszkusek.xml.comparator.diff.DifferenceCode;
import com.github.lukaszkusek.xml.comparator.diff.DifferenceInformation;
import com.github.lukaszkusek.xml.comparator.node.Node;
import com.google.common.collect.Maps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(value = Parameterized.class)
public class DifferenceInformationParametrizedTest {

    private Node node1;
    private Node node2;
    private String attributeName;
    private DifferenceCode differenceCode;

    private String expectedXpath;
    private String expectedMessage;

    public DifferenceInformationParametrizedTest(
            Node node1, Node node2, String attributeName, DifferenceCode differenceCode,
            String expectedXpath, String expectedMessage) {

        this.node1 = node1;
        this.node2 = node2;
        this.attributeName = attributeName;
        this.differenceCode = differenceCode;

        this.expectedXpath = expectedXpath;
        this.expectedMessage = expectedMessage;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> parameters() {
        //@formatter:off
        return Arrays
                .asList(new Object[][]{
                        {new NodeBuilder().withXpath("/xpath1").get(),
                                new NodeBuilder().withXpath("/xpath2").get(),
                                null,
                                DifferenceCode.DIFFERENT_ROOT_NODE,
                                null,
                                "First root node: /xpath1 is different from second root node: /xpath2"},

                        {new NodeBuilder().withXpath("/xpath1").get(),
                                null,
                                null,
                                DifferenceCode.MISSING_ELEMENT_IN_SECOND_DOCUMENT,
                                "/xpath1",
                                "Xpath exists in first xml document but doesn't exist in second one."},

                        {null,
                                new NodeBuilder().withXpath("/xpath2").get(),
                                null,
                                DifferenceCode.MISSING_ELEMENT_IN_FIRST_DOCUMENT,
                                "/xpath2",
                                "Xpath exists in second xml document but doesn't exist in first one."},

                        {new NodeBuilder().withXpath("/xpath").withValue("value1").get(),
                                new NodeBuilder().withXpath("/xpath").withValue("value2").get(),
                                null,
                                DifferenceCode.DIFFERENT_VALUE,
                                "/xpath/text()",
                                "Value differs. First: value1. Second: value2"},

                        {new NodeBuilder().withXpath("/xpath").withAttribute("attribute", "value1").get(),
                                new NodeBuilder().withXpath("/xpath").withAttribute("attribute", "value2").get(),
                                "attribute",
                                DifferenceCode.DIFFERENT_ATTRIBUTE_VALUE,
                                "/xpath/@attribute",
                                "Attribute value differs. First: value1. Second: value2"},

                        {new NodeBuilder().withXpath("/xpath").get(),
                                new NodeBuilder().withXpath("/xpath").get(),
                                null,
                                DifferenceCode.INCORRECT_ORDER,
                                "/xpath",
                                "Element from first document exists in first in incorrect order."},
                });
        //@formatter:on
    }

    @Test
    public void shouldReturnXpath() {
        // given
        DifferenceInformation differenceInformation =
                new DifferenceInformation(node1, node2, attributeName, differenceCode);

        // when
        String xpath = differenceInformation.getXPath();

        // then
        assertThat(xpath).isEqualTo(expectedXpath);
    }

    @Test
    public void shouldReturnMessage() {
        // given
        DifferenceInformation differenceInformation =
                new DifferenceInformation(node1, node2, attributeName, differenceCode);

        // when
        String message = differenceInformation.getMessage();

        // then
        assertThat(message).isEqualTo(expectedMessage);
    }

    private static class NodeBuilder {
        private String xpath;
        private String value;
        private Map<String, String> attributes;

        private NodeBuilder() {
            attributes = Maps.newHashMap();
        }

        public NodeBuilder withXpath(String xpath) {
            this.xpath = xpath;
            return this;
        }

        public NodeBuilder withValue(String value) {
            this.value = value;
            return this;
        }

        public NodeBuilder withAttribute(String attribute, String value) {
            this.attributes.put(attribute, value);
            return this;
        }

        public Node get() {
            Node node = new Node(0, xpath);
            node.setValue(value);

            attributes.entrySet().forEach(attribute -> node.putAttribute(attribute.getKey(), attribute.getValue()));

            return node;
        }
    }
}
