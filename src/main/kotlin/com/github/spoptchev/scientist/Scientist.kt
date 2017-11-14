package com.github.spoptchev.scientist

data class Scientist<T, C>(
        val contextProvider: ContextProvider<C>,
        val publish: Publisher<T, C> = NullPublisher(),
        val ignores: List<Matcher<T>> = emptyList(),
        val matcher: Matcher<T> = DefaultMatcher()
) {

    fun evaluate(experiment: Experiment<T, C>): T {
        val experimentState = experiment.conduct(contextProvider)

        return when(experimentState) {
            is Skipped -> experimentState.observation.result
            is Conducted -> {
                val (experimentName, observations, controlObservation, candidateObservations) = experimentState

                val allMismatches = candidateObservations.filterNot { it.matches(controlObservation, matcher) }
                val ignoredMismatches = allMismatches.filter { it.isIgnored(controlObservation, ignores) }
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
