package com.hivemq.plugin.discovery.file;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.hivemq.spi.callback.cluster.ClusterNodeAddress;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import static java.util.regex.Pattern.matches;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This callback will be registered with the HiveMQ callbackRegistry to be
 * called at cluster discovery intervals and to return a list read from a file
 *
 * @author Christian Rohmann, inovex GmbH
 */
public class FileDiscoveryCallback implements com.hivemq.spi.callback.cluster.ClusterDiscoveryCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileDiscoveryCallback.class);
    private static int ClusterPort; // all pods will use the same config and port
    private static String ServiceName;
    private List<ClusterNodeAddress> LastClusterNodes = new ArrayList<>();

    @Override
    public void init(String nodeid, ClusterNodeAddress cna) {
        LOGGER.info("This node runs on host " + cna.getHost() + " and listens on port " + cna.getPort() + ". The cluster-ID is " + nodeid);
        FileDiscoveryCallback.ClusterPort = cna.getPort();
        LOGGER.info("File-based cluster discovery plugin started!");
    }

    @Override
    public ListenableFuture<List<ClusterNodeAddress>> getNodeAddresses() {
        LOGGER.debug("List of cluster node addresses requested via ClusterDiscoveryCallback!");

        List<ClusterNodeAddress> ClusterNodes = new ArrayList<>();

        LOGGER.debug("Resolving all IPs of for hostname " + ServiceName);
        String clusterNodesFile = "conf/discovery/cluster-nodes";

        //read file into stream, try-with-resources
        try (Stream<String> stream = Files.lines(Paths.get(clusterNodesFile))) {

            List<ClusterNodeAddress> collect = stream.
                    map(line -> line.split(" ")[0]).
                    filter(firstColumn -> matches("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$", firstColumn)).
                    sorted().
                    map(ip -> new ClusterNodeAddress(ip.split(" ")[0], FileDiscoveryCallback.ClusterPort)).
                    collect(Collectors.toCollection(() -> ClusterNodes));

            String PreviousNodes = joinListOfNodes(LastClusterNodes);
            String CurrentNodes = joinListOfNodes(ClusterNodes);

            // Let's see if anything even changed
            if (PreviousNodes.equals(CurrentNodes)) {
                LOGGER.debug("List of cluster nodes did not change since last lookup  - " + CurrentNodes);
            } else {
                LOGGER.info("List of cluster nodes changed! Previous: {} || Current: {}", PreviousNodes, CurrentNodes);
                LastClusterNodes = ClusterNodes; // Remember the last set of nodes
            }

        } catch (IOException ex) {
                LOGGER.warn(ex.getMessage());
        }
        return Futures.immediateFuture(ClusterNodes);

    }

    @Override
    public void destroy() {
        LOGGER.debug("Destroying FileDiscoveryCallback.");
    }

    private String joinListOfNodes(List<ClusterNodeAddress> ClusterNodesToJoin) {
        return ClusterNodesToJoin.stream()
                .map(ClusterNodeAddress::getHost)
                .collect(Collectors.joining(", "));
    }

}
