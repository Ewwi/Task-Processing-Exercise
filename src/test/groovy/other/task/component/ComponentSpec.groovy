package other.task.component

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.TestPropertySource
import other.task.model.Task
import spock.lang.Specification

import static java.util.concurrent.TimeUnit.SECONDS
import static org.awaitility.Awaitility.await
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD
import static other.task.TaskController.NewTaskRequest
import static other.task.TaskController.TaskCreatedResponse
import static other.task.model.Task.State
import static other.task.model.Task.State.FINISHED
import static other.task.model.Task.State.RUNNING

@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = [
        "task-processing-time-in-ms=500",
])
class ComponentSpec extends Specification {
    @Autowired
    TestRestTemplate restTemplate

    def "should return 200 on GET /tasks"() {
        when:
        def tasks = restTemplate.getForObject("/tasks", List<Task>)

        then:
        tasks.size() == 0
    }

    def "should create task and return it with GET /tasks/{taskId}"() {
        given:
        def taskCreated = restTemplate.postForObject("/tasks", new NewTaskRequest(1, 1), TaskCreatedResponse)

        when:
        def task = restTemplate.getForObject("/tasks/${taskCreated.id()}", Map)

        then:
        task
    }

    def "should create task and return it with GET /tasks"() {
        given:
        restTemplate.postForObject("/tasks", new NewTaskRequest(1, 1), TaskCreatedResponse)

        when:
        def tasks = restTemplate.getForObject("/tasks", List<Task>)

        then:
        tasks.size() == 1
    }

    def "should create task and it should be finished after processing time"() {
        given:
        def taskCreated = restTemplate.postForObject("/tasks", new NewTaskRequest(2, 2), TaskCreatedResponse)

        when:
        def task = restTemplate.getForObject("/tasks/${taskCreated.id()}", Map)

        then:
        (task.state as State) == RUNNING

        and:
        await()
                .atMost(5, SECONDS)
                .until(() -> taskHasBeenFinished(taskCreated.id()))
    }

    boolean taskHasBeenFinished(long taskId) {
        def task = restTemplate.getForObject("/tasks/${taskId}", Map)

        (task.state as State) == FINISHED
    }
}
