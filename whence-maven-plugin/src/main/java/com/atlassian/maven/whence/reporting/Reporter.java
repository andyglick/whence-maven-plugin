package com.atlassian.maven.whence.reporting;

import com.atlassian.maven.whence.MvnLog;
import com.atlassian.maven.whence.data.CodeMapper;
import org.apache.maven.shared.dependency.graph.DependencyNode;

public class Reporter {

    public enum ReportStyle {
        TREE, FLAT
    }

    public enum ReportDetail {
        EXPORTS,
        ALL
    }

    public void report(MvnLog log, ReportStyle reportStyle, ReportDetail reportDetail, DependencyNode rootNode, CodeMapper packageMapper) {
        if (reportStyle == ReportStyle.TREE) {
            ReportingAsTreeVisitor visitor = new ReportingAsTreeVisitor(log, packageMapper, reportDetail);
            rootNode.accept(visitor);
        }
        if (reportStyle == ReportStyle.FLAT) {
            ReportingAsFlatVisitor visitor = new ReportingAsFlatVisitor(log, packageMapper, reportDetail);
            rootNode.accept(visitor);
        }
    }
}
