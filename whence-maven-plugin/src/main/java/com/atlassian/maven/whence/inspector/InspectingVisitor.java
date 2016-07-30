package com.atlassian.maven.whence.inspector;

import com.atlassian.maven.whence.MvnLog;
import com.atlassian.maven.whence.data.CodeMapper;
import com.atlassian.maven.whence.data.PackageInfo;
import com.atlassian.maven.whence.reporting.Reporter;
import com.google.common.base.Stopwatch;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.apache.maven.shared.dependency.graph.traversal.DependencyNodeVisitor;

import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class InspectingVisitor {

    private CodeMapper.Builder packageMapperBuilder = CodeMapper.builder();
    private JarInspector jarInspector = new JarInspector();

    public CodeMapper inspect(DependencyNode rootNode, ArtifactResolution artifactResolution, Reporter.ReportDetail reportDetail, MvnLog log) {

        final Stopwatch mainStopWatch = Stopwatch.createStarted();
        DependencyNodeVisitor visitor = new DependencyNodeVisitor() {
            @Override
            public boolean endVisit(DependencyNode node) {
                return true;
            }

            @Override
            public boolean visit(DependencyNode node) {
                if (mainStopWatch.elapsed(TimeUnit.SECONDS) > 20) {
                    log.info(sayStillWaiting());

                    mainStopWatch.reset();
                    mainStopWatch.start();
                }

                inspectNode(node, artifactResolution, reportDetail, log);

                return true;
            }

            private String sayStillWaiting() {
                return "\t ... (byte code analysis takes a while bear with us)";
            }
        };

        rootNode.accept(visitor);

        return packageMapperBuilder.build();
    }

    private void inspectNode(DependencyNode node, ArtifactResolution artifactResolution, Reporter.ReportDetail reportDetail, MvnLog log) {
        Stopwatch stopWatch = Stopwatch.createStarted();
        Artifact artifact = artifactResolution.resolveArtifact(node.getArtifact());

        log.verbose(format("\t\t'%s'...", artifact));
        PackageInfo info = jarInspector.inspect(node, artifactResolution, reportDetail);
        log.verbose(format("\t\t  (%d ms)", stopWatch.elapsed(MILLISECONDS)));


        packageMapperBuilder.addPackage(artifact, info);
    }


}
