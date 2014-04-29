package org.symqle.modeler;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.symqle.modeler.utils.SimpleLogger;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Goal which generates Symqle model
 * @goal generate
 *
 * @phase generate-sources
 *
 */
public class GenerateMojo
    extends AbstractMojo
{
    /**
     * The current Maven project.
     *
     * @parameter default-value="${project}"
     * @readonly
     * @required
     */
    private MavenProject project;

    /**
     * The directory where generated sources are put. This directory is included
     * to project sources.
     * @parameter expression="${symqle.outputDirectory}" default-value="${project.build.directory}/generated-sources/main/"
     * @required
     */
    private File outputDirectory;

    /**
     * The directory where sample sources are put. This directory is not included
     * to project sources. Set it equal to outputDirectory to include samples to compilation.
     * @parameter expression="${symqle.samplesDirectory}" default-value="${project.build.directory}/generated-samples/"
     * @required
     */
    private File samplesDirectory;

    /**
     * Jdbc driver fully qualified class name.
     * @parameter expression="symqle.jdbcDriver"
     * @required
     */
    private String jdbcDriver;

    /**
     * Jdbc URL.
     * @parameter expression="symqle.jdbcUrl"
     * @required
     */
    private String jdbcUrl;

    /**
     * Database user name.
     * @parameter expression="symqle.jdbcUser"
     * @required
     */
    private String jdbcUser;

    /**
     * Database password.
     * @parameter expression="symqle.jdbcPassword"
     * @required
     */
    private String jdbcPassword;

    /**
     * Java package of generated primary key and data transfer object classes.
     * @parameter expression="symqle.dataPackage"
     * @required
     */
    private String dataPackage;

    /**
     * Java package of generated table classes.
     * @parameter expression="symqle.symqleModelPackage"
     * @required
     */
    private String symqleModelPackage;

    /**
     * Java package of generated Selectors and Crud helpers.
     * @parameter expression="symqle.dataAccessPackage"
     * @required
     */
    private String dataAccessPackage;

    /**
     * Regexp pattern to exclude table types from model. Example: "ALIAS|SYNONYM". Case-insensitive.
     * @parameter expression="symqle.excludeTableTypePattern"
     */
    private String excludeTableTypePattern;

    /**
     * Regexp pattern to include table types from model. Example: "TABLE|VIEW". Case-insensitive.
     * You may use excludeTableType, includeTableType or both.
     * @parameter expression="symqle.includeTableTypePattern"
     */
    private String includeTableTypePattern;

    /**
     * Regexp pattern to exclude tables by name from model. Example: "SYS.*". Case-insensitive.
     * @parameter expression="symqle.excludeTableNamePattern"
     */
    private String excludeTableNamePattern;

    /**
     * Regexp pattern to include tables by name to model. Example: ".*". Case-insensitive.
     * @parameter expression="symqle.includeTableNamePattern"
     */
    private String includeTableNamePattern;

    /**
     * Regexp pattern to exclude columns by name from model. Example: ".*_create_ts". Case-insensitive.
     * @parameter expression="symqle.excludeColumnNamePattern"
     */
    private String excludeColumnNamePattern;

    /**
     * Regexp pattern to include columns by name from model. Example: ".*". Case-insensitive.
     * @parameter expression="symqle.includeColumnNamePattern"
     */
    private String includeColumnNamePattern;

    /**
     * Do not generate primary key classes; use java.lang and java.sql classes for keys. Default: false.
     * @parameter expression="symqle.naturalKeys"
     */
    private boolean naturalKeys;


    public void execute()
        throws MojoExecutionException
    {

        prepareDirectory(outputDirectory);
        prepareDirectory(samplesDirectory);
        project.addCompileSourceRoot(outputDirectory.getAbsolutePath());

        final Properties localProperties = new Properties();
        localProperties.setProperty("outputDirectory", outputDirectory.toString());
        localProperties.setProperty("samplesDirectory", samplesDirectory.toString());
        localProperties.setProperty("jdbcDriver", jdbcDriver);
        localProperties.setProperty("jdbcUrl", jdbcUrl);
        localProperties.setProperty("jdbcUser", jdbcUser);
        localProperties.setProperty("jdbcPassword", jdbcPassword);
        localProperties.setProperty("dataPackage", dataPackage);
        localProperties.setProperty("dataAccessPackage", dataAccessPackage);
        localProperties.setProperty("symqleModelPackage", symqleModelPackage);
        setOptionalProperty(localProperties, "excludeTableTypePattern", excludeTableTypePattern);
        setOptionalProperty(localProperties, "includeTableTypePattern", includeTableTypePattern);
        setOptionalProperty(localProperties, "excludeTableNamePattern", excludeTableNamePattern);
        setOptionalProperty(localProperties, "includeTableNamePattern", includeTableNamePattern);
        setOptionalProperty(localProperties, "excludeColumnNamePattern", excludeColumnNamePattern);
        setOptionalProperty(localProperties, "includeColumnNamePattern", includeColumnNamePattern);
        setOptionalProperty(localProperties, "naturalKeys", String.valueOf(naturalKeys));
        SimpleLogger.setLogger(new MojoLogger());
        try {
            new Launcher().run(localProperties);
        } catch (Exception e) {
            throw new MojoExecutionException("Generation failed", e);
        }
    }

    private void setOptionalProperty(final Properties properties, final String key, final String value) {
        if (value != null) {
            properties.setProperty(key, value);
        }
    }

    private void prepareDirectory(final File f) throws MojoExecutionException {
        if ( !f.exists() )
        {
            if (!f.mkdirs()) {
                throw new MojoExecutionException("Cannot create " + f);
            }
        } else {
            try {
                FileUtils.cleanDirectory(f);
            } catch (IOException e) {
                throw new MojoExecutionException(e.getMessage(), e);
            }
        }
    }

    private class MojoLogger extends SimpleLogger {
        @Override
        protected void logError(final String format, final Object... args) {
            getLog().error(String.format(format, args));
        }

        @Override
        protected void logError(final Throwable t, final String format, final Object... args) {
            getLog().error(String.format(format, args), t);
        }

        @Override
        protected void logWarn(final String format, final Object... args) {
            if (getLog().isWarnEnabled()) {
                getLog().warn(String.format(format, args));
            }
        }

        @Override
        protected void logInfo(final String format, final Object... args) {
            if (getLog().isInfoEnabled()) {
                getLog().info(String.format(format, args));
            }
        }

        @Override
        protected void logDebug(final String format, final Object... args) {
            if (getLog().isDebugEnabled()) {
                getLog().debug(String.format(format, args));
            }
        }
    }
}
