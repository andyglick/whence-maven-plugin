package com.atlassian.maven.whence.reporting;

import aQute.bnd.header.Attrs;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Descriptors;
import aQute.bnd.osgi.Packages;
import com.atlassian.maven.whence.MvnLog;
import com.atlassian.maven.whence.data.CodeMapper;
import com.atlassian.maven.whence.data.PackageInfo;
import org.apache.maven.shared.dependency.graph.DependencyNode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * Taken from maven dep plugin and its SerializingDependencyNodeVisitor but with better printing output
 */
class ReportingAsTreeVisitor
        extends ReportingVisitor {

    private static final String PACKAGE_INDENT = "|      ";

    /**
     * Provides tokens to use when serializing the dependency graph.
     */
    private static class GraphTokens {
        private final String nodeIndent;

        private final String lastNodeIndent;

        private final String fillIndent;

        private final String lastFillIndent;

        GraphTokens(String nodeIndent, String lastNodeIndent, String fillIndent, String lastFillIndent) {
            this.nodeIndent = nodeIndent;
            this.lastNodeIndent = lastNodeIndent;
            this.fillIndent = fillIndent;
            this.lastFillIndent = lastFillIndent;
        }

        String getNodeIndent(boolean last) {
            return last ? lastNodeIndent : nodeIndent;
        }

        String getFillIndent(boolean last) {
            return last ? lastFillIndent : fillIndent;
        }
    }

    private static final GraphTokens STANDARD_TOKENS = new GraphTokens("+- ", "\\- ", "|  ", "   ");


    private final Reporter.ReportDetail reportDetail;
    private final GraphTokens tokens;
    private final CodeMapper packageMapper;

    ReportingAsTreeVisitor(MvnLog log, CodeMapper packageMapper, Reporter.ReportDetail reportDetail) {
        super(log);
        this.reportDetail = reportDetail;
        this.tokens = STANDARD_TOKENS;
        this.packageMapper = packageMapper;
    }

    @Override
    boolean onNode(DependencyNode node) {
        String fillIndent = fillIndent(node);
        String nodeIndent = nodeIndent(node);

        log.info(format("%s%s%s", fillIndent, nodeIndent, node.toNodeString()));

        PackageInfo info = packageMapper.getPackageInfoFor(node.getArtifact());
        File file = info.getArtifact().getFile();

        log.info(format("%s%s%s", fillIndent, PACKAGE_INDENT,
                format("%s%s", tab(), file)));


        if (reportDetail == Reporter.ReportDetail.ALL) {
            printParameters(fillIndent, info.getExports(), "exports");
            printParameters(fillIndent, info.getImports(), "imports");

            printPackages(fillIndent, info.getApi(), "api");
            printPackages(fillIndent, info.getContains(), "contains");
            printPackages(fillIndent, info.getReferences(), "references");
        } else {
            printParameters(fillIndent, info.getExports(), "exports");
            printPackages(fillIndent, info.getApi(), "api");
        }
        return true;
    }

    private void printPackages(String fillIndent, Packages packages, final String packageType) {
        List<String> packageNames = packages.keySet()
                .stream()
                .map(Descriptors.PackageRef::getFQN)
                .collect(Collectors.toList());

        printPackageLines(fillIndent, packageType, sort(packageNames));
    }

    private void printParameters(String fillIndent, Parameters packages, final String packageType) {
        // we have multiple line of output
        List<String> output = new ArrayList<>();
        for (String packageName : sort(packages.keySet())) {
            output.add(packageName);
            Attrs attrs = ParameterAccess.attrs(packages, packageName);
            for (String key : attrs.keySet()) {
                if (key.equals("uses:")) {
                    String value = attrs.get(key);
                    output.add(String.format("%s%s=", tab(), key));
                    String[] split = value.split(",");
                    for (String splitPackage : split) {
                        output.add(String.format("%s%s", tabs(2), splitPackage));
                    }

                } else {
                    output.add(String.format("%s%s=%s", tab(), key, attrs.get(key)));
                }
            }
        }
        printPackageLines(fillIndent, packageType, output);
    }

    private void printPackageLines(String fillIndent, String packageType, Collection<String> lines) {

        log.info(format("%s%s%s", fillIndent, PACKAGE_INDENT,
                format("%s (%d)", packageType, lines.size())
                )
        );
        for (String packageRef : lines) {
            log.info(format("%s%s%s", fillIndent, PACKAGE_INDENT,
                    format("%s%s", tab(), packageRef)));
        }
    }

    private String fillIndent(DependencyNode node) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < depth(); i++) {
            sb.append(tokens.getFillIndent(isLast(node, i)));
        }
        return sb.toString();
    }

    private String nodeIndent(DependencyNode node) {
        StringBuilder sb = new StringBuilder();
        if (depth() > 0) {
            sb.append(tokens.getNodeIndent(isLast(node)));
        }
        return sb.toString();
    }

    private boolean isLast(DependencyNode node) {
        DependencyNode parent = node.getParent();

        boolean last;

        if (parent == null) {
            last = true;
        } else {
            List<DependencyNode> siblings = parent.getChildren();

            last = (siblings.indexOf(node) == siblings.size() - 1);
        }

        return last;
    }

    private boolean isLast(DependencyNode node, int ancestorDepth) {
        int distance = depth() - ancestorDepth;

        while (distance-- > 0) {
            node = node.getParent();
        }

        return isLast(node);
    }
}