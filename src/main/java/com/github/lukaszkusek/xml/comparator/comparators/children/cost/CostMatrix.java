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

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.github.lukaszkusek.xml.comparator.comparators.children.cost.minimum.MinimumCostAssignmentCalculator;
import com.github.lukaszkusek.xml.comparator.diff.DifferenceDetails;
import com.github.lukaszkusek.xml.comparator.node.INode;
import com.google.common.collect.ArrayTable;
import com.google.common.collect.Table;


public class CostMatrix {

    private ArrayTable<INode, INode, CostEntry> costMatrix;

    private CostMatrix(Collection<INode> children1, Collection<INode> children2) {
        costMatrix = ArrayTable.create(children1, children2);
    }

    public static CostMatrix create(Collection<INode> children1, Collection<INode> children2) {
        return new CostMatrix(children1, children2);
    }

    public void findMinimumCostAssignment(MinimumCostAssignmentCalculator minimumCostAssignmentCalculator) {
        merge(minimumCostAssignmentCalculator.getMinimumCostAssignment(this));
    }

    public CostMatrix merge(CostMatrix costMatrix) {
        costMatrix.getCellSet()
                .forEach(cell -> this.costMatrix.put(cell.getRowKey(), cell.getColumnKey(), cell.getValue()));

        return this;
    }

    public void put(INode child1, INode child2, DifferenceDetails differenceDetails) {
        if (isNotAlreadyTaken(child1, child2)) {
            doPut(child1, child2, differenceDetails);
        }
    }

    private boolean isNotAlreadyTaken(INode child1, INode child2) {
        CostEntry costEntry = costMatrix.get(child1, child2);

        return costEntry == null || costEntry.isNotTaken();
    }

    private void doPut(INode child1, INode child2, DifferenceDetails differenceDetails) {
        if (differenceDetails.isBestMatch()) {
            setBestMatch(child1, child2, differenceDetails);
        } else {
            costMatrix.put(child1, child2, CostEntry.of(differenceDetails));
        }
    }

    public void setBestMatch(INode child1, INode child2) {
        setBestMatch(child1, child2, costMatrix.get(child1, child2).getDifferenceDetails());
    }

    private void setBestMatch(INode child1, INode child2, DifferenceDetails differenceDetails) {
        takeRow(child1);
        takeColumn(child2);
        assign(child1, child2, differenceDetails);
    }

    private void takeColumn(INode child2) {
        costMatrix.rowKeyList().forEach(child1ForChild2 -> take(child1ForChild2, child2));
    }

    private void takeRow(INode child1) {
        costMatrix.columnKeyList().forEach(child2ForChild1 -> take(child1, child2ForChild1));
    }

    private void take(INode child1, INode child2) {
        costMatrix.put(child1, child2, CostEntry.taken());
    }

    private void assign(INode child1, INode child2, DifferenceDetails differenceDetails) {
        costMatrix.put(child1, child2, CostEntry.assign(differenceDetails));
    }

    public DifferenceDetails getDifferenceDetails() {
        return costMatrix.values().stream()
                .filter(CostEntry::isAssigned)
                .map(CostEntry::getDifferenceDetails)
                .reduce(new DifferenceDetails(), DifferenceDetails::putAll);
    }

    public DifferenceDetails getDifferenceDetails(INode rowKey, INode columnKey) {
        return Optional.ofNullable(costMatrix.get(rowKey, columnKey))
                .map(CostEntry::getDifferenceDetails)
                .orElse(null);
    }

    public DifferenceDetails getDifferenceDetails(int i, int j) {
        return getDifferenceDetails(rowKeys().get(i), columnKeys().get(j));
    }

    public INode getColumnKey(int index) {
        return columnKeys().get(index);
    }

    public INode getRowKey(int index) {
        return rowKeys().get(index);
    }

    public List<INode> columnKeys() {
        return costMatrix.columnKeyList();
    }

    public List<INode> rowKeys() {
        return costMatrix.rowKeyList();
    }

    // TODO consider if this exposes to much
    public Set<Table.Cell<INode, INode, CostEntry>> getCellSet() {
        return costMatrix.cellSet();
    }
}
