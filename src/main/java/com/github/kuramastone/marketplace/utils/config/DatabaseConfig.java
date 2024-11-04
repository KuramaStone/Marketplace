package com.github.kuramastone.marketplace.utils.config;

import com.github.kuramastone.bUtilities.YamlConfig;

public class DatabaseConfig {

    @YamlConfig.YamlKey("connection")
    private String connectionString;
    @YamlConfig.YamlKey("database name")
    private String dbName;
    @YamlConfig.YamlKey("refresh rate")
    private int refreshRate;

    public String getConnectionString() {
        return connectionString;
    }

    public String getDatabaseName() {
        return dbName;
    }

    public long getRefreshRate() {
        return (long) refreshRate;
    }
}
