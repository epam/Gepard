package com.epam.gepard.examples.gherkin.jbehave;

/*==========================================================================
Copyright 2004-2015 EPAM Systems

This file is part of Gepard.

Gepard is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Gepard is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Gepard.  If not, see <http://www.gnu.org/licenses/>.
===========================================================================*/

import com.epam.gepard.annotations.TestClass;
import org.junit.Test;

/**
 * Test for gherkin_example_invalid.story.
 * @author Adam_Csaba_Kiraly
 *
 */
@TestClass(id = "JBehave Test", name = "Valid Story file but missing last step definition in test")
public class JBehaveExampleInvalidTest extends JBehaveGlueCodeExampleTest {

    @Override
    protected String getStoryPath() {
        return "stories/gherkin_example_invalid.story";
    }

    @Test
    public void runJBehaveExampleInvalidTest() {
        testRunJBehave();
    }

}
