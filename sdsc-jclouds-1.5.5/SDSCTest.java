import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.openstack.swift.SwiftApiMetadata;
import org.jclouds.openstack.swift.SwiftAsyncClient;
import org.jclouds.openstack.swift.SwiftClient;
import org.jclouds.openstack.swift.domain.ContainerMetadata;
import org.jclouds.openstack.swift.domain.MutableObjectInfoWithMetadata;
import org.jclouds.openstack.swift.domain.ObjectInfo;
import org.jclouds.openstack.swift.domain.SwiftObject;
import org.jclouds.openstack.swift.options.CreateContainerOptions;
import org.jclouds.openstack.swift.options.ListContainerOptions;
import org.jclouds.rest.RestContext;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;

import java.io.FileInputStream;
import java.io.InputStream;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Tests the SDSC provider with JClouds 1.5.5
 *
 * @author Bill Branan
 *         Date: 3/12/14
 */
public class SDSCTest {

    private static String authUrl =
        "https://duracloud.auth.cloud.sdsc.edu/auth/v1.0";
//        "https://auth.api.rackspacecloud.com/v1.0";

    // ENTER CREDENTIALS HERE!
    private String username = <REPLACE>;
    private String password = <REPLACE>;

    private SwiftClient getFreshSwiftClient() {
        String trimmedAuthUrl = // JClouds expects authURL with no version
            authUrl.substring(0, authUrl.lastIndexOf("/"));
        RestContext<SwiftClient, SwiftAsyncClient> context =
            ContextBuilder.newBuilder(new SwiftApiMetadata())
                          .endpoint(trimmedAuthUrl)
                          .credentials(username, password)
                          .build(SwiftApiMetadata.CONTEXT_TOKEN);
        return context.getApi();
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

    public void testSwiftClient() throws Exception {
        System.out.println("STARTING JCLOUDS 1.5.5 TEST");
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
                
        String swift = putContentSwift(containerName);
        String blob = putContentBlob(containerName);
        
        deleteContent(containerName, swift, blob);

        System.out.println("TEST: DELETE Container named " + containerName);
        swiftClient.deleteContainerIfEmpty(containerName);
        System.out.println("  DELETE successful");

        System.out.println("JCLOUDS 1.5.5 TEST COMPLETE");
    }

    // Attempts to push a file using the Swift client
    public String putContentSwift(String containerName) throws Exception {   
        SwiftClient swiftClient = getFreshSwiftClient();
        String contentName = "test-content-swift-" + System.currentTimeMillis();
        System.out.println("TEST SWIFT: PUT content named " + contentName);
        
        SwiftObject swiftObject = swiftClient.newSwiftObject();
        MutableObjectInfoWithMetadata objectInfoMetadata = swiftObject.getInfo();
        objectInfoMetadata.setName(contentName);
        InputStream input = new FileInputStream("c:\\test.txt");
        swiftObject.setPayload(input);
        
        try {
            String checksum = swiftClient.putObject(containerName, swiftObject);
            System.out.println("  PUT successful, checksum: " + checksum);
        } catch(Exception e) {
            System.out.println("  PUT failed with error: " + e.getMessage());
            e.printStackTrace();          
        }
        return contentName;
    }

    // Attempts to push a file using the BlobStore client
    public String putContentBlob(String containerName) throws Exception {
        BlobStore blobStore = getFreshBlobStore();
        String contentName = "test-content-blob-" + System.currentTimeMillis();
        System.out.println("TEST BLOB: PUT content named " + contentName);
        
        InputStream input = new FileInputStream("c:\\test.txt");
        Blob blobs = blobStore.blobBuilder(contentName).payload(input).build();
        
        try {
            String checksum = blobStore.putBlob(containerName, blobs);        
            System.out.println("  PUT successful, checksum: " + checksum);
        } catch(Exception e) {
            System.out.println("  PUT failed with error: " + e.getMessage());
            e.printStackTrace();
        }
        return contentName;
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
        SDSCTest tester = new SDSCTest();
        tester.testSwiftClient();
    }

}
