package org.cghr.contextStartup

import javax.servlet.ServletContext
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener

/**
 * Created by ravitej on 30/1/15.
 */
class AppStartupListener implements ServletContextListener {


    @Override
    void contextInitialized(ServletContextEvent sce) {

        ServletContext servletContext = sce.getServletContext();
        String basepath = getRealPath(servletContext)
        System.setProperty("basePath", basepath);
    }

    String getRealPath(ServletContext servletContext) {

        String path = servletContext.getRealPath("/")
        resolvePath(path)

    }

    String resolvePath(String path) {
        path.endsWith("/") ? path : path + "/"
    }

    @Override
    void contextDestroyed(ServletContextEvent sce) {

    }
}
