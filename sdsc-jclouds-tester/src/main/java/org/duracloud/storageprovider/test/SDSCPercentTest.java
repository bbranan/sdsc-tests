/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.storageprovider.test;

/**
 * Tests the SDSC provider with specific content that includes a percent symbol
 * in the content ID.
 *
 * @author Bill Branan
 *         Date: 5/28/14
 */
public class SDSCPercentTest {

    /**
     * Tests the SDSC connection when the added content item includes
     * a percent sign.
     *
     * @param args an empty set of arguments
     * @throws Exception if there is a major issue
     */
    public static void main(String[] args) throws Exception {
        String contentName = "test-content-%x-" + System.currentTimeMillis();
        SDSCTest tester = new SDSCTest();
        tester.testSwiftClient(contentName);
        System.exit(0);
    }

}
