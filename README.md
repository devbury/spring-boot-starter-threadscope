# spring-boot-starter-threadscope
A Spring Boot starter that sets up a "thread" scope similar to [SimpleThreadScope]
(http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/context/support/SimpleThreadScope.html).
The difference being `spring-boot-starter-threadscope` supports destruction callbacks such as `@PreDestroy` and
propagates thread scoped beans to asynchronous threads.  Thread scope works in regular application contexts as
well as web application contexts.  It can be used to replace Spring's request scope.
