package org.apache.oozie.command.purge;

import com.google.common.annotations.VisibleForTesting;
import org.apache.oozie.ErrorCode;
import org.apache.oozie.executor.jpa.JPAExecutorException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@VisibleForTesting
public class SelectorTreeTraverser<T, U> {
    final T rootNode;
    final JPAFunction<T, List<U>> childrenFinder;
    final JPAFunction<List<U>, List<T>> selector;

    public SelectorTreeTraverser(final T rootNode, final JPAFunction<T, List<U>> childrenFinder,
                                 final JPAFunction<List<U>, List<T>> selector) {
        this.rootNode = rootNode;
        this.childrenFinder = childrenFinder;
        this.selector = selector;
    }

    public List<T> findAllDescendantNodesIfSelectable() throws JPAExecutorException {
        List<T> allDescendantNodes = new ArrayList<>();
        Set<T> uniqueDescendantNodes = new HashSet<>();
        allDescendantNodes.add(rootNode);
        uniqueDescendantNodes.add(rootNode);
        int nextIndexToCheck = 0;
        while (nextIndexToCheck < allDescendantNodes.size()) {
            T id = allDescendantNodes.get(nextIndexToCheck);
            List<U> childrenNodes = childrenFinder.apply(id);
            List<T> selectedChildren = selector.apply(childrenNodes);
            if (selectedChildren.size() == childrenNodes.size()) {
                allDescendantNodes.addAll(selectedChildren);
                uniqueDescendantNodes.addAll(selectedChildren);
                if (allDescendantNodes.size() != uniqueDescendantNodes.size()) {
                    throw new JPAExecutorException(ErrorCode.E0613, rootNode);
                }
            } else {
                return new ArrayList<>();
            }
            ++nextIndexToCheck;
        }
        return allDescendantNodes;
    }
}
