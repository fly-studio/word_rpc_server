package org.fly.rpc_server.setting;

import com.sun.istack.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.fly.core.text.json.Jsonable;
import org.fly.rpc_server.exception.ConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Setting {
    public static String ETC_PATH = null;

    static {
        String etcPath = System.getProperty("rpc_server.etc.path");
        if (etcPath != null && etcPath.length() != 0)
            ETC_PATH = etcPath;
        else
            ETC_PATH = new File(System.getProperty("user.dir"), "etc").getAbsolutePath();

        System.setProperty("HANLP_ROOT", System.getProperty("user.dir"));
    }

    public final static String CONFIG_FILE = "config.json";

    public final static Logger logger = LoggerFactory.getLogger(Setting.class);
    public static Config config;

    public static File getEtcPath()
    {
        return new File(ETC_PATH);
    }

    public static File getEtcPath(@NotNull String filename)
    {
        return new File(getEtcPath(), filename);
    }

    public static File getEtcPath(@NotNull File file)
    {
        return file.isAbsolute() ? file : getEtcPath(file.getPath());
    }

    public static Config getConfig() throws Exception
    {
        return getConfig(getEtcPath(CONFIG_FILE));
    }

    public static Config getConfig(File file) throws Exception
    {
        if (Setting.config != null)
            return Setting.config;

        Config config;

        try {
            config = Jsonable.fromJson(Config.class, file);
        } catch (Exception e) {
            throw new ConfigException("\"" + CONFIG_FILE + "\" JSON format ERROR.", e);
        }

        if (config.logDir == null) {
            config.logDir = getEtcPath("logs");
            logger.warn("Invaid [log_dir] in \"{}\", Redirect path to default.", CONFIG_FILE);
        }

        config.logDir = getEtcPath(config.logDir);
        System.setProperty("rpc_server.log.path", config.logDir.getAbsolutePath());

        // locate to user's log4j2.xml
        LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
        context.setConfigLocation(getEtcPath("log4j2.xml").toURI());

        Setting.config = config;

        logger.info("The [log] directory locate to {}", config.logDir.getAbsolutePath());

        logger.info("Loaded {}.", CONFIG_FILE);

        return config;
    }

    public static void readSettings() throws Exception
    {
        getConfig();
    }
}
