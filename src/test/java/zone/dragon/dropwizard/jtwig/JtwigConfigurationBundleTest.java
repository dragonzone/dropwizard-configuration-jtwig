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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import lombok.Data;

import static org.assertj.core.api.Java6Assertions.assertThat;

@ExtendWith(DropwizardExtensionsSupport.class)
public class JtwigConfigurationBundleTest {

    public static class TestApp extends Application<TestConfig> {
        @Override
        public void initialize(Bootstrap<TestConfig> bootstrap) {
            bootstrap.addBundle(new JtwigConfigurationBundle());
        }

        @Override
        public void run(TestConfig configuration, Environment environment) throws Exception {

        }
    }

    @Data
    public static class TestConfig extends Configuration {

        private String testProperty;

        private String testQuotedProperty;

        private String testUnquotedProperty;

        private int testIntegerProperty;
    }

    public static final DropwizardAppExtension<TestConfig> APP_RULE = new DropwizardAppExtension<>(TestApp.class, "src/test/resources/config.yaml");

    @Test
    public void testQuotedProperty() {
        TestConfig config = APP_RULE.getConfiguration();
        assertThat(config.getTestQuotedProperty()).isEqualTo("C:\\Users\\someuser\\testVar");
    }

    @Test
    public void testUnquotedProperty() {
        TestConfig config = APP_RULE.getConfiguration();
        assertThat(config.getTestUnquotedProperty()).isEqualTo("C:\\Users\\someuser\\testVar");
    }
}
