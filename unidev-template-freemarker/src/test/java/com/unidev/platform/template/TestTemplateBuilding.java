package com.unidev.platform.template;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;

import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;


@Slf4j
public class TestTemplateBuilding {

    @Test
    public void testTemplateBuildingNoVariables() {

        Template template = TemplateBuilder.newTemplate("Test template").build().get();

        String value = TemplateBuilder.evaluate(template, Map.of()).get();
        assertThat(value, not(isEmptyOrNullString()));
    }

    @Test
    public void testTemplateBuildingWithVariables() {
        Template freeMarkerTemplate = TemplateBuilder
                .newTemplate("template with ${variable} string")
                .build().get();

        String template = TemplateBuilder.evaluate(freeMarkerTemplate, Map.of("variable", "key666")).get();

        assertThat(template, not(isEmptyOrNullString()));
        assertThat(template, containsString("key666"));

        freeMarkerTemplate = TemplateBuilder
                .newTemplate("template with ${variable!} string").build().get();

        template = TemplateBuilder.evaluate(freeMarkerTemplate, Map.of()).get();

        assertThat(template, not(isEmptyOrNullString()));
        assertThat(template, not(containsString("variable")));
    }

    @Test
    public void testTemplateLoadingFromClassPath() {
        Template freeMarkerTemplate = TemplateBuilder
                    .newClassPathTemplate("template1.template")
                    .build().get();
        String template = TemplateBuilder.evaluate(freeMarkerTemplate, Map.of("variable", "value11")).get();
        assertThat(template, not(isEmptyOrNullString()));
        assertThat(template, containsString("value11"));
    }

    @Test
    public void testTemplateLoadingFromDirectory() throws IOException {
        File testFolder = FileUtils.getTempDirectory();
        File file = new File(testFolder, "test.ftl");
        FileUtils.writeStringToFile(file, "test template ${variable!}");
        Template freeMarkerTemplate = TemplateBuilder.newFilePathTemplate(testFolder.getAbsolutePath(), "test.ftl")
                .build().get();
        String template = TemplateBuilder.evaluate(freeMarkerTemplate, Map.of("variable", "value11")).get();
        assertThat(template, not(isEmptyOrNullString()));
        assertThat(template, containsString("value11"));
    }

    @Test
    public void testExceptionInTemplate() {

        TemplateBuilder templateBuilder = TemplateBuilder
            .newTemplate("template with *** ${variable} *** template-variable");
        templateBuilder.setExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
        Template freeMarkerTemplate = templateBuilder.build().get();
        String template = TemplateBuilder.evaluate(freeMarkerTemplate, Map.of()).get();

        log.info("Template output {}", template);

        assertThat(template, not(isEmptyOrNullString()));
        assertThat(template.contains("error"), is(false));
        assertThat(template.contains("template-variable"), is(true));
    }

}
