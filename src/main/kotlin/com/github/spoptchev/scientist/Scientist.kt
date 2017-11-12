package com.github.spoptchev.scientist

typealias Comparator<T> = (Outcome<T>, Outcome<T>) -> Boolean
typealias Ignore<T> = (Outcome<T>, Outcome<T>) -> Boolean

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
