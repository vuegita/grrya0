package com.inso.framework.spring.exception;

import java.io.Writer;

import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.StringUtils;

import freemarker.core.Environment;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class FreemarkerException implements TemplateExceptionHandler {
    private static final Log LOG = LogFactory.getLog(FreemarkerException.class);

    @Override
    public void handleTemplateException(TemplateException te, Environment env, Writer out) throws TemplateException {
    	LOG.error("Freemark error:", te);
        try {
        	out.write(StringUtils.getEmpty());
        } catch (Exception e) {
        }
    }

}
