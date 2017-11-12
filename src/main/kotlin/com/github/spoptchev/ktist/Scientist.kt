package com.github.spoptchev.ktist

data class Scientist<T, out C>(
        private val contextProvider: ContextProvider<C>,
        private val publish: Publisher<T, C> = NullPublisher(),
        private val ignores: List<Ignore<T>> = emptyList(),
        private val comparator: Comparator<T> = { a, b -> a == b }
) {

    fun evaluate(experiment: Experiment<T, C>): T {
        val experimentState = experiment.conduct(contextProvider)

        return when(experimentState) {
            is Skipped<T> -> experimentState.observation.result
            is Conducted<T> -> {
                val (experimentName, observations, controlObservation, candidateObservations) = experimentState

                val allMismatches = candidateObservations.filterNot { it.matches(controlObservation, comparator) }
                val ignoredMismatches = allMismatches.filterNot { it.isIgnored(controlObservation, ignores) }
                val mismatches = allMismatches - ignoredMismatches

                val result = Result(
                        experimentName = experimentName,
                        observations = observations,
                        controlObservation = controlObservation,
                        candidateObservations = candidateObservations,
                        mismatches = mismatches,
                        ignoredMismatches = ignoredMismatches,
                        contextProvider = contextProvider
                )

                publish(result)

                controlObservation.result
            }
        }
    }

}

data class Result<T, out C>(
        val experimentName: String,
        val observations: List<Observation<T>>,
        val controlObservation: Observation<T>,
        val candidateObservations: List<Observation<T>>,
        val mismatches: List<Observation<T>>,
        val ignoredMismatches: List<Observation<T>>,
        val contextProvider: ContextProvider<C>
) {

    val matched: Boolean by lazy { !mismatched && !ignored }
    val mismatched: Boolean by lazy { mismatches.isNotEmpty() }
    val ignored: Boolean by lazy { ignoredMismatches.isNotEmpty() }

}

interface Publisher<T, in C> : (Result<T, C>) -> Unit

class NullPublisher<T, in C> : Publisher<T, C> {
    override fun invoke(result: Result<T, C>) {}
}

interface ContextProvider<out C> : () -> C
