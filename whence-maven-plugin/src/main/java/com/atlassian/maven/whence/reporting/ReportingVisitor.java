package com.atlassian.maven.whence.reporting;

import com.atlassian.maven.whence.MvnLog;
import com.google.common.base.Strings;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.apache.maven.shared.dependency.graph.traversal.DependencyNodeVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

abstract class ReportingVisitor implements DependencyNodeVisitor {

    private static final String FOUR_SPACES = "    ";

    final MvnLog log;
    private int depth;

    ReportingVisitor(MvnLog log) {
        this.log = log;
        this.depth = 0;
    }

    abstract boolean onNode(DependencyNode node);

    @Override
    public boolean visit(DependencyNode node) {
        boolean continueFlag = onNode(node);
        depth++;
        return continueFlag;
    }

    @Override
    public boolean endVisit(DependencyNode node) {
        depth--;
        return true;
    }

    int depth() {
        return depth;
    }

    String tabs(int count) {
        return Strings.repeat(FOUR_SPACES, count);
    }

    String tab() {
        return Strings.repeat(FOUR_SPACES, 1);
    }

    <T extends Comparable<? super T>> Collection<T> sort(Collection<T> collection) {
        List<T> list = new ArrayList<>(collection);
        Collections.sort(list);
        return list;
    }

}
