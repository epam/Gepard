package com.epam.gepard.examples.soapui;
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
import com.epam.gepard.common.Environment;
import com.eviware.soapui.tools.SoapUIMockServiceRunner;

/**
 * This test class is to provide example usage of SoapUI via Gepard.
 * @author Tamas Kohegyi
 */
@TestClass(id = "SOAPUI", name = "Basic SoapUI Sample")
public class SoapUITestBase extends SoapUITestCase {

    /**
     * Simple test that loads the project file and executes it.
     * @throws Exception in case of test failure
     */
    public void testSoapUISampleAuthenticationExample() {
        String projectFile = Environment.getProperty(Environment.GEPARD_TEST_RESOURCE_PATH).concat("/soapui-examples/Sample-Authentication-Project-soapui-project.xml");
        setProjectFile(projectFile);   // Specify test project XML local file path, can use URL instead
        //run the SoapUI test
        runSoapUITest();
    }

    /**
     * Simple test that loads the project file and executes it.
     * @throws Exception in case of test failure
     */
    public void testSoapUISampleSOAPProjectExample() throws Exception {
        String projectFile = Environment.getProperty(Environment.GEPARD_TEST_RESOURCE_PATH).concat("/soapui-examples/Sample-SOAP-Project-soapui-project.xml");

        //run the mock service first
        SoapUIMockServiceRunner mockServiceRunner = new SoapUIMockServiceRunner();
        mockServiceRunner.setProjectFile(projectFile);
        mockServiceRunner.run();

        //run the SoapUI test
        setProjectFile(projectFile);   // Specify test project XML local file path, can use URL instead
        runSoapUITest();

        mockServiceRunner.stopAll();
    }

    /**
     * Simple test that loads the project file and executes it.
     * @throws Exception in case of test failure
     */
    public void testSoapUISampleRESTProjectExample() throws Exception {
        String projectFile = Environment.getProperty(Environment.GEPARD_TEST_RESOURCE_PATH).concat("/soapui-examples/Sample-REST-Project-soapui-project.xml");

        //run the mock service first
        SoapUIMockServiceRunner mockServiceRunner = new SoapUIMockServiceRunner();
        mockServiceRunner.setProjectFile(projectFile);
        mockServiceRunner.run();

        //run the SoapUI test
        setProjectFile(projectFile);   // Specify test project XML local file path, can use URL instead
        runSoapUITest();

        mockServiceRunner.stopAll();
    }

}
