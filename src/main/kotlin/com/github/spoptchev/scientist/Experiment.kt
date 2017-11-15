package com.github.spoptchev.scientist

interface Experiment<T, in C> {
    fun conduct(context: C): ExperimentState<T>
}

data class DefaultExperiment<T, in C>(
        val name: String,
        val control: Trial<T>,
        val candidates: List<Trial<T>>,
        val conductible: (C) -> Boolean = { true }
) : Experiment<T, C> {

    private val shuffledTrials: List<Trial<T>> by lazy {
        (listOf(control) + candidates).sortedBy { it.id }
    }

    override fun conduct(context: C): ExperimentState<T> {
        return if (conductible(context)) {
            val observations = shuffledTrials.map { it.run() }
            val controlObservation = observations.first { it.id == control.id }
            val candidateObservations = observations - controlObservation

            Conducted(name, observations, controlObservation, candidateObservations)
        } else {
            Skipped(control.run())
        }
    }

}
