# Scientist [![Build Status](https://travis-ci.org/spoptchev/scientist.svg?branch=master)](https://travis-ci.org/spoptchev/scientist)

A kotlin library for carefully refactoring critical paths in your application.

This library is inspired by the ruby gem [scientist](https://github.com/github/scientist).

## How do I science?

Let's pretend you're changing the way you handle permissions in a large web app. Tests can help guide your refactoring, but you really want to compare the current and refactored behaviors under load.


```kotlin
fun isAllowed(user: User): Boolean = scientist<Boolean, Unit>() conduct {
    experiment { "widget-permissions" }
    control { user.isAllowedOldWay() }
    candidate { user.isAllowedNewWay() }
}
```

Wrap a `control` lambda around the code's original behavior, and wrap `candidate` around the new behavior. When conducting the experiment `conduct` will always return whatever the `control` lambda returns, but it does a bunch of stuff behind the scenes:

* It decides whether or not to run the `candidate` lambda,
* Randomizes the order in which `control` and `candidate` lambdas are run,
* Measures the durations of all behaviors,
* Compares the result of `candidate` to the result of `control`,
* Swallows (but records) any exceptions thrown in the `candidate` lambda, and
* Publishes all this information.

## Scientist and Experiment

Compared to other scientist libraries this library separates the concepts of a scientist and the experiment.
Which in turn gives you more freedom and flexibility to compose and reuse scientists and experiments (especially with dependency injection frameworks).

```kotlin
val scientist = scientist<Boolean, Unit>()
val experiment = experiment<Boolean, Unit() {
    control { true }
}

val result = scientist conduct experiment
```

### Setting up the scientist

The scientist is responsible for setting up the environment of an experiment and conducting it.

#### Publishing results

The examples above will run, but they're not really *doing* anything. The `candidate` lambdas run every time and none of the results get published. Add a publisher to control the result reporting:

```kotlin
val scientist = scientist<Boolean, Unit> {
    publisher { result -> logger.info(result) }
}
```

You can also extend the publisher `typealias` which then can be used as a parameter of the publisher lambda:

```kotlin
val logger = loggerFactory.call()

class LoggingPublisher(val logger: Logger) : Publisher<Boolean, Unit> {
    override fun invoke(result: Result<Boolean, Unit>) {
        logger.info(result)
    }
}

val loggingPublisher = LoggingPublisher(logger)

val scientist = scientist<Boolean, Unit> {
    publisher(loggingPublisher)
}
```

#### Controlling matches

Scientist compares if control and candidate values have matched by using `==`. To override this behavior, use `match` to define how to compare observed values instead:

```kotlin
val scientist = scientist<Boolean, Unit> {
    match { candidate, control -> candidate != control }
}
```

`candidate` and `control` are both of type `Outcome` (a sealed class) which either can be `Success` or `Failure`, so you can easily reason about them. As an example take a look at the default implementation:

```kotlin
class DefaultMatcher<in T> : Matcher<T> {
    override fun invoke(candidate: Outcome<T>, control: Outcome<T>): Boolean = when(candidate) {
        is Success -> when(control) {
            is Success -> candidate == control
            is Failure -> false
        }
        is Failure -> when(control) {
            is Success -> false
            is Failure -> candidate.errorMessage == control.errorMessage
        }
    }
}
```

A `Success` outcome contains the value that has been evaluated. A `Failure` outcome contains the exception that was caught while evaluating a `control` or `candidate` statement.

#### Adding context

To provide additional data to the scientist `Result` and `Experiments` you can use the `context` lambda to add a context provider:

```kotlin
val scientist = scientist<Boolean, Map<String, Boolean>> {
    context { mapOf("yes" to true, "no" to false) }
}
```

The context is evaluated lazily and is exposed to the publishable `Result` by evaluating `val context = result.contextProvider()` and in the experiments `conductibleIf` lambda that will be described further down the page.

#### Ignoring mismatches

During the early stages of an experiment, it's possible that some of your code will always generate a mismatch for reasons you know and understand but haven't yet fixed. Instead of these known cases always showing up as mismatches in your metrics or analysis, you can tell the scientist whether or not to ignore a mismatch using the `ignore` lambda. You may include more than one lambda if needed:

```kotlin
val scientist = scientist<Boolean, Map<String, Boolean>> {
    ignore { candidate, control -> candidate.isFailure() }
}
```

Like in `match` candidate and control are of type `Outcome`.

#### Testing

When running your test suite, it's helpful to know that the experimental results always match. To help with testing, Scientist defines a `throwOnMismatches` field. Only do this in your test suite!

To throw on mismatches:

```kotlin
val scientist = scientist<Boolean, Map<String, Boolean>> {
    throwOnMismatches { true }
}
```

Scientist will throw a `MismatchException` exception if any observations don't match.

#### Putting it all together

```kotlin
val scientist = scientist<Boolean, Map<String, Boolean>> {
    publisher { result -> logger.info(result) }
    match { candidate, control -> candidate != control }
    context { mapOf("yes" to true, "no" to false) }
    ignore { candidate, control -> candidate.isFailure() }
}
```

### Setting up an experiment

With an experiment you are setting up tests for the critical paths of your application by specifying a `control` and `candidate` lambda.

```kotlin
fun experiment = experiment<Boolean, Unit> {
    name { "widget-permissions" }
    control { user.isAllowedOldWay() }
    candidate { user.isAllowedNewWay() }
}
```

#### Enabling/disabling experiments

Sometimes you don't want an experiment to run. Say, disabling a new code path for anyone who isn't member. You can disable an experiment by setting a `conductibleIf` lambda. If this returns `false`, the experiment will merely return the control value.

```kotlin
experiment<Boolean, Unit> {
    // ...
    conductibleIf { user.isMember() }
}
```

The `conductibleIf` lambda can also take a `contextProvider` as a parameter:

```kotlin
experiment<Boolean, Map<String, Boolean>> {
    // ...
    conductibleIf { context -> context()["externalCondition"]!! }
}
```

#### Handling errors

Scientist catches and tracks _all_ exceptions thrown in a `control` or `candidate` lambda. To catch a more restrictive set of exceptions add a `catch` lambda to your experiment setup:

```kotlin
experiment<Boolean, Unit> {
    // ...
    catch { e -> e is NullPointerException }
}
```

### Java interop

The Java interoperability can certainly be improved but should be sufficient for now:

```java
public boolean isAllowed(User user) {
    Scientist<Boolean, String> scientist = Setup.scientist(setup -> setup
            .context(() -> "execute")
    );

    Experiment<Boolean, String> experiment = Setup.experiment(setup -> setup
            .name(() -> "experiment-name")
            .control("test-control", () -> user.isAllowedOldWay())
            .candidate("test-candidate", () -> user.isAllowedNewWay())
            .conductibleIf((contextProvider) -> contextProvider.invoke().equals("execute"))
    );

    return scientist.evaluate(experiment);
}
```
