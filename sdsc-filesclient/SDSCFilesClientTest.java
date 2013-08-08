/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
import com.rackspacecloud.client.cloudfiles.FilesClient;
import com.rackspacecloud.client.cloudfiles.FilesContainer;
import com.rackspacecloud.client.cloudfiles.FilesException;
import com.rackspacecloud.client.cloudfiles.FilesObject;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Tests the SDSC provider
 *
 * @author Bill Branan
 *         Date: 8/01/13
 */
public class SDSCFilesClientTest {

    private static String authUrl =
        "https://duracloud.auth.cloud.sdsc.edu/auth/v1.0";

    private int plannedAttempts = 1;
    
    // ENTER CREDENTIALS HERE!
    private String username = <REPLACE>;
    private String password = <REPLACE>;

    private FilesClient getFreshFilesClient() throws Exception {
        FilesClient filesClient = new FilesClient(username, password, authUrl);
        if (!filesClient.login()) {
            throw new Exception("FilesClient login failed!");
        }
        return filesClient;
    }

    public void testFilesClient() throws Exception {
        System.out.println("STARTING FILES CLIENT ITERATION TEST");

        int iterationCompletedCount = 0;
        for(int i=0; i<plannedAttempts; i++) {
            try {
                iterateThroughContentFilesClient(getFreshFilesClient());
                iterationCompletedCount++;
            } catch(Exception e) {
                System.out.println("Iteration failure on attempt: " + i +
                                   ". Error message: " + e.getMessage());
            }
        }
        System.out.println("Files Client iteration attempts: " + plannedAttempts +
                           "; Iterations completed: " + iterationCompletedCount);

        System.out.println("FILES CLIENT ITERATION TEST COMPLETE");
    }

    private void iterateThroughContentFilesClient(FilesClient filesClient) {
        long spaceCount = 0;
        long errorCount = 0;        

        // Get the list of containers
        try {
            List<FilesContainer> containers = filesClient.listContainers();

            // Loop through each contaner
            for (FilesContainer container : containers) {
                long contentCount = 0;

                spaceCount++;
                String containerId = container.getName();
                System.out.println("Counting container: " + containerId);

                String marker = null;
                try {
                    List<FilesObject> contentItems =
                        filesClient.listObjects(containerId, null, 1000, marker);

                    // Loop through each content item
                    while(null != contentItems && contentItems.size() > 0) {
                        for(FilesObject contentItem : contentItems) {
                            String contentId = contentItem.getName();
                            contentCount++;
                            System.out.println("Item: " + contentCount);
                            try {
                                // Get the metadata for the content item
                                filesClient.getObjectMetaData(containerId, contentId);
                            } catch(FilesException e) {
                                errorCount++;
                                System.out.println("Exception getting content: " +
                                                   e.getMessage() + ", " +
                                                   e.getHttpStatusLine() +
                                                   ". Root Cause: " + getRootCause(e));
                            } catch(Exception e) {
                                errorCount++;
                                System.out.println("Exception getting content: " +
                                                   e.getMessage() +
                                                   ". Root Cause: " + getRootCause(e));
                            }
                            marker = contentId;
                        }
                        // Get the next set of items
                        contentItems =
                            filesClient.listObjects(containerId, null, 1000, marker);
                    }
                } catch(Exception e) {
                    errorCount++;
                    System.out.println("Exception getting object list: " +
                                       e.getMessage());
                }
                printCount(spaceCount, contentCount, errorCount);
            }
        } catch(Exception e) {
            errorCount++;
            System.out.println("Exception getting container list: " +
                   e.getMessage());
        }
    }

    // Print status
    private void printCount(long spaceCount, long contentCount, long errorCount) {
        System.out.println("Space count: " + spaceCount);
        System.out.println("Content count:" + contentCount);
        System.out.println("Error count:" + errorCount);
    }
    
    private String getRootCause(Throwable t) {
        while(t.getCause() != null) {
            t = t.getCause();
        }
        return t.getMessage();
    }    

    // Start the ball rolling    
    public static void main(String[] args) throws Exception {
        SDSCFilesClientTest tester = new SDSCFilesClientTest();
        tester.testFilesClient();
    }

}
