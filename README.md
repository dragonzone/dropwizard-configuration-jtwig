# Dropwizard Jtwig Templates Configuration [![Build Status](https://jenkins.dragon.zone/buildStatus/icon?job=dragonzone/dropwizard-configuration-jtwig/master)](https://jenkins.dragon.zone/blue/organizations/jenkins/dragonzone%2Fdropwizard-configuration-jtwig/activity?branch=master) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/zone.dragon/dropwizard-configuration-jtwig/badge.svg)](https://maven-badges.herokuapp.com/maven-central/zone.dragon/dropwizard-configuration-jtwig/)

This bundle enables jtwig template processing of an applications `config.yaml` file, making it easy to interpolate environmental
variables, system properties, and other boot-time properties (such as passwords and secrets) into your application config, as well as 
making structural changes and alterations to the configuration based on settings such as the OS type or other environmental cues.

To use this bundle, add it to your application in the initialize method:

```java
@Override
public void initialize(Bootstrap<YourConfig> bootstrap) {
    bootstrap.addBundle(new JtwigConfigurationBundle());
}
```

The initial data model contains all environmental variables under the `env` key, such as `{{ env.PATH }}` or `{{ env.HOME }}`, and all
system properties under the `system` key, such as `{{ system["user.dir"] }}`

### Escaping

By default, any strings will be escaped, so interpolations of strings should be *double-quoted*:

```
homeDir: "{{ env.HOME }}"
```

to insert an unescaped string, use the `none` format with the `autoescape` operator:

```
{% autoescape 'none' %}
homeDir: {{ env.HOME }}
{% endautoescape %}
```

### Customizing Template Rendering

To customize the variables available inside the template, use one of the constructor overloads to pass in the desired model. This makes it
easy to inject secrets or other startup properties into your configuration. For more advanced customizations, the `EnvironmentConfiguration`
itself can be provided for use.

**Note:** When providing a custom data model or environment configuration, no defaults will be added; If you still want support for the
environment variables, system properties, or YAML-compatible string escaping, you must make sure to include them in the inputs provided to
the plugin.