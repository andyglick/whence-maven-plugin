package com.atlassian.maven.whence.reporting;

import aQute.bnd.header.Attrs;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Descriptors;
import aQute.bnd.osgi.Packages;
import com.atlassian.maven.whence.data.PackageInfo;
import com.atlassian.maven.whence.data.PackageMapper;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.apache.maven.shared.dependency.graph.traversal.DependencyNodeVisitor;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

class ReportingAsFlatVisitor
        implements DependencyNodeVisitor {
    private final Log log;
    private final Reporter.ReportDetail reportDetail;
    private final PackageMapper packageMapper;

    ReportingAsFlatVisitor(Log log, PackageMapper packageMapper, Reporter.ReportDetail reportDetail) {
        this.log = log;
        this.reportDetail = reportDetail;
        this.packageMapper = packageMapper;
    }

    public boolean visit(DependencyNode node) {
        PackageInfo info = packageMapper.getPackageInfoFor(node.getArtifact());

        String gav = gav(node.getArtifact());

        printParameters(gav, info.getExports(), "exports");

        if (reportDetail == Reporter.ReportDetail.ALL) {
            printParameters(gav, info.getImports(), "imports");

            printPackages(gav, info.getContains(), "contains");
            printPackages(gav, info.getReferences(), "references");
        }
        return true;
    }

    private void printPackages(String gav, Packages packages, final String packageType) {

        List<String> list = sort(packages.keySet().stream()
                .map(Descriptors.PackageRef::getFQN)
                .collect(Collectors.toList()));
        printPackageLines(gav, packageType, list);
    }

    private void printParameters(String gav, Parameters packages, final String packageType) {
        Collection<String> output = sort(packages.keySet().stream()
                .map(packageName -> buildFlatAttrs(packages, packageName))
                .collect(Collectors.toList()));
        printPackageLines(gav, packageType, output);
    }

    private String buildFlatAttrs(Parameters packages, String packageName) {
        StringBuilder sb = new StringBuilder();
        sb.append(packageName);
        sb.append(" ");
        Attrs attrs = ParameterAccess.attrs(packages, packageName);
        int count = 0;
        for (String key : attrs.keySet()) {
            if (count > 0) {
                sb.append(";");
            }
            sb.append(key).append("=").append(attrs.get(key));
            count++;
        }
        return sb.toString();
    }

    private void printPackageLines(String gav, String packageType, Collection<String> packagesOf) {

        if (packagesOf.isEmpty()) {
            log.info(format("   %s %s %s", gav, packageType, "0 packages"));
        } else {
            for (String packageName : packagesOf) {
                log.info(format("   %s %s %s", gav, packageType, packageName));
            }
        }
    }

    private String gav(Artifact artifact) {
        return artifact.toString();
    }

    private List<String> sort(List<String> list) {
        Collections.sort(list);
        return list;
    }

    public boolean endVisit(DependencyNode node) {
        return true;
    }

}