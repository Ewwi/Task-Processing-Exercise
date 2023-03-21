package other.task.monitoring

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalManagementPort
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ThreadPoolMetricsSpec extends Specification {
    @Autowired
    TestRestTemplate restTemplate

    @LocalManagementPort
    String managementPort

    def "should expose /actuator/metrics/#expectedMetric metric endpoint"() {
        given:
        def metricsUrl = "http://localhost:${managementPort}/actuator/metrics/"

        when:
        def actual = restTemplate.getForObject(metricsUrl + expectedMetric, Map)

        then:
        actual.get("name") == expectedMetric

        where:
        expectedMetric << [
                "task-processing.queue-size",
                "task-processing.queue-capacity",
                "task-processing.queue-utilization",
                "task-processing.active-threads",
        ]
    }
}
