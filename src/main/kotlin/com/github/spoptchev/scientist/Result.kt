package com.github.spoptchev.scientist

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
