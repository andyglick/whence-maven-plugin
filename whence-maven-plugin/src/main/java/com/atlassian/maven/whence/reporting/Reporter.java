package com.atlassian.maven.whence.reporting;

import com.atlassian.maven.whence.data.PackageMapper;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.shared.dependency.graph.DependencyNode;

public class Reporter {

    public enum ReportStyle {
        TREE, FLAT
    }

    public enum ReportDetail {
        EXPORTS,
        ALL
    }

    public void report(Log log, ReportStyle reportStyle, ReportDetail reportDetail, DependencyNode rootNode, PackageMapper packageMapper) {
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
