package com.hivemq.plugin.discovery.file;

import com.hivemq.spi.PluginEntryPoint;
import com.hivemq.spi.callback.registry.CallbackRegistry;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This entry point call is called by the module main class. Currently there is
 * not configuration happening.
 *
 *
 * @author Christian Rohmann, inovex GmbH
 */
public class FileDiscoveryPluginEntryPoint extends PluginEntryPoint {

    Logger log = LoggerFactory.getLogger(FileDiscoveryPluginEntryPoint.class);
    private final FileDiscoveryCallback discoveryCallback;

    @Inject
    public FileDiscoveryPluginEntryPoint(final FileDiscoveryCallback discoveryCallback) {
        log.debug("FileDiscoveryPluginEntryPoint constructor called");
        this.discoveryCallback = discoveryCallback;
    }

    /**
     * This method is executed after the instanciation of the whole class. It is
     * used to initialize the implemented callbacks and make them known to the
     * HiveMQ core.
     */
    @PostConstruct
    public void postConstruct() {
        log.debug("Registering discoveryCallback with HiveMQ CallbackRegistry");
        CallbackRegistry callbackRegistry = getCallbackRegistry();
        callbackRegistry.addCallback(discoveryCallback);

    }

}
