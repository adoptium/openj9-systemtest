# openj9.test.sharedClasses.jvmti

The openj9.test.sharedclasses.jvmti project tests the IBM Java shared classes APIs.

Both the Java APIs and the JVMTI interfaces are tested.  The JVMTI interface is tested via a native ('C' language) Java agent.

The test are automated by the SharedClassesAPI stf test.

To run the SharedClassesAPI test, run

```
perl <stf-root>/stf.core/scripts/stf.pl -test-root="<openj9-systemtest-root>" -test="SharedClassesAPI"
```

