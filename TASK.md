# Task description

Task's description and requirements:

Please design and implement a simple REST API for asynchronous Tasks Processing.
Task description:

1. The API should allow to create a task, read the status and the results of the task.
2. When the task is created then the customer receives the unique id of the task.
3. The customer can check the status and the results of the task using the received id.
   Functional requirements:
    - Task accepts two numbers as the parameters: the base and the exponent
    - The result of the task is the exponentiation result: baseexponent
    - While one task is executing the next tasks can also be started and processed, API is not blocked
    - The status of the task should contain the information about the progress
    - Optional: There is an endpoint that lists all the tasks with their statuses and results
      Technical requirements:
    - Use Spring Boot with Java 14+ (Spring initializr might be helpful https://start.spring.io/ )
    - You may use Thread.sleep(1000) or similar approach so the tasks processing takes more time.
    - Please document briefly how to start the application and how to use the API.
      Examples of using the API:

| input                                  | output                                                                                                    |
|----------------------------------------|-----------------------------------------------------------------------------------------------------------|
| POST /tasks { base: 2, exponent: 10 }  | { id: 1 }                                                                                                 |
| GET /tasks/1                           | { id: 1, status: running, progress: 40% }                                                                 |
| POST /tasks { base: 5, exponent: 100 } | { id: 2 }                                                                                                 |
| GET /tasks/2                           | { id: 2, status: running, progress: 2% }                                                                  |
| GET /tasks/1                           | { id: 1, status: finished, progress: 100%, result: 1024 }                                                 |
| GET /tasks                             | { [{ id: 1, status: finished, progress: 100%, result: 1024 }, { id: 2, status: running, progress: 4% } ]} |
