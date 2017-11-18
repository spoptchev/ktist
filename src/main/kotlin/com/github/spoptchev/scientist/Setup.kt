@file:JvmName("Setup")
package com.github.spoptchev.scientist

import kotlin.properties.Delegates

class ExperimentSetup<T, C> {

    private var name: String = "default-experiment"
    private var control: Trial<T> by Delegates.notNull()
    private var candidates: List<Trial<T>> = mutableListOf()
    private var conductible: (ContextProvider<C>) -> Boolean = { true }
    private var catches: (Throwable) -> Boolean = { true }

    fun name(name: () -> String) = apply { this.name = name() }
    fun experiment(name: () -> String) = name(name)

    fun control(name: String = "control", behaviour: Behaviour<T>) = apply {
        control = Trial(name = name, behaviour = behaviour)
    }

    fun candidate(name: String = "candidate", behaviour: Behaviour<T>) = apply {
        candidates += Trial(name = name, behaviour = behaviour)
    }

    fun conductibleIf(predicate: (ContextProvider<C>) -> Boolean) = apply {
        conductible = predicate
    }

    fun catch(catcher: (Throwable) -> Boolean) = apply {
        catches = catcher
    }

    internal fun complete() = DefaultExperiment(
            name = name,
            control = control.copy(catches = catches),
            candidates = candidates.map { it.copy(catches = catches) },
            conductible = conductible
    )

}

class ScientistSetup<T, C> {

    private var contextProvider: ContextProvider<C> = NotImplementedContextProvider()
    private var publish: Publisher<T, C> = NoPublisher()
    private var ignores: List<Matcher<T>> = mutableListOf()
    private var matcher: Matcher<T> = DefaultMatcher()

    fun publisher(publisher: Publisher<T, C>) = apply {
        publish = publisher
    }

    fun ignore(ignore: Matcher<T>) = apply {
        this.ignores += ignore
    }

    fun match(matcher: Matcher<T>) = apply {
        this.matcher = matcher
    }

    fun context(contextProvider: ContextProvider<C>) = apply {
        this.contextProvider = contextProvider
    }

    internal fun complete() = Scientist(contextProvider, publish, ignores, matcher)

}

infix fun <T, C> Scientist<T, C>.conduct(setup: ExperimentSetup<T, C>.() -> ExperimentSetup<T, C>): T =
        this.evaluate(experiment(setup))

infix fun <T, C> Scientist<T, C>.conduct(experiment: Experiment<T, C>): T = this.evaluate(experiment)

fun <T, C> experiment(setup: ExperimentSetup<T, C>.() -> ExperimentSetup<T, C>): Experiment<T, C>
        = setup(ExperimentSetup()).complete()

fun <T, C> scientist(setup: ScientistSetup<T, C>.() -> ScientistSetup<T, C>): Scientist<T, C>
        = setup(ScientistSetup()).complete()

fun <T, C> scientist(): Scientist<T, C>
        = ScientistSetup<T, C>().complete()
