package com.example.mavenpom.actions;

import com.example.mavenpom.client.SSLClient;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class FetchMavenPomAction {
    private static final Logger LOG = Logger.getInstance(FetchMavenPomAction.class);
    private static final String MAVEN_POM_URL = 
        "https://repo.maven.apache.org/maven2/org/apache/maven/plugins/maven-clean-plugin/2.5/maven-clean-plugin-2.5.pom";

    public String fetchPom(@NotNull Project project) throws Exception {
        LOG.info("Fetching Maven POM for project: " + project.getName());
        SSLClient sslClient = new SSLClient();
        return sslClient.makeRequest(MAVEN_POM_URL);
    }
} 