package org.duracloud.storageprovider.test;

//import com.google.common.collect.ImmutableSet;
//import com.google.inject.Module;
//import org.jclouds.ContextBuilder;
//import org.jclouds.io.payloads.InputStreamPayload;
//import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
//import org.jclouds.openstack.swift.SwiftApiMetadata;
//import org.jclouds.openstack.swift.SwiftClient;
//import org.jclouds.openstack.swift.domain.ContainerMetadata;
//import org.jclouds.openstack.swift.options.CreateContainerOptions;
//import org.jclouds.openstack.swift.options.ListContainerOptions;
//import org.jclouds.openstack.swift.v1.SwiftApi;
//import org.jclouds.openstack.swift.v1.features.ObjectApi;
//
//import java.io.FileInputStream;
//import java.io.InputStream;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;

/**
 * Tests the SDSC provider with JClouds. This class specifically tests the
 * use of the newer SwiftApi class which is currently in beta, but is planned
 * to be the primary interface for JClouds 2.0.
 *
 * As of 05/28/14, using JClouds 1.7.2, running this test results in a
 * "405 Method Not Allowed" exception. This is most likely because the new API
 * expects to authenticate against an openstack endpoint which supports
 * version 2.0 of the auth endpoint, which it would appear that SDSC does not
 * yet support. It's worth noting that the same calls made to Rackspace result
 * in a "401 Unauthorized" exception. It is not clear why that is the case, as
 * the credentials clearly work properly with the older client code.
 *
 * The contents of this class are commented out because the SwiftApi is not
 * available in older versions of JClouds. So this class will not compile with
 * JClouds 1.5.5, for example.
 *
 * @author Bill Branan
 *         Date: 5/28/14
 */
public class SDSCTestApi {
//
//    // ENTER AUTH URL HERE!
//    private static String authUrl =
//        "https://duracloud.auth.cloud.sdsc.edu/auth/v2.0";
//        //"https://auth.api.rackspacecloud.com/v2.0";
//
//    // ENTER CREDENTIALS HERE!
//    private String username = <REPLACE>;
//    private String password = <REPLACE>;
//
//    // UPDATE FILE PATH HERE!
//    private String filepath = "c:\\test.txt";
//
//    private SwiftClient getFreshSwiftClient() {
//        String trimmedAuthUrl = // JClouds expects authURL with no version
//            authUrl.substring(0, authUrl.lastIndexOf("/"));
//        Iterable<Module> modules =
//            ImmutableSet.<Module> of(new SLF4JLoggingModule());
//        return ContextBuilder.newBuilder(new SwiftApiMetadata())
//                              .endpoint(trimmedAuthUrl)
//                              .credentials(username, password)
//                              .modules(modules)
//                              // For JClouds 1.7.1 - 1.7.2
//                              .buildApi(SwiftClient.class);
//    }
//
//    private SwiftApi getFreshSwiftApi() {
//        Iterable<Module> modules =
//            ImmutableSet.<Module> of(new SLF4JLoggingModule());
//
//        return ContextBuilder.newBuilder(
//            new org.jclouds.openstack.swift.v1.SwiftApiMetadata())
//                .endpoint(authUrl)
//                .credentials(username, password)
//                .modules(modules)
//                .buildApi(SwiftApi.class);
//    }
//
//    public void testSwiftClient() throws Exception {
//        System.out.println("STARTING JCLOUDS TEST");
//        SwiftClient swiftClient = getFreshSwiftClient();
//
//        System.out.println("TEST: Get Containers");
//        // Get the list of containers
//        Set<ContainerMetadata> containers =
//            swiftClient.listContainers(new ListContainerOptions());
//        for(ContainerMetadata container : containers) {
//            System.out.println("  " + container.getName());
//        }
//
//        String containerName = "test-container-" + System.currentTimeMillis();
//        System.out.println("TEST: Create Container named " + containerName);
//        Map<String, String> properties = new HashMap<>();
//        CreateContainerOptions createContainerOptions =
//            CreateContainerOptions.Builder.withMetadata(properties);
//        swiftClient.createContainer(containerName, createContainerOptions);
//
//
//        String swiftapiContentId = putContentSwiftApi(containerName);
//
////        deleteContent(containerName, swift, blob);
//
//        deleteContent(containerName, swiftapiContentId);
//
//        System.out.println("TEST: DELETE Container named " + containerName);
//        swiftClient.deleteContainerIfEmpty(containerName);
//        System.out.println("  DELETE successful");
//
//        System.out.println("JCLOUDS TEST COMPLETE");
//    }
//
//    public String putContentSwiftApi(String containerName) throws Exception {
//        SwiftApi swiftApi = getFreshSwiftApi();
//        String contentName = "test-content-swift-" + System.currentTimeMillis();
//        System.out.println("TEST SWIFT API: PUT content named " + contentName);
//
//        ObjectApi objectApi =
//            swiftApi.objectApiInRegionForContainer("region", containerName);
//        InputStream input = new FileInputStream(filepath);
//        Map<String, String> metadata = new HashMap<>();
//        metadata.put("Content-Mimetype", "text/plain");
//
//        try {
//            String checksum = objectApi.replace(contentName,
//                                                new InputStreamPayload(input),
//                                                metadata);
//            System.out.println("  PUT successful, checksum: " + checksum);
//        } catch(Exception e) {
//            System.out.println("  PUT failed with error: " + e.getMessage());
//            e.printStackTrace();
//        }
//        return contentName;
//    }
//
//    // Attempts to delete content
//    public void deleteContent(String containerName, String... contentNames) {
//        SwiftClient swiftClient = getFreshSwiftClient();
//        for(String contentName : contentNames) {
//            System.out.println("TEST: DELETE content named " + contentName);
//            swiftClient.removeObject(containerName, contentName);
//            System.out.println("  DELETE successful");
//        }
//    }
//
//    // Start the ball rolling
//    public static void main(String[] args) throws Exception {
//        SDSCTestApi tester = new SDSCTestApi();
//        tester.testSwiftClient();
//        System.exit(0);
//    }

}
