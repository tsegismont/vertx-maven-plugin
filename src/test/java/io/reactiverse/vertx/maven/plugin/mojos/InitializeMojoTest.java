package io.reactiverse.vertx.maven.plugin.mojos;

import io.reactiverse.vertx.maven.plugin.ArtifactBuilder;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.DefaultMavenExecutionResult;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.lifecycle.DefaultLifecycleExecutor;
import org.apache.maven.model.Build;
import org.apache.maven.plugin.DefaultBuildPluginManager;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.internal.impl.DefaultRepositorySystem;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks the behavior of the {@code InitializeMojo}.
 */
public class InitializeMojoTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File projectBuildDir;

    @Before
    public void setUp() throws Exception {
        projectBuildDir = temporaryFolder.newFolder();
    }

    @Test
    public void testSkip() throws Exception {
        InitializeMojo mojo = createMojoInstance();
        mojo.skip = true;
        mojo.execute();
        assertThat(mojo.skip).isTrue();
    }

    @Test
    public void testThatTestDependenciesAreFilteredOut() throws Exception {
        InitializeMojo mojo = createMojoInstance();

        Set<Artifact> deps = Stream.of(
                new ArtifactBuilder().artifact("acme-1").type("txt").build(),
                // Jar artifact for the next dependency, as the file will be emtpy if analyze it will
                // throw an exception
                new ArtifactBuilder().artifact("acme-test").scope("test").build(),
                new ArtifactBuilder().artifact("acme-2").classifier("hello").type("txt").build())
            .collect(toSet());
        mojo.project.setDependencyArtifacts(deps);

        mojo.execute();

        assertThat(mojo.skip).isFalse();
    }

    private InitializeMojo createMojoInstance() throws PlexusContainerException {
        InitializeMojo mojo = new InitializeMojo();
        mojo.project = new MavenProject();
        mojo.repositorySystem = new DefaultRepositorySystem();
        mojo.repositorySystemSession = new DefaultRepositorySystemSession();
        mojo.buildPluginManager = new DefaultBuildPluginManager();
        mojo.container = new DefaultPlexusContainer(new DefaultContainerConfiguration());
        mojo.mavenSession = new MavenSession(mojo.container, mojo.repositorySystemSession, new DefaultMavenExecutionRequest(),
            new DefaultMavenExecutionResult());

        mojo.lifecycleExecutor = new DefaultLifecycleExecutor();
        mojo.remoteRepositories = Collections.emptyList();
        mojo.projectBuildDir = projectBuildDir.getAbsolutePath();

        Build build = new Build();
        build.setOutputDirectory(projectBuildDir.getAbsolutePath());
        mojo.project.setBuild(build);

        return mojo;
    }

}
