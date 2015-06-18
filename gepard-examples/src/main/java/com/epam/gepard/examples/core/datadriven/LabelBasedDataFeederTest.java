package com.epam.gepard.examples.core.datadriven;

import com.epam.gepard.annotations.TestClass;
import com.epam.gepard.generic.GepardTestClass;
import org.junit.Test;

/**
 * This is a sample test class that uses LabelBasedDataFeeder data loader class.
 * @author tkohegyi
 */
@TestClass(id = "DEMO-14", name = "Using LabelBasedDataFeeder")
public class LabelBasedDataFeederTest implements GepardTestClass {

    @Test
    public void testMustPass() {
        logComment("This is a fake test, it must pass.");
    }

}
