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
    public void returnDefault_WhenUnexpected() {

        final String foo = "foo";
        final String bar = "bar";
        final String argument = "Argument";
        final Map<String, String> processArguments = argumentProcessor.process(foo, bar, argument);
        assertSoftly(softly -> softly.assertThat(processArguments).containsOnly(entry("default", argument)));

    }

    @Test
    public void returnTimeoutAndDefault_WhenTimeoutFullFlag() {

        final String timeoutFlag = "--timeout";
        final String timeout = "123";
        final String argument = "Argument";
        final Map<String, String> processArguments = argumentProcessor.process(timeoutFlag, timeout, argument);
        assertSoftly(softly -> softly.assertThat(processArguments)
                .containsOnly(entry("timeout", timeout), entry("default", argument)));

    }

    @Test
    public void returnTimeoutAndDefault_WhenTimeoutShortFlag() {

        final String timeoutFlag = "-t";
        final String timeout = "123";
        final String argument = "Argument";
        final Map<String, String> processArguments = argumentProcessor.process(timeoutFlag, timeout, argument);
        assertSoftly(softly -> softly.assertThat(processArguments)
                .containsOnly(entry("timeout", timeout), entry("default", argument)));

    }

    @Test
    public void returnHelpAndDefault_WhenHelpFullFlag() {

        final String helpFlag = "--help";
        final String argument = "Argument";
        final Map<String, String> processArguments = argumentProcessor.process(helpFlag, argument);
        assertSoftly(softly -> softly.assertThat(processArguments)
                .containsOnly(entry("help", null), entry("default", argument)));

    }

    @Test
    public void returnHelpAndDefault_WhenHelpShortFlag() {

        final String helpFlag = "-h";
        final String argument = "Argument";
        final Map<String, String> processArguments = argumentProcessor.process(helpFlag, argument);
        assertSoftly(softly -> softly.assertThat(processArguments)
                .containsOnly(entry("help", null), entry("default", argument)));

    }

    @Test
    public void returnHelp_WhenOnlyHelp() {

        final String helpFlag = "-h";
        final Map<String, String> processArguments = argumentProcessor.process(helpFlag);
        assertSoftly(softly -> softly.assertThat(processArguments).contains(entry("help", null)));

    }

    @Test
    public void returnEmpty_WhenNoArgs() {

        assertThat(argumentProcessor.process()).isEmpty();
    }

}
