10.3.1.4_test is a branch based upon the 10.3.1.4 code
(10.3 branch, revision 561794).

This is to allow modification of and building derbyTesting.jar in
order to run the 10.3.1.4 junit tests against later 10.3 releases.
This allows testing of the backwards compatability by having
the 10.3.1.4 tests mimic an users application.
Changes are only made in this branch to testing code.
Such changes are to allow error free test runs when Derby's
behaviour has changed in an acceptable manor.
Examples might be:
 - the expected SQL state thrown by a statement
 - fixes for test failures for tests not passing at the original revision
 - Removal of/changes to bug workarounds in the tests

Ideally changes would be 'svn merge'd in from testing changes
in the 10.3 branch. Changes should be resticted to java/testing
or other testing related files.

General idea is to build the jars with this codeline and then
run the junit tests with derbyTesting.jar from this codeline
and all the other jars from the later version or release candidate.

The tests can be run by running suites.All or through the junit targets.

Junit tests in theory should be easier to run against newer versions
as they do not rely on diff'ing output.
