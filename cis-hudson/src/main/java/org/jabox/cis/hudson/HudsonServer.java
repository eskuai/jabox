/*
 * Jabox Open Source Version
 * Copyright (C) 2009-2010 Dimitris Kapanidis                                                                                                                          
 * 
 * This file is part of Jabox
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package org.jabox.cis.hudson;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.wicket.persistence.provider.ConfigXstreamDao;
import org.jabox.apis.embedded.AbstractEmbeddedServer;
import org.jabox.environment.Environment;
import org.jabox.maven.helper.MavenDownloader;
import org.jabox.model.DefaultConfiguration;
import org.jabox.utils.SettingsModifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HudsonServer extends AbstractEmbeddedServer {
    private static final long serialVersionUID = 6348183433480294734L;

    private static final Logger LOGGER = LoggerFactory
        .getLogger(HudsonServer.class);

    private static final String GROUP_ID = "org.jvnet.hudson.main";

    private static final String ARTIFACT_ID = "hudson-war";

    private static final String VERSION = "1.395";

    private static final String TYPE = "war";

    public String getServerName() {
        return "hudson";
    }

    @Override
    public String getWarPath() {
        injectPlugins();
        return MavenDownloader.downloadArtifact(GROUP_ID, ARTIFACT_ID,
            VERSION, TYPE).getAbsolutePath();
    }

    public static void injectPlugins() {
        injectPlugin("org.jvnet.hudson.plugins", "analysis-core", "1.14");
        injectPlugin("org.jvnet.hudson.plugins", "dry", "1.5");
        injectPlugin("org.jvnet.hudson.plugins", "pmd", "3.10");
        injectPlugin("org.jvnet.hudson.plugins", "findbugs", "4.14");
        injectPlugin("org.jvnet.hudson.plugins", "checkstyle", "3.10");
        injectPlugin("org.jvnet.hudson.plugins.m2release", "m2release",
            "0.6.1");
        injectPlugin("org.jvnet.hudson.plugins", "redmine", "0.9");
        injectPlugin("org.jvnet.hudson.plugins", "git", "1.1.3");
        injectPlugin("org.jvnet.hudson.plugins", "claim", "1.7");
        injectPlugin("org.jvnet.hudson.plugins", "ci-game", "1.17");
        injectPlugin("org.jvnet.hudson.plugins", "sonar", "1.6.1");
        injectConfiguration("hudson.tasks.Maven.xml");
        injectConfiguration("hudson.plugins.sonar.SonarPublisher.xml");
    }

    private static void injectConfiguration(final String resource) {
        File dest = new File(Environment.getHudsonHomeDir(), resource);

        if (!dest.exists()) {
            DefaultConfiguration dc = ConfigXstreamDao.getConfig();
            InputStream is =
                HudsonServer.class.getResourceAsStream(resource);

            Map<String, String> values = new HashMap<String, String>();
            values.put("${sonar.host.url}", dc.getCqm().getServer()
                .getUrl());

            try {
                String data =
                    SettingsModifier.parseInputStream(is, values);
                FileUtils.writeStringToFile(dest, data);
            } catch (IOException e) {
                LOGGER.error("Error writting: " + dest.getAbsolutePath(),
                    e);
            }
        }
    }

    private static void injectPlugin(final String groupId,
            final String artifactId, final String version) {
        File plugin =
            MavenDownloader.downloadArtifact(groupId, artifactId, version,
                "hpi");
        try {
            FileUtils.copyFile(plugin, new File(getHudsonPluginDir(),
                stripVersion(plugin.getName())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected static String stripVersion(final String name) {
        String replaceAll = name.replaceAll("-[^-]*.hpi", ".hpi");
        return replaceAll;
    }

    private static File getHudsonPluginDir() {
        return new File(Environment.getHudsonHomeDir(), "plugins");
    }
}
