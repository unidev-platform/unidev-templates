package com.unidev.platform.template;


import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Class to build output from template
 */
@Slf4j
@Data
@Builder
@AllArgsConstructor
public class TemplateBuilder {

    private static final ExceptionHandler EXCEPTION_HANDLER = new ExceptionHandler();

    private enum TemplateBuilderMode {STRING, CLASSPATH, FILE}

    @Builder.Default private TemplateBuilderMode mode = TemplateBuilderMode.STRING;
    private String template;
    private String templateLocation;

    @Setter
    @Getter
    private freemarker.template.TemplateExceptionHandler exceptionHandler;

    /**
     * New template with template
     */
    public static TemplateBuilder newTemplate(String template) {
        return TemplateBuilder.builder().template(template).build();
    }

    /**
     * Create classpath template builder, template field will be used for classpath file
     */
    public static TemplateBuilder newClassPathTemplate(String classPathTemplate) {
        return TemplateBuilder.builder().template(classPathTemplate)
            .mode(TemplateBuilderMode.CLASSPATH).build();
    }

    /**
     * Create template wthich is loading from  path
     */
    public static TemplateBuilder newFilePathTemplate(String location, String template) {
        return TemplateBuilder.builder().templateLocation(location).template(template)
            .mode(TemplateBuilderMode.FILE).build();
    }

    /**
     * Default constructor
     */
    public TemplateBuilder() {
        this.template = "";
    }

    /**
     * Constructor with template
     */
    public TemplateBuilder(String template) {
        this.template = template;
    }

    /**
     * Add template body string
     */
    public TemplateBuilder addTemplate(String template) {
        this.template = template;
        return this;
    }

    /**
     * Add location from where to load stuff
     */
    public TemplateBuilder addTemplateLocation(String templateLocation) {
        this.templateLocation = templateLocation;
        return this;
    }

    /**
     * Build template
     *
     * @return May return null if something crashed inside...
     */
    public Optional<Template> build() {
        Configuration configuration = new Configuration(
            Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        configuration.setTemplateExceptionHandler(
            exceptionHandler == null ? EXCEPTION_HANDLER : exceptionHandler);
        configuration.setLogTemplateExceptions(false);
        if (StringUtils.isNotBlank(templateLocation)) {
            try {
                configuration.setDirectoryForTemplateLoading(new File(templateLocation));
            } catch (Exception e) {
                log.error("Failed set template location", e);
                return Optional.empty();
            }
        }
        switch (mode) {
            case STRING:
                try {
                    return Optional.of(new Template("", template, configuration));
                } catch (Exception e) {
                    log.error("Failed to load string template", e);
                    return Optional.empty();
                }
            case CLASSPATH:
                configuration
                    .setClassLoaderForTemplateLoading(this.getClass().getClassLoader(), "/");
                try {
                    return Optional.ofNullable(configuration.getTemplate(template));
                } catch (Exception e) {
                    log.error("Failed to load classpath template", e);
                    return Optional.empty();
                }
            case FILE:
                try {
                    return Optional.ofNullable(configuration.getTemplate(template));
                } catch (IOException e) {
                    log.error("Failed to load file template", e);
                    return Optional.empty();
                }
        }
        return Optional.empty();
    }

    public static Optional<String> evaluate(Template freemarkerTemplate, Map<String, Object> variables) {
        StringWriter stringWriter = new StringWriter();
        try {
            freemarkerTemplate.process(variables, stringWriter);
        } catch (Exception e) {
            log.error("Failed to generate template", e);
        }
        return Optional.of(stringWriter.toString());
    }

}
