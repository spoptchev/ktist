package com.github.spoptchev.scientist


class MismatchException(experimentName: String) : RuntimeException("Experiment $experimentName observations mismatched")
