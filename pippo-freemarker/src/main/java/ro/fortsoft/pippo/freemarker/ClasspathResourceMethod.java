/*
 * Copyright (C) 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ro.fortsoft.pippo.freemarker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.fortsoft.pippo.core.PippoRuntimeException;
import ro.fortsoft.pippo.core.route.ClasspathResourceHandler;
import ro.fortsoft.pippo.core.route.UrlBuilder;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import ro.fortsoft.pippo.core.route.WebjarsResourceHandler;

/**
 * Base class for handling classpath resource url generation from a Freemarker template.
 *
 * @author James Moger
 * @param <X>
 */
abstract class ClasspathResourceMethod<X extends ClasspathResourceHandler> implements TemplateMethodModelEx {

    public final Logger log = LoggerFactory.getLogger(getClass());

    final UrlBuilder urlBuilder;

    final Class<X> resourceHandlerClass;

    final AtomicReference<String> urlPattern;

    public ClasspathResourceMethod(UrlBuilder urlBuilder, Class<X> resourceHandlerClass) {
        this.urlBuilder = urlBuilder;
        this.resourceHandlerClass = resourceHandlerClass;
        this.urlPattern = new AtomicReference<>(null);
    }

    @Override
    public TemplateModel exec(List args) throws TemplateModelException {

        if (urlPattern.get() == null) {
            String pattern = urlBuilder.urlPatternFor(resourceHandlerClass);
            if (pattern == null) {
                throw new PippoRuntimeException("You must register a route for {}",
                        resourceHandlerClass.getSimpleName());
            }

            urlPattern.set(pattern);
        }

        String path = args.get(0).toString();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ClasspathResourceHandler.PATH_PARAMETER, path);
        String url = urlBuilder.urlFor(urlPattern.get(), parameters);
        return new SimpleScalar(url);

    }

}