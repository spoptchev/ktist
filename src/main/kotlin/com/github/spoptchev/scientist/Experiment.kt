package com.github.spoptchev.scientist

interface Experiment<T, in C> {
    fun conduct(contextProvider: ContextProvider<C>): ExperimentState<T>
}

data class DefaultExperiment<T, in C>(
        val name: String,
        val control: OpenTrial<T>,
        val candidates: List<OpenTrial<T>>,
        val conductable: (ContextProvider<C>) -> Boolean = { true }
) : Experiment<T, C> {

    private val shuffledTrials: List<OpenTrial<T>> by lazy {
        (listOf(control) + candidates).sortedBy { it.id }
    }

    override fun conduct(contextProvider: ContextProvider<C>): ExperimentState<T> {
        return if (conductable(contextProvider)) {
            val observations = shuffledTrials.map { it.run() }
            val controlObservation = observations.first { it == control }
            val candidateObservations = observations - controlObservation

            Conducted(name, observations, controlObservation, candidateObservations)
        } else {
            Skipped(control.run())
        }
    }

}
