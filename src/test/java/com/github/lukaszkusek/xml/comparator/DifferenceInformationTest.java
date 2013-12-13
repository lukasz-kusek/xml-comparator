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

import com.github.lukaszkusek.xml.comparator.diff.DifferenceCode;
import com.github.lukaszkusek.xml.comparator.diff.DifferenceInformation;
import com.github.lukaszkusek.xml.comparator.node.Node;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class DifferenceInformationTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldThrowExceptionBecauseOfNullDifferenceCode() {
        // given
        DifferenceInformationBuilder builder = new DifferenceInformationBuilder()
                .withDifferenceCode(null);

        // when
        // then
        thrown.expect(IllegalArgumentException.class);
        builder.get();
    }

    @Test
    public void shouldReturnNode1() {
        // given
        Node node = new Node(0, "/xpath");

        DifferenceInformation differenceInformation =
                new DifferenceInformationBuilder().withNode1(node).get();

        // when
        assertThat(differenceInformation.getNode1().get()).isSameAs(node);
    }

    @Test
    public void shouldReturnNode2() {
        // given
        Node node = new Node(0, "/xpath");

        DifferenceInformation differenceInformation =
                new DifferenceInformationBuilder().withNode2(node).get();

        // when
        assertThat(differenceInformation.getNode2().get()).isSameAs(node);
    }

    private static class DifferenceInformationBuilder {
        private Node node1;
        private Node node2;
        private String attributeName;
        private DifferenceCode differenceCode;

        public DifferenceInformationBuilder() {
            withNode1(new Node(0, "/xpath1"));
            withNode2(new Node(0, "/xpath2"));
            withAttributeName("attribute");
            withDifferenceCode(DifferenceCode.DIFFERENT_ROOT_NODE);
        }

        public DifferenceInformationBuilder withNode1(Node node1) {
            this.node1 = node1;

            return this;
        }

        public DifferenceInformationBuilder withNode2(Node node2) {
            this.node2 = node2;

            return this;
        }

        public DifferenceInformationBuilder withAttributeName(String attributeName) {
            this.attributeName = attributeName;

            return this;
        }

        public DifferenceInformationBuilder withDifferenceCode(DifferenceCode differenceCode) {
            this.differenceCode = differenceCode;

            return this;
        }

        public DifferenceInformation get() {
            return new DifferenceInformation(node1, node2, attributeName, differenceCode);
        }
    }
}
