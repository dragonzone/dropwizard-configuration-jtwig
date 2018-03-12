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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import org.jtwig.environment.Environment;
import org.jtwig.environment.EnvironmentConfiguration;
import org.jtwig.environment.EnvironmentConfigurationBuilder;
import org.jtwig.environment.EnvironmentFactory;
import org.jtwig.resource.loader.InMemoryResourceLoader;
import org.jtwig.resource.loader.TypedResourceLoader;
import org.jtwig.resource.reference.ResourceReference;

import io.dropwizard.configuration.ConfigurationSourceProvider;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Wraps the output of a {@link ConfigurationSourceProvider} and parses it as a Jtwig template before passing it on to Dropwizard
 */
@Slf4j
public class JtwigConfigurationSourceProvider implements ConfigurationSourceProvider {
    private final ConfigurationSourceProvider delegate;
    private final EnvironmentConfiguration environmentConfiguration;
    private final JtwigModel model;

    public JtwigConfigurationSourceProvider(@NonNull ConfigurationSourceProvider delegate, @NonNull EnvironmentConfiguration environmentConfiguration, @NonNull JtwigModel model) {
        this.delegate = delegate;
        this.environmentConfiguration = environmentConfiguration;
        this.model = model;
    }

    /**
     * Injects a resource resolver into the configuration for resolving the root template
     *
     * @param environment
     *     Configuration to modify to add a resolver for the root template
     * @param configReference
     *     Reference that the configuration template should be resolved as
     * @param configStream
     *     Stream containing the root template
     *
     * @return The updated configuration
     */
    protected EnvironmentConfiguration addConfigResourceLoader(@NonNull EnvironmentConfiguration environment, @NonNull ResourceReference configReference, @NonNull InputStream configStream) {
        return new EnvironmentConfigurationBuilder(environment)
            .resources()
            .resourceLoaders()
            .add(new TypedResourceLoader(
                configReference.getType(),
                new InMemoryResourceLoader(Collections.singletonMap(configReference.getPath(), () -> configStream))
            ))
            .and()
            .and()
            .build();
    }

    @Override
    public InputStream open(@NonNull String path) throws IOException {
        InputStream originalStream = delegate.open(path);
        ResourceReference configReference = ResourceReference.memory(path);
        Environment environment = new EnvironmentFactory().create(addConfigResourceLoader(
            this.environmentConfiguration,
            configReference,
            originalStream
        ));
        JtwigTemplate template = new JtwigTemplate(environment, configReference);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        template.render(model, baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }
}
