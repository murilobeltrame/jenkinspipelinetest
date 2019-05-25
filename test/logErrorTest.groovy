// test/logErrorTest.groovy
// Import the required stuff: JUnit &amp; Jenkins Pipeline Unit
import org.junit.*
import static org.junit.Assert.*
import com.lesfurets.jenkins.unit.*

// Extend the BasePipelineTest to use the Jenkins Pipeline Unit framework
class logErrorTest extends BasePipelineTest {
    // The class under test
    def logError
 
    // Before every testcase is run, do this:
    @Before
    void setUp() {
        super.setUp()
        // Load the script, without executing it.
        logError = loadScript("vars/logError.groovy")
    }
 
    // This is our testcase!
    @Test
    void 'Log message to console with "ERROR" prepended'() {
        // Execute the 'call' method on our class under test
        logError.call("message")
        // Validate that echo is only called once
        assertEquals(1, helper.methodCallCount('echo'))
        // Validate that the call to echo contains the string "ERROR"
        assertTrue(helper.getCallStack()[1].args[0].toString().contains("ERROR"))
        // print the complete callstack to the console for good measure
        printCallStack()
    }
 
}