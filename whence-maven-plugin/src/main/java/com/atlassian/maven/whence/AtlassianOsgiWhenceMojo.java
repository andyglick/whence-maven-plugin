package com.atlassian.maven.whence;

import com.atlassian.maven.whence.data.PackageMapper;
import com.atlassian.maven.whence.inspector.ArtifactResolution;
import com.atlassian.maven.whence.inspector.InspectingVisitor;
import com.atlassian.maven.whence.reporting.Reporter;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilderException;
import org.apache.maven.shared.dependency.graph.DependencyNode;

import java.io.File;

import static java.lang.String.format;
import static java.lang.String.valueOf;

/**
 */
@Mojo(
        name = "whence",
        defaultPhase = LifecyclePhase.PREPARE_PACKAGE,
        requiresDependencyResolution = ResolutionScope.COMPILE
)
public class AtlassianOsgiWhenceMojo extends AbstractMojo {
    private static final String OUR_NAME = "Atlassian Whence Maven Plugin";

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(defaultValue = "${project.build.outputDirectory}")
    private File outputDirectory;

    @Parameter(defaultValue = "false", property = "verbose")
    private Boolean verbose;

    @Parameter(property = "style", defaultValue = "tree")
    private String style;

    @Parameter(property = "detail", defaultValue = "exports")
    private String detail;

    @Component(hint = "default")
    private DependencyGraphBuilder dependencyGraphBuilder;

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Starting " + OUR_NAME + "...");
        getLog().info("");
        getLog().info("\tAnalyzing code....");
        getLog().info("");
        long then = System.currentTimeMillis();

        Reporter.ReportStyle reportStyle = toStyle(style);
        Reporter.ReportDetail reportDetail = toDetail(detail);

        ArtifactResolution artifactResolution = new ArtifactResolution(project.getArtifacts());
        DependencyNode graph = getDependencyGraph(project, artifactResolution);
        PackageMapper mappings = new InspectingVisitor()
                .inspect(graph, artifactResolution);

        new Reporter()
                .report(getLog(), reportStyle, reportDetail, graph, mappings);

        long ms = System.currentTimeMillis() - then;
        getLog().info("");
        getLog().info(format("\tAnalysis ran in %d ms.", ms));
    }

    private DependencyNode getDependencyGraph(MavenProject project, ArtifactResolution artifactResolution) {

        try {
            return dependencyGraphBuilder.buildDependencyGraph(project,
                    artifact -> isSuitableArtifact(
                            artifactResolution.resolveArtifact(artifact)
                    )
            );
        } catch (DependencyGraphBuilderException e) {
            throw new RuntimeException(e);
        }
    }


    private boolean isSuitableArtifact(Artifact artifact) {
        if (artifact.getArtifactHandler().isAddedToClasspath() &&
                !Artifact.SCOPE_TEST.equals(artifact.getScope())) {
            File file = artifact.getFile();
            if (file != null) {
                return true;
            }
            getLog().warn(
                    "File is not available for artifact " + artifact + " in project "
                            + project.getArtifact());
        }
        return false;
    }

    private Reporter.ReportStyle toStyle(String style) {
        try {
            return Reporter.ReportStyle.valueOf(valueOf(style).toUpperCase());
        } catch (IllegalArgumentException e) {
            getLog().warn(format("Unable to interpret 'style' parameter of '%s'... using 'tree'", style));
            return Reporter.ReportStyle.TREE;
        }
    }

    private Reporter.ReportDetail toDetail(String detail) {
        try {
            return Reporter.ReportDetail.valueOf(valueOf(detail).toUpperCase());
        } catch (IllegalArgumentException e) {
            getLog().warn(format("Unable to interpret 'detail' parameter of '%s'... using 'exports'", detail));
            return Reporter.ReportDetail.EXPORTS;
        }
    }
}
