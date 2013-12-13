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

package com.github.lukaszkusek.xml.comparator.comparators.children.cost;

import com.github.lukaszkusek.xml.comparator.diff.DifferenceDetails;

public class CostEntry {

    private boolean assigned;
    private boolean taken;
    private DifferenceDetails differenceDetails;

    CostEntry(boolean taken) {
        this.taken = taken;
    }

    CostEntry(DifferenceDetails differenceDetails) {
        this.differenceDetails = differenceDetails;
    }

    CostEntry(boolean assigned, DifferenceDetails differenceDetails) {
        this.assigned = assigned;
        this.differenceDetails = differenceDetails;
    }

    static CostEntry taken() {
        return new CostEntry(true);
    }

    static CostEntry of(DifferenceDetails differenceDetails) {
        return new CostEntry(differenceDetails);
    }

    static CostEntry assign(DifferenceDetails differenceDetails) {
        return new CostEntry(true, differenceDetails);
    }

    public boolean isNotTaken() {
        return !taken;
    }

    public boolean isAssigned() {
        return assigned;
    }

    DifferenceDetails getDifferenceDetails() {
        return differenceDetails;
    }
}
