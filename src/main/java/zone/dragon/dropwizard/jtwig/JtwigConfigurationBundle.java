/*
 * Copyright 2018 Bryan Harclerode
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package zone.dragon.dropwizard.jtwig;

import java.util.Map;

import org.jtwig.JtwigModel;
import org.jtwig.environment.DefaultEnvironmentConfiguration;
import org.jtwig.environment.EnvironmentConfiguration;
import org.jtwig.environment.EnvironmentConfigurationBuilder;
import org.jtwig.escape.EscapeEngine;

import com.fasterxml.jackson.core.io.JsonStringEncoder;

import io.dropwizard.Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * Enables Jtwig parsing of the Dropwizard configuration file
 */
public class JtwigConfigurationBundle implements Bundle {
    /**
     * Key under which environment variables can be found in the default model
     */
    public static final String ENVIRONMENT_KEY = "env";
    /**
     * Key under which system properties can be found in the default model
     */
    public static final String SYSTEM_PROPERTY_KEY = "system";
    /**
     * Name of the escape format used for escaping YAML strings
     */
    public static final String YAML_ESCAPE_ENGINE_NAME = "yaml";
    /**
     * JSON encoder instance used to escape YAML strings
     */
    private static final JsonStringEncoder ENCODER = JsonStringEncoder.getInstance();
    /**
     * Escaping engine for use with Jtwig that escapes YAML strings
     */
    public static final EscapeEngine YAML_ESCAPE_ENGINE = input -> new String(ENCODER.quoteAsString(input));

    private final JtwigModel model;
    private final EnvironmentConfiguration environmentConfiguration;


    /**
     * Configures the bundle with a default model of environment variables, accessible as properties of {@code env}, and system properties,
     * accessible in the template as properties of {@code system}.
     */
    public JtwigConfigurationBundle() {
        this(JtwigModel.newModel().with(ENVIRONMENT_KEY, System.getenv()).with(SYSTEM_PROPERTY_KEY, System.getProperties()));
    }

    /**
     * Configures the bundle with the specified model; It is recommended to include environment variables and system properties in a manner
     * that is compatible with the default model.
     *
     * @param model
     *     Initial model for the Jtwig template when the config is rendered
     */
    public JtwigConfigurationBundle(Map<String, Object> model) {
        this(JtwigModel.newModel(model));
    }

    /**
     * Configures the bundle with the specified model; It is recommended to include environment variables and system properties in a manner
     * that is compatible with the default model.
     *
     * @param model
     *     Initial model for the Jtwig template when the config is rendered
     */
    public JtwigConfigurationBundle(JtwigModel model) {
        this(new EnvironmentConfigurationBuilder(new DefaultEnvironmentConfiguration())
                 .escape()
                 .withDefaultEngine(YAML_ESCAPE_ENGINE_NAME)
                 .withInitialEngine(YAML_ESCAPE_ENGINE_NAME)
                 .engines()
                 .add(YAML_ESCAPE_ENGINE_NAME, YAML_ESCAPE_ENGINE)
                 .and()
                 .and()
                 .build(), model);
    }

    /**
     * Configures the bundle with the specified Jtwig configuration and model; It is recommended to include environment variables and system
     * properties in a manner that is compatible with the default model, and that YAML-compatible string escaping be enabled by default. The
     * configuration provided will be extended with an in-memory resource resolver for resolving the template during startup.
     *
     * @param environmentConfiguration
     *     Configuration to use for the Jtwig templating engine
     * @param model
     *     Initial model for the Jtwig template when the config is rendered
     */
    public JtwigConfigurationBundle(EnvironmentConfiguration environmentConfiguration, JtwigModel model) {
        this.model = model;
        this.environmentConfiguration = environmentConfiguration;
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        JtwigConfigurationSourceProvider sourceProvider = new JtwigConfigurationSourceProvider(
            bootstrap.getConfigurationSourceProvider(),
            environmentConfiguration,
            model
        );
        bootstrap.setConfigurationSourceProvider(sourceProvider);
    }

    @Override
    public void run(Environment environment) {
        // not used
    }

    /**
     * The initial model that will be used when rendering the template
     *
     * @return The initial model that will be used when rendering the template
     */
    public JtwigModel getModel() {
        return model;
    }

    /**
     * The Jtwig configuration that will be used when rendering the template
     *
     * @return The Jtwig configuration that will be used when rendering the template
     */
    public EnvironmentConfiguration getEnvironmentConfiguration() {
        return environmentConfiguration;
    }
}
