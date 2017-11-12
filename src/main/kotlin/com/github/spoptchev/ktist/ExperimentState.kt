package com.github.spoptchev.ktist

sealed class ExperimentState<T>
data class Skipped<T>(val observation: Observation<T>) : ExperimentState<T>()
data class Conducted<T>(
        val name: String,
        val observations: List<Observation<T>>,
        val controlObservation: Observation<T>,
        val candidateObservations: List<Observation<T>>
) : ExperimentState<T>()
