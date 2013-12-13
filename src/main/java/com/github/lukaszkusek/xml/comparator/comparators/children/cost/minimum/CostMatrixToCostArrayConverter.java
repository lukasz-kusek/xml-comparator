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

package com.github.lukaszkusek.xml.comparator.comparators.children.cost.minimum;

import com.github.lukaszkusek.xml.comparator.comparators.children.cost.CostMatrix;

class CostMatrixToCostArrayConverter {

    private CostMatrix childrenComparisonMatrix;

    CostMatrixToCostArrayConverter(CostMatrix childrenComparisonMatrix) {
        this.childrenComparisonMatrix = childrenComparisonMatrix;
    }

    void applyMinimumCostAssignment(int[] minimumCostMatching) {
        for (int i = 0; i < minimumCostMatching.length; i++) {
            childrenComparisonMatrix.setBestMatch(
                    childrenComparisonMatrix.getRowKey(i),
                    childrenComparisonMatrix.getColumnKey(minimumCostMatching[i]));
        }
    }

    CostMatrix getCostMatrix() {
        return childrenComparisonMatrix;
    }

    double[][] getCostArray() {
        int rowKeysSize = childrenComparisonMatrix.rowKeys().size();
        int columnKeysSize = childrenComparisonMatrix.columnKeys().size();

        double[][] costMatrix = new double[rowKeysSize][columnKeysSize];

        for (int i = 0; i < rowKeysSize; i++) {
            for (int j = 0; j < columnKeysSize; j++) {
                costMatrix[i][j] = childrenComparisonMatrix.getDifferenceDetails(i, j).getCount();
            }
        }

        return costMatrix;
    }
}
