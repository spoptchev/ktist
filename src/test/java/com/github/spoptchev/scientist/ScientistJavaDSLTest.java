package com.github.spoptchev.scientist;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import org.junit.Test;

public class ScientistJavaDSLTest {

    class CatchingPublisher implements Function1<Result<Integer, ? extends String>, Unit> {

        private Result<Integer, ? extends String> result;

        @Override
        public Unit invoke(Result<Integer, ? extends String> result) {
            this.result = result;

            return Unit.INSTANCE;
        }

    }

    @Test
    public void testScientistDSL() {
        CatchingPublisher catchingPublisher = new CatchingPublisher();

        Scientist<Integer, String> scientist = Setup.scientist(setup -> setup
                .context(() -> "execute")
                .publisher(catchingPublisher)
        );

        Experiment<Integer, String> experiment = Setup.experiment(setup -> setup
                .name(() -> "experiment-name")
                .control("test-control", () -> 1)
                .candidate("test-candidate", () -> 1)
                .conductibleIf((contextProvider) -> contextProvider.invoke().equals("execute"))
                .catches((throwable) -> throwable instanceof NullPointerException)
        );

        Integer value = scientist.evaluate(experiment);
        Result<Integer, ? extends String> result = catchingPublisher.result;

        assert value == 1;
        assert result.getExperimentName().equals("experiment-name");
        assert result.getObservations().size() == 2;
        assert !result.getMismatched();
        assert !result.getIgnored();
        assert result.getMatched();
    }

}
