package com.github.spoptchev.scientist

data class ExperimentSetup<T, C>(
        val name: String,
        val control: Trial<T>? = null,
        val candidates: List<Trial<T>> = emptyList(),
        val conductible: (ContextProvider<C>) -> Boolean = { true }
) {

    fun control(name: String = "control", behaviour: Behaviour<T>) =
            copy(control = Trial(name = name, behaviour = behaviour))

    fun candidate(name: String = "candidate", behaviour: Behaviour<T>) =
            copy(candidates = candidates + Trial(name = name, behaviour = behaviour))

    fun conductibleIf(func: (ContextProvider<C>) -> Boolean) =
            copy(conductible = func)

}

data class ScientistSetup<T, C>(
        val publish: Publisher<T, C> = NullPublisher(),
        val ignores: List<Matcher<T>> = emptyList(),
        val matcher: Matcher<T> = DefaultMatcher()
) {

    fun publisher(publisher: Publisher<T, C>) = copy(publish = publisher)

    fun ignore(ignore: Matcher<T>) = copy(ignores = ignores + ignore)

    fun match(matcher: Matcher<T>) = copy(matcher = matcher)

}

data class ScienceSetup<T, C>(
        val experimentName: String = "default-experiment",
        val publish: Publisher<T, C> = NullPublisher(),
        val ignores: List<Matcher<T>> = emptyList(),
        val matcher: Matcher<T> = DefaultMatcher(),
        val experiment: Experiment<T, C>? = null
) {

    fun publisher(publisher: Publisher<T, C>) = copy(publish = publisher)

    fun ignore(ignore: Matcher<T>) = copy(ignores = ignores + ignore)

    fun match(matcher: Matcher<T>) = copy(matcher = matcher)

    fun experiment(setup: ExperimentSetup<T, C>.() -> ExperimentSetup<T, C>) =
            copy(experiment = experiment(experimentName, setup))

    fun experiment(experiment: Experiment<T, C>) = copy(experiment = experiment)

}

fun <T, C> experiment(name: String, setup: ExperimentSetup<T, C>.() -> ExperimentSetup<T, C>): Experiment<T, C>
        = setup(ExperimentSetup(name))
        .run { DefaultExperiment(name, control!!, candidates, conductible) }

fun <T, C> scientist(contextProvider: ContextProvider<C>, setup: ScientistSetup<T, C>.() -> ScientistSetup<T, C>): Scientist<T, C>
        = setup(ScientistSetup())
        .run { Scientist(contextProvider, publish, ignores, matcher) }

fun <T, C> science(name: String, contextProvider: ContextProvider<C>, setup: ScienceSetup<T, C>.() -> ScienceSetup<T, C>): T
        = setup(ScienceSetup(experimentName = name))
        .run { Scientist(contextProvider, publish, ignores, matcher).evaluate(experiment!!) }

fun <T> science(name: String, setup: ScienceSetup<T, Unit>.() -> ScienceSetup<T, Unit>): T
        = science(name, { Unit }, setup)
