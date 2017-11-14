@file:JvmName("ExperimentsSetup")
package com.github.spoptchev.scientist

data class ExperimentSetup<T, C>(
        private val name: String,
        private val control: Trial<T>? = null,
        private val candidates: List<Trial<T>> = emptyList(),
        private val conductible: (ContextProvider<C>) -> Boolean = { true }
) {

    fun control(name: String = "control", behaviour: Behaviour<T>) =
            copy(control = Trial(name = name, behaviour = behaviour))

    fun candidate(name: String = "candidate", behaviour: Behaviour<T>) =
            copy(candidates = candidates + Trial(name = name, behaviour = behaviour))

    fun conductibleIf(func: (ContextProvider<C>) -> Boolean) =
            copy(conductible = func)

    internal fun complete() = DefaultExperiment(name, control!!, candidates, conductible)

}

data class ScientistSetup<T, C>(
        private val contextProvider: ContextProvider<C>,
        private val publish: Publisher<T, C> = NullPublisher(),
        private val ignores: List<Matcher<T>> = emptyList(),
        private val matcher: Matcher<T> = DefaultMatcher()
) {

    fun publisher(publisher: Publisher<T, C>) = copy(publish = publisher)

    fun ignore(ignore: Matcher<T>) = copy(ignores = ignores + ignore)

    fun match(matcher: Matcher<T>) = copy(matcher = matcher)

    internal fun complete() = Scientist(contextProvider, publish, ignores, matcher)

}

data class ScienceSetup<T, C>(
        private val contextProvider: ContextProvider<C>,
        private val experimentName: String = "default-experiment",
        private val scientist: Scientist<T, C>? = null,
        private val experiment: Experiment<T, C>? = null
) {

    fun scientist(setup: ScientistSetup<T, C>.() -> ScientistSetup<T, C>) =
            copy(scientist = scientist(contextProvider, setup = setup))
    fun scientist(scientist: Scientist<T, C>) =
            copy(scientist = scientist)

    fun experiment(setup: ExperimentSetup<T, C>.() -> ExperimentSetup<T, C>) =
            copy(experiment = experiment(experimentName, setup))
    fun experiment(experiment: Experiment<T, C>) = copy(experiment = experiment)

    internal fun complete() = scientist!!.evaluate(experiment!!)

}

fun <T, C> experiment(name: String, setup: ExperimentSetup<T, C>.() -> ExperimentSetup<T, C>): Experiment<T, C>
        = setup(ExperimentSetup(name)).complete()

fun <T, C> scientist(contextProvider: ContextProvider<C>, setup: ScientistSetup<T, C>.() -> ScientistSetup<T, C>): Scientist<T, C>
        = setup(ScientistSetup(contextProvider)).complete()

fun <T, C> science(name: String, contextProvider: ContextProvider<C>, setup: ScienceSetup<T, C>.() -> ScienceSetup<T, C>): T
        = setup(ScienceSetup(contextProvider = contextProvider, experimentName = name)).complete()

fun <T> science(name: String, setup: ScienceSetup<T, Unit>.() -> ScienceSetup<T, Unit>): T
        = science(name, NoContextProvider, setup)
