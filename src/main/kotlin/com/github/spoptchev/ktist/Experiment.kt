package com.github.spoptchev.ktist

open class Experiment<T, in C>(
        val name: String,
        val control: OpenTrial<T>,
        val candidates: List<OpenTrial<T>>,
        val enabledIf: (ContextProvider<C>) -> Boolean = { true },
        val runIf: (ContextProvider<C>) -> Boolean = { true }
) {

    private val shuffledTrials: List<OpenTrial<T>> by lazy {
        (listOf(control) + candidates).sortedBy { it.id }
    }

    fun conduct(context: ContextProvider<C>): ExperimentState<T> {
        return if (conductExperiment(context)) {
            val observations = shuffledTrials.map { it.run() }
            val controlObservation = observations.first { it == control }
            val candidateObservations = observations - controlObservation

            Conducted(name, observations, controlObservation, candidateObservations)
        } else {
            Skipped(control.run())
        }
    }

    protected fun conductExperiment(context: ContextProvider<C>) = enabledIf(context) && runIf(context)

}
