package com.epam.gepard.examples.service;

import com.epam.gepard.annotations.TestClass;
import com.epam.gepard.generic.GepardTestClass;
import org.junit.Test;

/**
 * Demonstration of the power of rest assure.
 * @author tkohegyi
 */
@TestClass(id = "REST Service", name = "Using REST Assured")
public class RestServiceTest implements GepardTestClass {

    @Test
    public void testMustPass() {
        naTestCase("This part is not-yet implemented fully");
    }
}
