/*
 *
 *   Copyright (c) 2016-2018 Red Hat, Inc.
 *
 *   Red Hat licenses this file to you under the Apache License, version
 *   2.0 (the "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *   implied.  See the License for the specific language governing
 *   permissions and limitations under the License.
 */
package io.reactiverse.vertx.maven.plugin.components.impl;

import io.reactiverse.vertx.maven.plugin.components.ManifestCustomizerService;
import io.reactiverse.vertx.maven.plugin.model.ExtraManifestKeys;
import io.reactiverse.vertx.maven.plugin.mojos.AbstractVertxMojo;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
@Component(
    role = ManifestCustomizerService.class,
    hint = "project"
)
public class ProjectManifestCustomizer implements ManifestCustomizerService {

    @Override
    public Map<String, String> getEntries(AbstractVertxMojo mojo, MavenProject project) {
        Map<String, String> attributes = new HashMap<>();
        Model model = project.getModel();

        attributes.put(ExtraManifestKeys.PROJECT_ARTIFACT_ID.header(), model.getArtifactId());
        attributes.put(ExtraManifestKeys.PROJECT_GROUP_ID.header(), model.getGroupId());
        attributes.put(ExtraManifestKeys.PROJECT_VERSION.header(), model.getVersion());
        attributes.put(ExtraManifestKeys.PROJECT_NAME.header(),
            model.getName() == null ? model.getArtifactId() : model.getName());

        attributes.put(ExtraManifestKeys.BUILD_TIMESTAMP.header(), manifestTimestampFormat(new Date()));

        if (project.getUrl() != null) {
            attributes.put(ExtraManifestKeys.PROJECT_URL.header(), project.getUrl());
        }

        // TODO get the filtered lists.
        List<Dependency> dependencies = model.getDependencies();
        if (dependencies != null && !dependencies.isEmpty()) {
            String deps = dependencies.stream()
                .filter(d -> "compile".equals(d.getScope()) || null == d.getScope())
                .map(ProjectManifestCustomizer::asCoordinates)
                .collect(Collectors.joining(" "));
            attributes.put(ExtraManifestKeys.PROJECT_DEPS.header(), deps);
        }

        return attributes;
    }

    public static String manifestTimestampFormat(Date date) {
        return new SimpleDateFormat("yyyyMMdd HH:mm:ss z").format(date);
    }

    /**
     * utility method to return {@link Dependency} as G:V:A:C maven coordinates
     *
     * @param dependency - the maven {@link Dependency} whose coordinates need to be computed
     * @return - the {@link Dependency} info as G:V:A:C string
     */
    private static String asCoordinates(Dependency dependency) {
        StringBuilder dependencyCoordinates = new StringBuilder().
            append(dependency.getGroupId())
            .append(":")
            .append(dependency.getArtifactId())
            .append(":")
            .append(dependency.getVersion());

        if (dependency.getClassifier() != null) {
            dependencyCoordinates.append(":").append(dependency.getClassifier());
        }

        return dependencyCoordinates.toString();
    }

}
