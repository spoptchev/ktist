package com.github.spoptchev.ktist

data class Scientist<T>(
        private val publish: Publisher<T> = NullPublisher(),
        private val context: Context = emptyMap(),
        private val ignores: List<Ignore<T>> = emptyList(),
        private val comparator: Comparator<T> = { a, b -> a == b }
) {

    fun evaluate(experiment: OpenExperiment<T>): T {
        val conductedExperiment = experiment.conduct()
        val (experimentName, observations, controlObservation, candidateObservations) = conductedExperiment

        val allMismatches = candidateObservations
                .filterNot { it.matches(controlObservation, comparator) }
        val ignoredMismatches = allMismatches
                .filterNot { it.isIgnored(controlObservation, ignores) }
        val mismatches = allMismatches - ignoredMismatches

        val result = Result(
                experimentName = experimentName,
                observations = observations,
                controlObservation = controlObservation,
                candidateObservations = candidateObservations,
                mismatches = mismatches,
                ignoredMismatches = ignoredMismatches,
                context = context
        )

        publish(result)

        return controlObservation.result
    }

}

data class Result<T>(
        val experimentName: String,
        val observations: List<Observation<T>>,
        val controlObservation: Observation<T>,
        val candidateObservations: List<Observation<T>>,
        val mismatches: List<Observation<T>>,
        val ignoredMismatches: List<Observation<T>>,
        val context: Context
) {

    val matched: Boolean by lazy { !mismatched && !ignored }
    val mismatched: Boolean by lazy { mismatches.isNotEmpty() }
    val ignored: Boolean by lazy { ignoredMismatches.isNotEmpty() }

}

interface Publisher<T> : (Result<T>) -> Unit

class NullPublisher<T> : Publisher<T> {
    override fun invoke(result: Result<T>) {}
}

typealias Context = Map<String, Any>

