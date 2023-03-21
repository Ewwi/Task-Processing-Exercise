package other.task

import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.server.ResponseStatusException
import other.task.model.FinishedTask
import other.task.model.RunningTask
import other.task.model.Task
import spock.lang.Specification
import spock.mock.DetachedMockFactory

import static org.skyscreamer.jsonassert.JSONCompareMode.NON_EXTENSIBLE
import static org.springframework.http.HttpStatus.*
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

@WebMvcTest
class TaskControllerSpec extends Specification {
    @Autowired
    MockMvc mockMvc

    @Autowired
    TaskService taskServiceStub

    def "should return 200 on GET /tasks"() {
        given:
        taskServiceStub.findAllTasks(*_) >> [runningTask(), finishedTask()]

        when:
        def response = mockMvc.perform(get("/tasks"))
                .andReturn()
                .getResponse()

        then:
        response.status == OK.value()

        and:
        jsonEqual response.contentAsString, expectedTaskListJson()
    }

    Task runningTask() {
        RunningTask.builder()
                .id(1)
                .base(1)
                .exponent(1)
                .progress(0)
                .build()
    }

    Task finishedTask() {
        FinishedTask.builder()
                .id(2)
                .base(2)
                .exponent(2)
                .result(4)
                .build()
    }

    void jsonEqual(String actual, String expected) {
        JSONAssert.assertEquals(expected, actual, NON_EXTENSIBLE)
    }

    String expectedTaskListJson() {
        """
            [
                {
                    "id": 1,
                    "state": "RUNNING",
                    "progress": 0
                },
                {
                    "id": 2,
                    "state": "FINISHED",
                    "result": 4
                }
            ]
        """
    }

    def "should return 200 on GET /tasks/{taskId}"() {
        given:
        taskServiceStub.findTaskById(2) >> Optional.of(finishedTask())

        when:
        def response = mockMvc.perform(get("/tasks/2"))
                .andReturn()
                .getResponse()

        then:
        response.status == OK.value()

        and:
        jsonEqual response.contentAsString, expectedTaskJson()
    }

    String expectedTaskJson() {
        """
            {
                "id": 2,
                "state": "FINISHED",
                "result": 4
            }
        """
    }

    def "should return 404 on GET /tasks/{taskId} when task is not found"() {
        given:
        taskServiceStub.findTaskById(2) >> Optional.empty()

        when:
        def response = mockMvc.perform(get("/tasks/2"))
                .andReturn()
                .getResponse()

        then:
        response.status == NOT_FOUND.value()
    }

    def "should return 202 on POST /tasks"() {
        given:
        taskServiceStub.createNewTask(*_) >> 1L

        and:
        def postRequest = post("/tasks")
                .contentType(APPLICATION_JSON)
                .content(newTaskCreationRequestJson())

        when:
        def response = mockMvc.perform(postRequest)
                .andReturn()
                .getResponse()

        then:
        response.status == ACCEPTED.value()

        and:
        jsonEqual response.contentAsString, '{"id": 1}'
    }

    def "should return 503 on POST /tasks when task queue is full"() {
        given:
        taskServiceStub.createNewTask(*_) >> { throw new ResponseStatusException(SERVICE_UNAVAILABLE) }

        and:
        def postRequest = post("/tasks")
                .contentType(APPLICATION_JSON)
                .content(newTaskCreationRequestJson())

        when:
        def response = mockMvc.perform(postRequest)
                .andReturn()
                .getResponse()

        then:
        response.status == SERVICE_UNAVAILABLE.value()

        and:
        response.contentAsString == ""
    }

    def newTaskCreationRequestJson() {
        """
            {
                "base": 1,
                "exponent": 1
            }
        """
    }


    @TestConfiguration
    static class StubConfig {
        @Bean
        TaskService stubTaskService() {
            return new DetachedMockFactory().Stub(TaskService)
        }
    }
}
