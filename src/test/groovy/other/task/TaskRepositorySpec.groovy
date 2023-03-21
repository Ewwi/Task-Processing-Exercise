package other.task

import spock.lang.Specification

class TaskRepositorySpec extends Specification {
    def repository = new TaskRepository()

    def "setup"() {
        repository.deleteAllTasks()
    }

    def "should create new task and fetch it"() {
        given:
        def createdTask = repository.createNewTask(1, 1)

        when:
        def allTasks = repository.findAll(0, 100)
        def specificTask = repository.findById(createdTask.getId()).get()

        then:
        allTasks.size() == 1

        and:
        allTasks[0] == specificTask
    }

    def "should delete task"() {
        given:
        def taskInRepository = repository.createNewTask(1, 1)

        when:
        def hasBeenDeleted = repository.deleteTask(taskInRepository.getId())

        then:
        hasBeenDeleted

        and:
        def allTasks = repository.findAll(0, 100)
        allTasks.size() == 0
    }
}
