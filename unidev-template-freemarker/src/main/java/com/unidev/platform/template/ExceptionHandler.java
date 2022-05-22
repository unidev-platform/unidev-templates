package com.unidev.platform.template;

import freemarker.core.Environment;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;

/**
 * Custom template exception handler
 */
@Slf4j
public class ExceptionHandler implements freemarker.template.TemplateExceptionHandler {
    public void handleTemplateException(TemplateException te, Environment env, java.io.Writer out)
        throws TemplateException {
        log.warn("Exception in template processing", te);
        throw te;
    }
}
