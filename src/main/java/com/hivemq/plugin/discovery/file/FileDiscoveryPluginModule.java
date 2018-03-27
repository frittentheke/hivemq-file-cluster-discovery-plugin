package com.hivemq.plugin.discovery.file;

import com.hivemq.spi.HiveMQPluginModule;
import com.hivemq.spi.PluginEntryPoint;
import com.hivemq.spi.plugin.meta.Information;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the plugin module class, which handles the initialization and
 * configuration of the plugin.
 *
 * @author Christian Rohmann, inovex GmbH
 */
@Information(name = "HiveMQ File Cluster Discovery Plugin", author = "Christian Rohmann - inovex GmbH", version = "0.1", description = "Repeadedly read list of IPs from a file to then form a HiveMQ Cluster")
public class FileDiscoveryPluginModule extends HiveMQPluginModule {

    Logger log = LoggerFactory.getLogger(FileDiscoveryPluginModule.class);
    String hivemqClusterID;

    /**
     * This method is provided to execute some custom plugin configuration
     * stuff. Is is the place to execute Google Guice bindings,etc if needed.
     */
    @Override
    protected void configurePlugin() {
        log.debug("Configuration of FileDiscoveryPluginModule called!");
    }

    /**
     * This method returns the main class of our the plugin.
     *
     * @return callback priority
     */
    @Override
    protected Class<? extends PluginEntryPoint> entryPointClass() {
        return FileDiscoveryPluginEntryPoint.class;
    }
}
