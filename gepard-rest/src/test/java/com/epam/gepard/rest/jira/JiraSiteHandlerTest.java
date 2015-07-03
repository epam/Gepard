package com.epam.gepard.rest.jira;

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

import com.epam.gepard.common.Environment;
import com.epam.gepard.rest.TestMock;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Unit test for {@link com.epam.gepard.rest.jira.JiraSiteHandler}.
 * @author tkohegyi
 */
public class JiraSiteHandlerTest {

    private JiraSiteHandler underTest;

    @Mock
    private TestMock testMock;
    @Mock
    private Environment environment;

    @Before
    public void setUp() throws Exception {
        environment = new Environment();
        testMock = new TestMock();
        //underTest = new JiraSiteHandler(testMock, environment);
    }

    @Test
    public void dummyTest() {
        //GIVEN
        //WHEN
        //THEN
    }

}
