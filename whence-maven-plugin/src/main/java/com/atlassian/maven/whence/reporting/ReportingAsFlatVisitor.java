package com.atlassian.maven.whence.reporting;

import aQute.bnd.header.Attrs;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Descriptors;
import aQute.bnd.osgi.Packages;
import com.atlassian.maven.whence.MvnLog;
import com.atlassian.maven.whence.data.CodeMapper;
import com.atlassian.maven.whence.data.PackageInfo;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.shared.dependency.graph.DependencyNode;

import java.util.Collection;
import java.util.stream.Collectors;

import static java.lang.String.format;

class ReportingAsFlatVisitor
        extends ReportingVisitor {
    private final Reporter.ReportDetail reportDetail;
    private final CodeMapper packageMapper;

    ReportingAsFlatVisitor(MvnLog log, CodeMapper packageMapper, Reporter.ReportDetail reportDetail) {
        super(log);
        this.reportDetail = reportDetail;
        this.packageMapper = packageMapper;
    }

    @Override
    boolean onNode(DependencyNode node) {
        PackageInfo info = packageMapper.getPackageInfoFor(node.getArtifact());

        String gav = gav(node.getArtifact());

        if (reportDetail == Reporter.ReportDetail.ALL) {
            printFlatParameters(gav, info.getExports(), "exports");

            printFlatParameters(gav, info.getImports(), "imports");

            printFlatPackages(gav, info.getApi(), "api");
            printFlatPackages(gav, info.getContains(), "contains");
            printFlatPackages(gav, info.getReferences(), "references");
        } else {
            printFlatParameters(gav, info.getExports(), "exports");

            printFlatPackages(gav, info.getApi(), "api");
        }
        return true;
    }

    private void printFlatPackages(String gav, Packages packages, final String packageType) {
        Collection<String> packageNames = sort(packages.keySet()
                .stream()
                .map(Descriptors.PackageRef::getFQN)
                .collect(Collectors.toList())
        );
        printPackageLines(gav, packageType, packageNames);
    }

    private void printFlatParameters(String gav, Parameters packages, final String packageType) {
        Collection<String> output = sort(packages.keySet()
                .stream()
                .map(packageName -> buildFlatAttrs(packages, packageName))
                .collect(Collectors.toList())
        );
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

}