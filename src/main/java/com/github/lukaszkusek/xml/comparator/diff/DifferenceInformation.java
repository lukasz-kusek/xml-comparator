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

import java.util.Optional;
import java.util.function.Function;

import com.github.lukaszkusek.xml.comparator.node.Node;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class DifferenceInformation {

    private Optional<Node> node1;
    private Optional<Node> node2;
    private String attributeName;
    private DifferenceCode differenceCode;

    public DifferenceInformation(Node node1, Node node2, String attributeName, DifferenceCode differenceCode) {
        Preconditions.checkArgument(differenceCode != null, "DifferenceCode cannot be null.");

        this.node1 = Optional.ofNullable(node1);
        this.node2 = Optional.ofNullable(node2);
        this.attributeName = attributeName;
        this.differenceCode = differenceCode;
    }

    public Optional<Node> getNode1() {
        return node1;
    }

    public Optional<Node> getNode2() {
        return node2;
    }

    private String getNodeXpath(Optional<Node> node) {
        return node.map(Node::getXPath).orElse("[null]");
    }

    public String getNode1Xpath() {
        return getNodeXpath(node1);
    }

    public String getNode2Xpath() {
        return getNodeXpath(node2);
    }

    private String getNodeValue(Optional<Node> node) {
        return node.map(Node::getValue).orElse("[null]");
    }

    public String getNode1Value() {
        return getNodeValue(node1);
    }

    public String getNode2Value() {
        return getNodeValue(node2);
    }

    private String getNodeAttributeValue(Optional<Node> node) {
        return node
                .map(Node::getAttributes)
                .map(attributes -> attributes.get(attributeName))
                .orElse("[null]");
    }

    public String getNode1AttributeValue() {
        return getNodeAttributeValue(node1);
    }

    public String getNode2AttributeValue() {
        return getNodeAttributeValue(node2);
    }

    public String getXPath() {
        return getXPath(Node::getXPath);
    }

    public String getSimpleXPath() {
        return getXPath(Node::getSimpleXPath);
    }

    private String getXPath(Function<Node, String> xPathExtractor) {
        final String suffix;

        //@formatter:off
        switch (differenceCode) {
            case DIFFERENT_ROOT_NODE:
                return null;

            case DIFFERENT_VALUE:
                suffix = "/text()";
                break;

            case DIFFERENT_ATTRIBUTE_VALUE:
                suffix = "/@" + attributeName;
                break;

            default:
                suffix = "";
        }
        //@formatter:on

        return node1
                .map(xPathExtractor)
                .map(xPath -> xPath + suffix)
                .orElse(
                        node2.map(xPathExtractor)
                                .map(xPath -> xPath + suffix)
                                .orElse(null));
    }

    public String getAttributeName() {
        return attributeName;
    }

    public DifferenceCode getDifferenceCode() {
        return differenceCode;
    }

    public String getMessage() {
        //@formatter:off
        switch (differenceCode) {
            case DIFFERENT_ROOT_NODE:
                return differenceCode.getMessage(getNode1Xpath(), getNode2Xpath());

            case MISSING_ELEMENT_IN_SECOND_DOCUMENT:
            case MISSING_ELEMENT_IN_FIRST_DOCUMENT:
                break;

            case DIFFERENT_VALUE:
                return differenceCode.getMessage(getNode1Value(), getNode2Value());

            case DIFFERENT_ATTRIBUTE_VALUE:
                return differenceCode.getMessage(getNode1AttributeValue(), getNode2AttributeValue());

            case INCORRECT_ORDER:
                break;
        }
        //@formatter:on

        return differenceCode.getMessage();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        DifferenceInformation that = (DifferenceInformation) other;

        return new EqualsBuilder()
                .append(attributeName, that.attributeName)
                .append(differenceCode, that.differenceCode)
                .append(node1, that.node1)
                .append(node2, that.node2)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(attributeName)
                .append(differenceCode)
                .append(node1)
                .append(node2)
                .toHashCode();
    }
}
