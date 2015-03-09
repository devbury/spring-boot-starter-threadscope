# spring-boot-starter-threadscope
A Spring Boot starter that sets up a "thread" scope similar to [SimpleThreadScope]
(http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/context/support/SimpleThreadScope.html).
The difference being `spring-boot-starter-threadscope` supports destruction callbacks such as `@PreDestroy` and
propagates thread scoped beans to asynchronous threads.  Thread scope works in regular application contexts as
well as web application contexts.  It can be used to replace Spring's request scope.

## Activating

`spring-boot-starter-threadscope` is published to the [jcenter](https://bintray.com/bintray/jcenter) and [maven central](http://search.maven.org) repositories. To
use the starter, add the following dependency information to your project.

```xml
<dependency>
    <groupId>com.devbury</groupId>
    <artifactId>spring-boot-starter-threadscope</artifactId>
    <version>1.1.0</version>
</dependency>
```

To build and install the project from source, clone the Git repository and run

`mvn clean install`

The starter provides you with a configured thread scope and a task executor that will propagate thread scoped beans
to asynchronous tasks.

## Configuring
### Properties

The following properties may be set in your application.properties file to override default values.
```properties
threadScope.poolSize=25
threadScope.threadNamePrefix=async-
threadScope.scopeName=request
```

The default setting of `threadScope.scopeName=request` will replace the default web request scope with the starter's
thread based scope and all existing request scoped beans will use the new thread scope.  If you want to use both the
existing request scope and the new thread scope, change the value of this setting.

### Default Beans

`spring-boot-starter-threadscope` will configure a default working environment but will remove the defaults based on
the beans configured in your application.  If an `AsyncConfigurer` is not already already defined one will be
registered.  A default `SimpleAsyncUncaughtExceptionHandler` will be defined if an `AsyncUncaughtExceptionHandler` is
not defined.  A `ThreadScopePropagatingScheduler` that can propagate the scheduling thread's scope to threads in the
pool will be used unless another implementation is provided.
