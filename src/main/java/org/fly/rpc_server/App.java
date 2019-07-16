package org.fly.rpc_server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.fly.rpc_server.server.Server;
import org.fly.rpc_server.setting.Setting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 *
 */
public class App 
{
    private final static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main( String[] args )
    {
        try {
            Setting.readSettings();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return;
        }

        setGlobalUncaughtExceptionHandler();

        Server server = new Server("0.0.0.0", 8745);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.trace("Shutdown from Runtime hook.");

            if( LogManager.getContext() instanceof LoggerContext) {
                logger.debug("Shutting down log4j2");
                Configurator.shutdown((LoggerContext)LogManager.getContext());
            } else
                logger.warn("Unable to shutdown log4j2");

            server.stop();

        },"Shutdown-Thread"));

        server.start();
    }

    private static void setGlobalUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler((Thread t, Throwable e)  -> {
            logger.error("UnCaughtException", e);
        });
    }


}
