package br.com.esign.postdenuncia.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import br.com.esign.postdenuncia.dao.HibernateUtil;
import br.com.esign.postdenuncia.dao.UltimaMedicaoCache;
import br.com.esign.postdenuncia.etl.FonteTimer;
import br.com.esign.postdenuncia.util.Global;

/**
 * Application Lifecycle Listener implementation class AppLifecycleListener
 *
 */
@WebListener
public class AppLifecycleListener implements ServletContextListener {

    private final String[] timers = {"CETESB", "INEA", "SMAC", "IAP", "FEAM", "IEMA", "INEMA"};

    /**
     * @param event
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        HibernateUtil.getSessionFactory(); // Just call the static initializer of that class 

        UltimaMedicaoCache.getCache();

        Global global = Global.getInstance();
        String imagesFolder = event.getServletContext().getInitParameter("imagesFolder");
        global.setImagesFolder(imagesFolder);

        for (int i = 0, j = timers.length; i < j; i++) {
            FonteTimer timer = global.getFonteTimerInstance(timers[i]);
            timer.start();
        }
    }

    /**
     * @param event
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        Global global = Global.getInstance();
        for (int i = 0, j = timers.length; i < j; i++) {
            FonteTimer timer = global.getFonteTimerInstance(timers[i]);
            timer.stop();
        }

        HibernateUtil.getSessionFactory().close(); // Free all resources
    }

}