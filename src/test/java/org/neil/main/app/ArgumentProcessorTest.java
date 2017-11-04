package org.neil.main.app;

import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.assertj.core.data.MapEntry.entry;

public class ArgumentProcessorTest {

    private ArgumentProcessor argumentProcessor = new SimpleArgumentProcessor();

    @Test
    public void returnDefault_WhenNoFlagArg() {

        final String argument = "Argument";
        final Map<String, String> processArguments = argumentProcessor.process(argument);
        assertSoftly(softly -> softly.assertThat(processArguments).containsOnly(entry("default", argument)));

    }

    @Test
    public void returnEmpty_WhenNoArgs() {

        assertThat(argumentProcessor.process()).isEmpty();
    }

}
