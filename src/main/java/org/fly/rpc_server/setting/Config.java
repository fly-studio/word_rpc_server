package org.fly.rpc_server.setting;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;

public class Config {
    @JsonProperty("log_dir") public File logDir;
    @JsonProperty("debug") public boolean debug;
}
