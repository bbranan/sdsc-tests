sdsc-tests
==========

Tests which work against the SDSC cloud storage system.

There are two tests available in this set, one which
connects to SDSC Cloud using the JClouds client, and
one which connects using the FilesClient provided by
Rackspace (which is no longer supported).


build
=====

Select a test (either JClouds or FilesClient) and cd
into the appropriate directory.

Run "ant"

This will perform a build


run
===

Run "ant run"

This will execute the test