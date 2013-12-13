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
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.github.lukaszkusek.xml.comparator.node.Node;
import com.google.common.collect.Sets;

public class DifferenceDetails  {

    private Set<DifferenceInformation> differenceInformationSet;
    private Set<DifferenceInformation> uniqueDifferenceInformationSet;

    public DifferenceDetails() {
        differenceInformationSet = Sets.newHashSet();
        uniqueDifferenceInformationSet = Sets.newHashSet();
    }

    public static DifferenceDetails empty() {
        return new DifferenceDetails();
    }

    public static DifferenceDetails of(Node node1, Node node2, DifferenceCode differenceCode) {
        DifferenceDetails differenceDetails = new DifferenceDetails();
        differenceDetails.put(node1, node2, differenceCode);

        return differenceDetails;
    }

    public DifferenceDetails put(Node node1, Node node2, DifferenceCode differenceCode) {
        put(node1, node2, null, differenceCode);

        return this;
    }

    public static DifferenceDetails of(Node node1, Node node2, String attributeName, DifferenceCode differenceCode) {
        DifferenceDetails differenceDetails = new DifferenceDetails();
        differenceDetails.put(node1, node2, attributeName, differenceCode);

        return differenceDetails;
    }

    public DifferenceDetails put(Node node1, Node node2, String attributeName, DifferenceCode differenceCode) {
        differenceInformationSet.add(new DifferenceInformation(node1, node2, attributeName, differenceCode));
        uniqueDifferenceInformationSet.add(
                new DifferenceInformation(
                        Optional.ofNullable(node1).map(SimpleXPathNode::new).orElse(null),
                        Optional.ofNullable(node2).map(SimpleXPathNode::new).orElse(null),
                        attributeName,
                        differenceCode));

        return this;
    }

    public DifferenceDetails putAll(DifferenceDetails differenceDetails) {
        this.differenceInformationSet.addAll(differenceDetails.getDifferenceInformationSet());
        this.uniqueDifferenceInformationSet.addAll(differenceDetails.getUniqueDifferenceInformationSet());

        return this;
    }

    public Set<DifferenceInformation> getDifferenceInformationSet() {
        return differenceInformationSet;
    }

    public void setDifferenceInformationSet(Set<DifferenceInformation> differenceInformationSet) {
        this.differenceInformationSet = differenceInformationSet;
    }

    public Set<DifferenceInformation> getUniqueDifferenceInformationSet() {
        return uniqueDifferenceInformationSet;
    }

    public void setUniqueDifferenceInformationSet(Set<DifferenceInformation> uniqueDifferenceInformationSet) {
        this.uniqueDifferenceInformationSet = uniqueDifferenceInformationSet;
    }

    public DifferenceDetails filter(Predicate<DifferenceInformation> xPathsToOmitPredicate) {
        DifferenceDetails filteredDifferenceDetails = new DifferenceDetails();

        filteredDifferenceDetails.setUniqueDifferenceInformationSet(
                filter(getUniqueDifferenceInformationSet(), xPathsToOmitPredicate));

        filteredDifferenceDetails.setDifferenceInformationSet(
                filter(getDifferenceInformationSet(), xPathsToOmitPredicate));

        return filteredDifferenceDetails;
    }

    private Set<DifferenceInformation> filter(
            Set<DifferenceInformation> differenceInformationSet, Predicate<DifferenceInformation> xPathsToOmitPredicate) {

        return differenceInformationSet.stream().filter(xPathsToOmitPredicate).collect(Collectors.toSet());
    }

    public int getCount() {
        return differenceInformationSet.size();
    }

    public boolean isBestMatch() {
        return getCount() == 0;
    }
}
