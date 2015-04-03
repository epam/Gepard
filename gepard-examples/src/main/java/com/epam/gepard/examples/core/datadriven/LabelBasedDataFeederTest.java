package com.epam.gepard.examples.core.datadriven;

import com.epam.gepard.annotations.TestClass;
import com.epam.gepard.generic.OtherTestCase;

/**
 * This is a sample test class that uses LabelBasedDataFeeder data loader class.
 * @author tkohegyi
 */
@TestClass(id = "Data Driven Tests", name = "Using LabelBasedDataFeeder")
public class LabelBasedDataFeederTest extends OtherTestCase {
    public void testMustPass() {
        logComment("This is a fake test, it must pass.");
    }
}
