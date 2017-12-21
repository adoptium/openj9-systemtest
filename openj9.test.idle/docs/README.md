openj9.test.idle
- This project contains a stress version of the Idle Micro Benchmark test which aims to test the Idle detection and management feature available in J9 vm. For more information on the feature and related -XX JVM options, visit : https://www.ibm.com/support/knowledgecenter/SSYKE2_8.0.0/com.ibm.java.lnx.80.doc/diag/appendixes/cmdline/commands_jvm_xx.html.
- The test has been written to have alternating active and idle cycles.
- Active cycles are periods of time when the test will perform CPU and Memory Intensive operations to consume CPU cycles and Heap Memory.
- Idle cycles are longer periods of time where the test sleeps/ performs no activity.
- Idle detection and management are enabled/disabled via newly added JVM options. 
- The Idle detection feature is available on all platforms whereas Idle management feature is supported only on Linux x86 platforms.
- The test exercises the newly added JVM options and checks if they behave as expected under stress conditions.

- There are 3 variations to this test 

1. MinIdleWaitTime 
- Tests the -XX:IdleTuningMinIdleWaitTime option. This is the minimum amount of time the application needs to be idle to be detected as Idle.
- Applicable on all platforms.
- Uses the following Java arguments while running the test 
	-XX:IdleTuningMinIdleWaitTime=180 -Xmx1024m -Xjit:verbose={compilePerformance},vlog=jitlog

2. GcOnIdle
- Tests the -XX:+IdleTuningGcOnIdle and when enabled, GC kicks in during the Idle cycle and reduces the footprint of the application. 
- Applicable only on Linux_x86-32 and Linux_x86-64 platforms as of now.
- Uses the following JVM arguments while running the test 
	-XX:+IdleTuningGcOnIdle -Xtune:virtualized -XX:IdleTuningMinIdleWaitTime=120 -Xmx1024m -verbose:gc -Xverbosegclog:gc.verbose -Xjit:verbose={compilePerformance},vlog=jitlog" $(LOG)

3. CompactOnIdle 
- Tests the -XX:+IdleTuningCompactOnIdle and when enabled, GC performs compaction on the heap during idle cycle to create contiguious free memory spaces for newer allocations.
- Applicable only on Linux_x86-32 and Linux_x86-64 platforms as of now.
- Uses the following JVM arguments while running the test
	-XX:+IdleTuningCompactOnIdle -Xtune:virtualized -XX:IdleTuningMinIdleWaitTime=120 -Xmx1024m -verbose:gc -Xverbosegclog:gc.verbose -Xjit:verbose={compilePerformance},vlog=jitlog

