package com.atlassian.maven.whence.reporting;

import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Descriptors;
import aQute.bnd.osgi.Packages;
import com.atlassian.maven.whence.data.PackageInfo;
import com.atlassian.maven.whence.data.PackageMapper;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.apache.maven.shared.dependency.graph.traversal.DependencyNodeVisitor;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * Taken from maven dep plugin and its SerializingDependencyNodeVisitor but with better printing output
 */
class ReportingAsTreeVisitor
        implements DependencyNodeVisitor {

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


    private final Log log;
    private final Reporter.ReportDetail reportDetail;
    private final GraphTokens tokens;
    private int depth;
    private final PackageMapper packageMapper;

    ReportingAsTreeVisitor(Log log, PackageMapper packageMapper, Reporter.ReportDetail reportDetail) {
        this.log = log;
        this.reportDetail = reportDetail;
        this.tokens = STANDARD_TOKENS;
        this.packageMapper = packageMapper;

        depth = 0;
    }

    public boolean visit(DependencyNode node) {
        String fillIndent = fillIndent(node);
        String nodeIndent = nodeIndent(node);

        log.info(format("%s%s%s", fillIndent, nodeIndent, node.toNodeString()));

        PackageInfo info = packageMapper.getPackageInfoFor(node.getArtifact());

        if (node.getParent() != null) {
            printPackages(fillIndent, info.getExports(), "exports");

            if (reportDetail == Reporter.ReportDetail.ALL) {
                printPackages(fillIndent, info.getImports(), "imports");

                printPackages(fillIndent, info.getContains(), "contains");
                printPackages(fillIndent, info.getReferences(), "references");
            }
        }


        depth++;
        return true;
    }

    private void printPackages(String fillIndent, Packages packages, final String packageType) {
        Set<Descriptors.PackageRef> packagesOf = packages.keySet();

        printPackageLines(fillIndent, packageType,
                packagesOf.stream()
                        .map(Descriptors.PackageRef::getFQN)
                        .collect(Collectors.toList()));
    }

    private void printPackages(String fillIndent, Parameters packages, final String packageType) {
        Set<String> packagesOf = packages.keySet();
        printPackageLines(fillIndent, packageType, packagesOf);
    }

    private void printPackageLines(String fillIndent, String packageType, Collection<String> packagesOf) {
        String packageIndent = "|      ";

        log.info(format("%s%s%s", fillIndent, packageIndent,
                format("%s (%d)", packageType, packagesOf.size())
                )
        );
        for (String packageRef : packagesOf) {
            log.info(format("%s%s%s", fillIndent, packageIndent,
                    format("\t%s", packageRef)));
        }
    }

    public boolean endVisit(DependencyNode node) {
        depth--;
        return true;
    }

    private String fillIndent(DependencyNode node) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < depth; i++) {
            sb.append(tokens.getFillIndent(isLast(node, i)));
        }
        return sb.toString();
    }

    private String nodeIndent(DependencyNode node) {
        StringBuilder sb = new StringBuilder();
        if (depth > 0) {
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
        int distance = depth - ancestorDepth;

        while (distance-- > 0) {
            node = node.getParent();
        }

        return isLast(node);
    }
}