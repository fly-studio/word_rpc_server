package org.fly.rpc_server.setting;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;

public class Config {
    @JsonProperty("log_dir") public File logDir;
    public boolean debug;
    public Server server = new Server();

    public static class Server {
        public String host = "0.0.0.0";
        public int port = 8745;
    }
}
