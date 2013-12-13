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

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.lukaszkusek.xml.comparator.comparators.children.cost.CostEntry;
import com.github.lukaszkusek.xml.comparator.comparators.children.cost.CostMatrix;
import com.github.lukaszkusek.xml.comparator.node.INode;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

class CostMatrixFilter {

    Optional<CostMatrix> filterAlreadyAssigned(CostMatrix childrenComparisonMatrix) {
        Set<INode> alreadyAssignedRowKeys = getAlreadyAssignedRows(childrenComparisonMatrix);
        Set<INode> alreadyAssignedColumnKeys = getAlreadyAssignedColumns(childrenComparisonMatrix);

        Collection<INode> filteredRowKeys =
                filterKeys(alreadyAssignedRowKeys, childrenComparisonMatrix.rowKeys());

        Collection<INode> filteredColumnKeys =
                filterKeys(alreadyAssignedColumnKeys, childrenComparisonMatrix.columnKeys());

        if (filteredRowKeys.size() == 0 || filteredColumnKeys.size() == 0) {
            return Optional.empty();
        }

        return Optional.of(filterChildrenComparisonMatrix(childrenComparisonMatrix, filteredRowKeys, filteredColumnKeys));
    }

    private CostMatrix filterChildrenComparisonMatrix(
            CostMatrix childrenComparisonMatrix,
            Collection<INode> filteredRowKeys,
            Collection<INode> filteredColumnKeys) {

        CostMatrix filtered = CostMatrix.create(filteredRowKeys, filteredColumnKeys);

        filtered.getCellSet().forEach(
                cell ->
                        filtered.put(
                                cell.getRowKey(),
                                cell.getColumnKey(),
                                childrenComparisonMatrix.getDifferenceDetails(
                                        cell.getRowKey(),
                                        cell.getColumnKey()))
        );

        return filtered;
    }

    private Collection<INode> filterKeys(Set<INode> alreadyAssignedKeyNodes, List<INode> keyNodes) {
        return Sets.difference(Sets.newHashSet(keyNodes), alreadyAssignedKeyNodes);
    }

    private Set<INode> getAlreadyAssignedRows(CostMatrix costMatrix) {
        return getAlreadyAssignedNodes(costMatrix, cell -> cell.getRowKey());
    }

    private Set<INode> getAlreadyAssignedColumns(CostMatrix costMatrix) {
        return getAlreadyAssignedNodes(costMatrix, cell -> cell.getColumnKey());
    }

    private Set<INode> getAlreadyAssignedNodes(
            CostMatrix costMatrix, Function<Table.Cell<INode, INode, CostEntry>, INode> mapper) {

        return costMatrix.getCellSet().stream()
                .filter(cell -> cell.getValue().isAssigned())
                .map(mapper)
                .collect(Collectors.toSet());
    }

}
