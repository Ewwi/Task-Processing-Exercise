package other.task.component

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.TestPropertySource
import other.task.model.Task
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD
import static other.task.TaskController.NewTaskRequest
import static other.task.TaskController.TaskCreatedResponse

@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = [
        "task-processing-queue-capacity=0",
        "task-processing-thread-pool-size=1",
])
class QueueFullComponentSpec extends Specification {
    @Autowired
    TestRestTemplate restTemplate

    def "should return 503 on POST /tasks and leave no tasks in repository when task queue is full"() {
        given:
        restTemplate.postForEntity("/tasks", new NewTaskRequest(2, 2), TaskCreatedResponse)

        when:
        def response = restTemplate.postForEntity("/tasks", new NewTaskRequest(2, 2), TaskCreatedResponse)

        then:
        response.getStatusCode() == SERVICE_UNAVAILABLE

        and:
        def tasks = restTemplate.getForObject("/tasks", List<Task>)
        tasks.size() == 1
    }
}
