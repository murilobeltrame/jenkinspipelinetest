import groovy.testSupport.PipelineSpockTestBase
import groovy.mocks.module_Artifact
import groovy.mocks.module_Notification

class defaultPipelineTest extends PipelineSpockTestBase {

    def script
    def artifactMock
    def mavenMock
    def notificationMock

    def setup() {
        registerMocks()
        registerPluginMethods()
        script = loadScript('vars/defaultUtilityPipeline.groovy')
    }

    def cleanup() {
        printCallStack()
    }

    void 'Happy flow'() {
        given:
        def junitMock = Mock(Closure)
        helper.registerAllowedMethod('junit', [HashMap.class], junitMock)
        when:
        script.call([:])
        then:
        1 * mavenMock.call('clean verify')
        1 * junitMock.call(_)
        1 * artifactMock.publish()
        1 * notificationMock.sendEmail(_)
        assertJobStatusSuccess()
    }

    void 'Rainy day'() {
        given:
        def junitMock = Mock(Closure)
        helper.registerAllowedMethod('junit', [HashMap.class], junitMock)
        when:
        script.call([:])
        then:
        1 * mavenMock.call(_) >> {
            binding.getVariable('currentBuild').result = 'FAILURE'
        }
        1 * junitMock.call(_)
        0 * artifactMock.publish()
        1 * notificationMock.sendEmail(_)
        assertJobStatusFailure()
    }

    void 'A Maven failure should still interpret the junit test report'() {
        given:
        def junitMock = Mock(Closure)
        helper.registerAllowedMethod('junit', [HashMap.class], junitMock)
        when:
        script.call([:])
        then:
        1 * mavenMock.call('clean verify') >> { binding.getVariable('currentBuild').result = 'FAILURE' }
        1 * junitMock.call(_)
        assertJobStatusFailure()
    }

    void 'Send notification when status of Maven call changes'() {
        given:
        def junitMock = Mock(Closure)
        helper.registerAllowedMethod('junit', [HashMap.class], junitMock)

        and:
        binding.getVariable('currentBuild').previousBuild.result = 'FAILED'

        when:
        script.call([:])

        then:
        1 * mavenMock.call('clean verify')
        1 * junitMock.call(_)
        2 * notificationMock.sendEmail(_)
        assertJobStatusSuccess()
    }

    def registerMocks() {
        mavenMock = Mock(Closure)
        helper.registerAllowedMethod('module_Maven', [String.class], mavenMock)

        artifactMock = Mock(module_Artifact)
        binding.setVariable('module_Artifact', artifactMock)

        notificationMock = Mock(module_Notification)
        binding.setVariable('module_Notification', notificationMock)
    }

    def registerPluginMethods() {
        helper.registerAllowedMethod('junit', [HashMap.class], null)
    }
}
