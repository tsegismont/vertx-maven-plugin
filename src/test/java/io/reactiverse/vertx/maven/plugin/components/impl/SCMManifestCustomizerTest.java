package io.reactiverse.vertx.maven.plugin.components.impl;

import io.reactiverse.vertx.maven.plugin.mojos.PackageMojo;
import org.apache.maven.model.Scm;
import org.apache.maven.project.MavenProject;
import org.junit.Test;

import java.util.Map;

import static io.reactiverse.vertx.maven.plugin.model.ExtraManifestKeys.SCM_TAG;
import static io.reactiverse.vertx.maven.plugin.model.ExtraManifestKeys.SCM_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Check the {@link SCMManifestCustomizer} class behavior.
 */
public class SCMManifestCustomizerTest {


    @Test
    public void testWithSkippingTheScmMetadata() {
        SCMManifestCustomizer customizer = new SCMManifestCustomizer();
        PackageMojo mojo = mock(PackageMojo.class);
        when(mojo.skipScmMetadata()).thenReturn(true);
        Map<String, String> entries = customizer.getEntries(mojo, createProject());
        assertThat(entries).isEmpty();
    }

    @Test
    public void testWithNoSCM() {
        SCMManifestCustomizer customizer = new SCMManifestCustomizer();
        PackageMojo mojo = mock(PackageMojo.class);
        Map<String, String> entries = customizer.getEntries(mojo, createProject());
        assertThat(entries).isEmpty();
    }

    @Test
    public void testWithSCM() {
        SCMManifestCustomizer customizer = new SCMManifestCustomizer();
        PackageMojo mojo = mock(PackageMojo.class);
        Map<String, String> entries = customizer.getEntries(mojo, createProjectWithScm());
        assertThat(entries).contains(entry(SCM_TAG.header(),"HEAD"), entry(SCM_URL.header(), "https://github.com/openshiftio"));
    }

    private MavenProject createProjectWithScm() {
        MavenProject project = createProject();
        Scm scm = new Scm();
        scm.setUrl("https://github.com/openshiftio");
        scm.setConnection("scm:git:https://github.com/openshiftio/booster-parent.git");
        scm.setDeveloperConnection("scm:git:git:@github.com:openshiftio/booster-parent.git");
        scm.setTag("HEAD");
        project.setScm(scm);
        return project;
    }

    private MavenProject createProject() {
        MavenProject project = new MavenProject();
        project.setArtifactId("acme");
        project.setGroupId("org.acme");
        project.setVersion("1.0-SNAPSHOT");
        return project;
    }

}
