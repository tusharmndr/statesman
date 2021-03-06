package io.appform.statesman.engine.handlebars;

import com.github.jknack.handlebars.*;
import com.github.jknack.handlebars.context.FieldValueResolver;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.context.MethodValueResolver;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Singleton
public class HandleBarsService {

    private static final ValueResolver[] NOTIFY_VALUE_RESOLVERS = {
            JavaBeanValueResolver.INSTANCE,
            FieldValueResolver.INSTANCE,
            MapValueResolver.INSTANCE,
            JsonNodeValueResolver.INSTANCE,
            MethodValueResolver.INSTANCE
    };

    private Handlebars handlebars;
    private Map<String, Template> compiledTemplates;


    public HandleBarsService() {
        handlebars = new Handlebars();
        registerHelpers(handlebars);
        compiledTemplates = new ConcurrentHashMap<String, Template>();
    }

    public String transform(String template, Object data) {
        try {
            if (!compiledTemplates.containsKey(template)) {
                addTemplate(template);
            }
            return compiledTemplates.get(template).apply(Context.
                    newBuilder(data)
                    .build());
        } catch (Exception e) {
            //TODO add proper exception
            throw new RuntimeException();
        }
    }

    private void registerHelpers(Handlebars handlebars) {
        HandleBarsHelperRegistry.newInstance(handlebars).register();
    }

    private synchronized void addTemplate(String template) throws Exception {
        if (!compiledTemplates.containsKey(template)) {
            compiledTemplates.put(template, handlebars.compileInline(template));
        }
    }
}