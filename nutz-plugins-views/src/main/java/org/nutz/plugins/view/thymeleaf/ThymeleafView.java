package org.nutz.plugins.view.thymeleaf;

import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.view.AbstractPathView;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

public class ThymeleafView extends AbstractPathView {

    private static final Log log = Logs.get();

    private ThymeleafProperties properties;

    public ThymeleafView(ThymeleafProperties properties, String path) {
        super(path);
        this.properties = properties;
    }

    @Override
    public void render(HttpServletRequest request, HttpServletResponse response, Object value) throws Exception {
        String path = evalPath(request, value);
        response.setContentType(properties.getContentType() + "; charset=" + properties.getEncoding());
        response.setCharacterEncoding(properties.getEncoding());
        try {
            org.nutz.lang.util.Context ctx = super.createContext(request, value);
            IContext context = new Context(Locale.getDefault(), ctx.getInnerMap());
            TemplateEngine templateEngine = new TemplateEngine();
            templateEngine.setTemplateResolver(initializeTemplateResolver(properties));
            templateEngine.process(path, context, response.getWriter());
        }
        catch (Exception e) {
            log.error("模板引擎错误", e);
            throw e;
        }
    }

    private ServletContextTemplateResolver initializeTemplateResolver(ThymeleafProperties properties) {
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(Mvcs.getServletContext());

        templateResolver.setTemplateMode(properties.getMode());
        templateResolver.setPrefix(properties.getPrefix());
        templateResolver.setSuffix(properties.getSuffix());
        templateResolver.setCharacterEncoding(properties.getEncoding());
        templateResolver.setCacheable(properties.isCache());
        templateResolver.setCacheTTLMs(properties.getCacheTTLMs());

        return templateResolver;
    }
}
