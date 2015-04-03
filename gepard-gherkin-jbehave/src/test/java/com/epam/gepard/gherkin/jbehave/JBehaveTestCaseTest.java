package com.epam.gepard.gherkin.jbehave;

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

import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

/**
 * Unit test for {@link JBehaveTestCase}.
 * @author Adam_Csaba_Kiraly
 */
public class JBehaveTestCaseTest {

    private static final String STORY_PATH = "storyPath";

    @Mock
    private GepardEmbedder gepardEmbedder;

    private JBehaveTestCase underTest;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        underTest = new JBehaveTestCase() {
            @Override
            protected String getStoryPath() {
                return STORY_PATH;
            }
        };
        Whitebox.setInternalState(underTest, "gepardEmbedder", gepardEmbedder);
    }

    @Test
    public void testTestGherkinShouldSetupEmbedder() {
        //GIVEN in setup
        //WHEN
        underTest.testRunJBehave();
        //THEN
        verify(gepardEmbedder).setupJBehaveEmbedder(underTest);
    }

    @Test
    public void testTestGherkinShouldRunEmbedderWith() {
        //GIVEN in setup
        //WHEN
        underTest.testRunJBehave();
        //THEN
        verify(gepardEmbedder).runTest(STORY_PATH);
    }

}
