/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.openstack.swift.SwiftApiMetadata;
import org.jclouds.openstack.swift.SwiftClient;
import org.jclouds.openstack.swift.domain.ContainerMetadata;
import org.jclouds.openstack.swift.domain.MutableObjectInfoWithMetadata;
import org.jclouds.openstack.swift.domain.ObjectInfo;
import org.jclouds.openstack.swift.domain.SwiftObject;
import org.jclouds.openstack.swift.options.CreateContainerOptions;
import org.jclouds.openstack.swift.options.ListContainerOptions;
import org.jclouds.rest.RestContext;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Tests the SDSC provider
 *
 * @author Bill Branan
 *         Date: 3/11/14
 */
public class SDSCTest {

    private static String authUrl =
        "https://duracloud.auth.cloud.sdsc.edu/auth/v1.0";
    
    private int plannedAttempts = 5;

    // ENTER CREDENTIALS HERE!
    private String username = <REPLACE>;
    private String password = <REPLACE>;
    
    private SimpleDateFormat dateFormat = 
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");

    private SwiftClient getFreshSwiftClient() {
        String trimmedAuthUrl = // JClouds expects authURL with no version
            authUrl.substring(0, authUrl.lastIndexOf("/"));
        return ContextBuilder.newBuilder(new SwiftApiMetadata())
                             .endpoint(trimmedAuthUrl)
                             .credentials(username, password)
                             .buildApi(SwiftClient.class);
    }

    public void testSwiftClient() throws Exception {
        System.out.println("STARTING JCLOUDS 1.7.1 TEST");

        SwiftClient swiftClient = getFreshSwiftClient();

        System.out.println("TEST: Get Containers");
        // Get the list of containers
        Set<ContainerMetadata> containers =
            swiftClient.listContainers(new ListContainerOptions());
        for(ContainerMetadata container : containers) {
            System.out.println("  " + container.getName());
        }
        
        String containerName = "test-container-" + System.currentTimeMillis();
        System.out.println("TEST: Create Container named " + containerName);
        Map<String, String> properties = new HashMap<>();
        CreateContainerOptions createContainerOptions =
            CreateContainerOptions.Builder.withMetadata(properties);
        swiftClient.createContainer(containerName, createContainerOptions);
        
        String contentName = "test-content-" + System.currentTimeMillis();
        System.out.println("TEST: PUT content named " + contentName);
        SwiftObject swiftObject = swiftClient.newSwiftObject();
        MutableObjectInfoWithMetadata objectInfoMetadata = swiftObject.getInfo();
        objectInfoMetadata.setName(contentName);
        swiftObject.setPayload(contentName);
        String checksum = swiftClient.putObject(containerName, swiftObject);
        System.out.println("PUT successful, checksum: " + checksum);
        
        System.out.println("TEST: DELETE content named " + contentName);
        swiftClient.removeObject(containerName, contentName);        

        System.out.println("TEST: Delete Container named " + containerName);
        swiftClient.deleteContainerIfEmpty(containerName);

        System.out.println("JCLOUDS 1.7.1 TEST COMPLETE");
    }

    // Start the ball rolling    
    public static void main(String[] args) throws Exception {
        SDSCTest tester = new SDSCTest();
        tester.testSwiftClient();
    }

}
