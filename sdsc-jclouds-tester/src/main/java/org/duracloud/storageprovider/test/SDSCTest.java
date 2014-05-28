package org.duracloud.storageprovider.test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.swift.SwiftApiMetadata;
import org.jclouds.openstack.swift.SwiftClient;
import org.jclouds.openstack.swift.domain.ContainerMetadata;
import org.jclouds.openstack.swift.domain.MutableObjectInfoWithMetadata;
import org.jclouds.openstack.swift.domain.ObjectInfo;
import org.jclouds.openstack.swift.domain.SwiftObject;
import org.jclouds.openstack.swift.options.CreateContainerOptions;
import org.jclouds.openstack.swift.options.ListContainerOptions;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Tests the SDSC provider with JClouds
 *
 * @author Bill Branan
 *         Date: 3/12/14
 */
public class SDSCTest {

    // ENTER AUTH URL HERE!
    private static String authUrl =
        "https://duracloud.auth.cloud.sdsc.edu/auth/v1.0";
        //"https://auth.api.rackspacecloud.com/v1.0";

    // ENTER CREDENTIALS HERE!
    private String username = <REPLACE>;
    private String password = <REPLACE>;

    // UPDATE FILE PATH HERE!
    private String filepath = "c:\\test.txt";

    private SwiftClient getFreshSwiftClient() {
        String trimmedAuthUrl = // JClouds expects authURL with no version
            authUrl.substring(0, authUrl.lastIndexOf("/"));
        Iterable<Module> modules = 
            ImmutableSet.<Module> of(new SLF4JLoggingModule());            
        return ContextBuilder.newBuilder(new SwiftApiMetadata())
                              .endpoint(trimmedAuthUrl)
                              .credentials(username, password)
                              .modules(modules)
                              // For JClouds 1.7.1 - 1.7.2
//                              .buildApi(SwiftClient.class);
                              // For JClouds 1.5.5
                              .build(SwiftApiMetadata.CONTEXT_TOKEN).getApi();
    }

    private BlobStore getFreshBlobStore() {
        String trimmedAuthUrl = // JClouds expects authURL with no version
            authUrl.substring(0, authUrl.lastIndexOf("/"));
        return ContextBuilder.newBuilder(new SwiftApiMetadata())
                              .endpoint(trimmedAuthUrl)
                              .credentials(username, password)
                              .buildView(BlobStoreContext.class)
                              .getBlobStore();
    }

    public void testSwiftClient(String contentName) throws Exception {
        System.out.println("STARTING JCLOUDS TEST");
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

        String swiftContentName = "swift-" + contentName;
        String blobContentName = "blob-" + contentName;

        putContentSwift(containerName, swiftContentName);
        putContentBlob(containerName, blobContentName);

        listContent(containerName);

        deleteContent(containerName, swiftContentName, blobContentName);

        System.out.println("TEST: DELETE Container named " + containerName);
        swiftClient.deleteContainerIfEmpty(containerName);
        System.out.println("  DELETE successful");

        System.out.println("JCLOUDS TEST COMPLETE");
    }

    // Attempts to push a file using the Swift client
    public void putContentSwift(String containerName, String contentName)
        throws Exception {
        SwiftClient swiftClient = getFreshSwiftClient();
        System.out.println("TEST SWIFT: PUT content named " + contentName);

        SwiftObject swiftObject = swiftClient.newSwiftObject();
        MutableObjectInfoWithMetadata objectInfoMetadata = swiftObject.getInfo();
        objectInfoMetadata.setName(contentName);
        InputStream input = new FileInputStream(filepath);
        swiftObject.setPayload(input);

        try {
            String checksum = swiftClient.putObject(containerName, swiftObject);
            System.out.println("  PUT successful, checksum: " + checksum);
        } catch(Exception e) {
            System.out.println("  PUT failed with error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Attempts to push a file using the BlobStore client
    public void putContentBlob(String containerName, String contentName)
        throws Exception {
        BlobStore blobStore = getFreshBlobStore();

        System.out.println("TEST BLOB: PUT content named " + contentName);

        InputStream input = new FileInputStream(filepath);
        Blob blobs = blobStore.blobBuilder(contentName).payload(input).build();

        try {
            String checksum = blobStore.putBlob(containerName, blobs);
            System.out.println("  PUT successful, checksum: " + checksum);
        } catch(Exception e) {
            System.out.println("  PUT failed with error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Attempts to list all content in a container
    public void listContent(String containerName) {
        SwiftClient swiftClient = getFreshSwiftClient();

        try {
            Set<ObjectInfo> objectList =
                swiftClient.listObjects(containerName, new ListContainerOptions());
            Iterator<ObjectInfo> objectInfoIterator = objectList.iterator();
            System.out.println("TEST: Get List of Content in Container");
            while(objectInfoIterator.hasNext()) {
                ObjectInfo objectInfo = objectInfoIterator.next();
                System.out.println("   " + objectInfo.getName());
            }
        } catch(Exception e) {
            System.out.println("  FAILED to list container contents " +
                               "due to error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Attempts to delete content
    public void deleteContent(String containerName, String... contentNames) {
        SwiftClient swiftClient = getFreshSwiftClient();
        for(String contentName : contentNames) {
            System.out.println("TEST: DELETE content named " + contentName);
            swiftClient.removeObject(containerName, contentName);
            System.out.println("  DELETE successful");
        }
    }

    // Start the ball rolling
    public static void main(String[] args) throws Exception {
        String contentName = "test-content-" + System.currentTimeMillis();
        SDSCTest tester = new SDSCTest();
        tester.testSwiftClient(contentName);
        System.exit(0);
    }

}
