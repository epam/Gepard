package com.epam.gepard.generic;

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

import com.epam.gepard.AllTestRunner;
import com.epam.gepard.common.Environment;
import com.epam.gepard.common.TestCaseExecutionData;
import com.epam.gepard.common.TestClassExecutionData;
import com.epam.gepard.logger.LogFileWriter;
import com.epam.gepard.util.ExitCode;
import com.epam.gepard.util.Util;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestFailure;
import junit.framework.TestResult;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Enumeration;
import java.util.Properties;

/**
 * This class is the holder class for a Test Case Set (GenericTestCase).
 * <p/>
 * It has two main functionality:
 * a) Supporting HTML logs
 * b) Invoking beforeTestCaseSet and afterTestCaseSet event
 * <p/>
 * Pls note that beforeTestCaseSet and afterTestCaseSet are invoked in two
 * dummy instances of the GenericTestCase. Therefore none of the attributes
 * can be used in the subclass.
 * <p/>
 * Here are the invocation steps:
 * <pre><code>
 * &lt;dummy_instance1&gt;.beforeTestCaseSet()
 * &lt;instance1&gt;.setUp()
 * &lt;instance1&gt;.testCase1()
 * &lt;instance1&gt;.tearDown()
 * &lt;instance2&gt;.setUp()
 * &lt;instance2&gt;.testCase2()
 * &lt;instance2&gt;.tearDown()
 * &lt;dummy_instance2&gt;.afterTestCaseSet()
 * </code></pre>
 * This class can be created only by a GenericTestCase.
 * <p/>
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) EPAM Systems 2002-2005</p>
 * <p>Company: EPAM Systems </p>
 *
 * @author Laszlo Toth, Tamas Godan, Tamas Kohegyi
 */
public class GenericTestSuite extends TestSuite {
    private static final int EXIT_STATUS = 4;
    private LogFileWriter htmlLog;
    private String classDir = "";
    private String className = "";
    private String classFullName = "";
    private final GenericResult gr;
    private int dummyCount;
    private int naCount;
    private final int dataDrivenId;
    private final Environment environment;

    /**
     * This is the object of the test class to store test class execution data.
     */
    private TestClassExecutionData o;

    /**
     * Constructor.
     *
     * @param theClass    Test class
     * @param testID      test ID
     * @param testName    test name
     * @param environment holds the properties of the application
     */
    public GenericTestSuite(final Class<? extends TestCase> theClass, final String testID, final String testName, final Environment environment) {
        super(theClass);
        this.environment = environment;
        dataDrivenId = CommonTestCase.getActualDataRow();
        gr = new GenericResult(testID, testName);
        unPackClassNameandDir(theClass);

        //check test Class constructor, we need constructor without any parameters
        Constructor<?> constructor;
        try {
            constructor = getTestConstructor(theClass);
            if (constructor.getParameterTypes().length != 0) {
                AllTestRunner.CONSOLE_LOG.info("\nTest Class:" + theClass.getCanonicalName() + " has no proper constructor available, pls fix!");
                AllTestRunner.exitFromGepard(ExitCode.EXIT_CODE_TEST_CLASS_HAS_BAD_CONSTRUCTOR);
            }
        } catch (NoSuchMethodException e) {
            AllTestRunner.CONSOLE_LOG.info("\nTest Class:" + theClass.getCanonicalName() + " has no constructor available, pls fix!");
            AllTestRunner.exitFromGepard(ExitCode.EXIT_CODE_TEST_CLASS_HAS_NO_CONSTRUCTOR);
        }
    }

    /**
     * Returns test ID.
     *
     * @return test ID string
     */
    public String getTestID() {
        return gr.getID();
    }

    /**
     * Returns test name.
     *
     * @return test name string
     */
    public String getTestName() {
        return gr.getName();
    }

    /**
     * Returns test HTML log file path.
     *
     * @return path
     */
    public String getTestURL() {
        String dataDrivenName = getDataDrivenClassName();
        return classDir + ("".equals(classDir) ? "" : "/") + dataDrivenName + ".html";
    }

    /**
     * Gets the number of dummy test cases in this test case set.
     *
     * @return with the number.
     */
    public int getDummyCount() {
        return dummyCount;
    }

    /**
     * Gets the number of N/A test cases in this test case set.
     *
     * @return with the number.
     */
    public int getNaCount() {
        return naCount;
    }

    /**
     * Sets classDir attribute to the proper path of the given class.
     *
     * @param theClass Class to use
     */
    private void unPackClassNameandDir(final Class<?> theClass) {
        String pname = theClass.getName();
        classFullName = pname;

        String[] st = pname.split("\\.");

        if ((st[0] != null) && !("test".equals(st[0]))) {
            className = st[0];
        }
        int i = 1;
        while (i < st.length) {
            if (className.length() > 0) {
                classDir = classDir + className + "/";
            }
            className = st[i];
            i++;
        }
        if (classDir.endsWith("/")) {
            classDir = classDir.substring(0, classDir.length() - 1);
        }

    }

    /**
     * Re-formats a path so that it contains only forward slashes,
     * and also removes double slashes.
     *
     * @param pathName is the input path to be formatted correctly.
     * @return with the formatted path.
     */
    public String formatPathName(final String pathName) {
        String inPathName = pathName.replace('\\', '/');
        int index = 0;
        int pos;
        while ((pos = inPathName.indexOf("//", index)) != -1) {
            inPathName = inPathName.substring(0, pos) + "/" + inPathName.substring(pos + 2);
            index = pos;
        }
        return inPathName;
    }

    /**
     * Creates the specified directory path.
     *
     * @param dirPath directory path
     */
    private void createDirectory(final String dirPath) {

        String inDirPath = formatPathName(dirPath);

        try {
            //Checking whether classpath exists or not
            File start = new File(inDirPath);
            if (!start.exists()) {
                //noinspection ResultOfMethodCallIgnored
                start.mkdirs();
            }
        } catch (Exception e) {
            AllTestRunner.CONSOLE_LOG.info("ERROR: Cannot create folder.", e);
        }

    }

    /**
     * Builds up the so called Data Driven ClassName, which is used at report generation.
     * In case the test is data driven (and therefore called several times), to ensure that the result shows all the runs individually,
     * the ClassName that is used to build up the result path should be driven by the number of the data rows currently used.
     *
     * @return the Data driven class name, i.e. returns with the classname + the actual data row.
     */
    private String getDataDrivenClassName() {
        return className + dataDrivenId;
    }

    /**
     * Run test case set (i.e. the Test Class)
     *
     * @param result Test result
     */
    @Override
    public void run(final TestResult result) {

        //Before running the test case set
        String caseID = classFullName + "/" + dataDrivenId;
        o = GenericListTestSuite.getTestClassExecutionData(caseID);
        o.setTestCaseSet(Environment.createTestCaseSet(gr.getName(), Environment.getScript()));

        //crate result info for Test Set (class)
        createDirectory(environment.getProperty(Environment.GEPARD_HTML_RESULT_PATH) + "/" + classDir);
        String dataDrivenName = getDataDrivenClassName();
        htmlLog = new LogFileWriter(environment.getProperty(Environment.GEPARD_RESULT_TEMPLATE_PATH) + "/" + "temp_generictestsuite.html",
                environment.getProperty(Environment.GEPARD_HTML_RESULT_PATH) + "/" + classDir + "/" + dataDrivenName + ".html", environment);
        Properties props = new Properties();
        props.setProperty("ID", gr.getID());
        props.setProperty("Name", gr.getName());
        htmlLog.insertBlock("Header", props);

        //beforeTestCaseSet / before TestClass, handling @BeforeClass
        beforeTestCaseSet();

        htmlLog.insertBlock("TableHead", props);

        //Running the test case set
        super.run(result);

        //After running the test case set

        if (countTestCases() == 0) {
            htmlLog.insertBlock("NoTestCases", null);
        }
        htmlLog.insertBlock("TableEnd", props);

        //afterTestCaseSet / after TestClass, handling @AfterClass
        afterTestCaseSet();

        htmlLog.insertBlock("Footer", null);
        o.getTestCaseSet().updateStatus();
    }

    private void beforeTestCaseSet() {
        CommonTestCase genBefore = (CommonTestCase) getFakeInstance();
        try {
            genBefore.beforeTestCaseSet();
        } catch (Throwable t) {
            String message = Util.getStackTrace(t);
            Properties prs = new Properties();
            prs.setProperty("ErrorMsg", message);
            htmlLog.insertBlock("beforeTestCaseSetError", prs);
        }
    }

    private void afterTestCaseSet() {
        CommonTestCase genAfter = (CommonTestCase) getFakeInstance();
        try {
            genAfter.afterTestCaseSet();
        } catch (Throwable t) {
            String message = Util.getStackTrace(t);
            Properties prs = new Properties();
            prs.setProperty("ErrorMsg", message);
            htmlLog.insertBlock("afterTestCaseSetError", prs);

        }
    }

    /**
     * Run a single test method.
     *
     * @param test   Test case
     * @param result Test result
     */
    @Override
    public void runTest(final Test test, final TestResult result) {
        //Before running the test case
        TestResult tr = new TestResult();
        result.startTest(test);

        //Running the test case
        super.runTest(test, tr);

        //After running the test case
        Util u = new Util();
        boolean isDummy = checkIfDummyTestCase(test);
        boolean isNA = checkIfNaTestCase(test, result);
        String dataDrivenName = className + dataDrivenId + "/" + ((TestCase) test).getName() + dataDrivenId;
        Properties props;
        if (isNA) {
            PropertiesData data = createPropertiesData(isDummy, true);
            props = createProperties(test, u, data, dataDrivenName);
        } else if (tr.failureCount() + tr.errorCount() == 0) {
            //The test succeeded
            PropertiesData data = createPropertiesData(isDummy, false);
            props = createProperties(test, u, data, dataDrivenName);
            o.increaseCountPassed();
        } else {
            //The test failed (note that tr contains only ONE failure or error).
            o.increaseCountFailed();
            String errorMsg = "---No error message---";
            if (tr.failures().hasMoreElements()) {
                TestFailure e = tr.failures().nextElement();
                Throwable t = e.thrownException();
                errorMsg = t.getMessage();
                result.addFailure(test, (AssertionFailedError) t); //we want to collect failures in result
            } else {
                warnUserInCaseOfMoreErrors(test, result, tr, u);
            }
            PropertiesData data = createFailurePropertiesData(isDummy, false, errorMsg);
            props = createProperties(test, u, data, dataDrivenName);
        }
        htmlLog.insertBlock("TestRow", props);

        result.endTest(test);
    }

    private void warnUserInCaseOfMoreErrors(final Test test, final TestResult result, final TestResult tr, final Util u) {
        if (tr.errors().hasMoreElements()) {
            //somebody made bad thing in the test script. ehhh.
            //we should stop running the test right now.
            // but we cannot, so just warn the user...
            o.addSysOut("\n##################################################################################");
            o.addSysOut("# Test Script or the Test Environment caused unrecoverable exception.            #");
            o.addSysOut("#       This is a test script error, or a test environment error.                #");
            o.addSysOut("# Please check your Test Environment and debug your Test Scripts and correct it. #");
            o.addSysOut("##################################################################################");
            TestFailure f = tr.errors().nextElement();
            o.addSysOut("ERROR: " + f.exceptionMessage());
            String desc = u.escapeHTML(Util.getStackTrace(f.thrownException()));
            o.addSysOut(desc);
            //add this special error to the test results
            result.addFailure(test, new AssertionFailedError(
                    "Test Script/Test Environment error was detected by Gepard, please check your Test Script/Test Environment."));
            //what we can do is to set the exit code
            //noinspection ConstantConditions
            TestCaseExecutionData.setExitStatus((CommonTestCase) test, EXIT_STATUS);
        }
    }

    private PropertiesData createFailurePropertiesData(final boolean isDummy, final boolean isNA, final String errorMsg) {
        String finalErrorMessage = errorMsg;
        if (finalErrorMessage == null) {
            finalErrorMessage = "---No error message---";
        }
        String extColor = determineFailureExtColor(isDummy, isNA);
        String extText = determineFailureExtText(isDummy, isNA);
        String testResult = "Failed";
        return new PropertiesData(extText, extColor, testResult, finalErrorMessage);
    }

    private PropertiesData createPropertiesData(final boolean isDummy, final boolean isNA) {
        String extColor = determineExtColor(isDummy, isNA);
        String testResult = determineTestResult(isNA);
        String extText = determineExtText(isDummy);
        String errorMsg = "";
        return new PropertiesData(extText, extColor, testResult, errorMsg);
    }

    private class PropertiesData {
        private final String extText;
        private final String extColor;
        private final String testResult;
        private final String errorMsg;

        public PropertiesData(final String extText, final String extColor, final String testResult, final String errorMsg) {
            this.extText = extText;
            this.extColor = extColor;
            this.testResult = testResult;
            this.errorMsg = errorMsg;
        }

        public String getExtText() {
            return extText;
        }

        public String getExtColor() {
            return extColor;
        }

        public String getTestResult() {
            return testResult;
        }

        public String getErrorMsg() {
            return errorMsg;
        }

    }

    private boolean checkIfNaTestCase(final Test test, final TestResult result) {
        boolean isNA = false;
        if (test instanceof CommonTestCase) {
            CommonTestCase ctc = (CommonTestCase) test;
            isNA = ctc.isNA();
            if (isNA) {
                naCount++;
                o.increaseCountNA();
                AssertionFailedError ae = new AssertionFailedError();
                ae.initCause(ctc.getNaReason());
                result.addFailure(test, ae);
            }
        }
        return isNA;
    }

    private boolean checkIfDummyTestCase(final Test test) {
        boolean isDummy = false;
        if (test instanceof CommonTestCase) {
            isDummy = ((CommonTestCase) test).isDummy();
            if (isDummy) {
                dummyCount++;
                o.increaseCountDummy();
            }
        }
        return isDummy;
    }

    private Properties createProperties(final Test test, final Util u, final PropertiesData data, final String dataDrivenName) {
        Properties props = new Properties();
        props.setProperty("TestCase", ((TestCase) test).getName());
        props.setProperty("TestResult", data.getTestResult());
        props.setProperty("ErrorMsg", u.escapeHTML(data.getErrorMsg()));
        props.setProperty("TestResultColor", data.getExtColor());
        props.setProperty("DummyText", u.escapeHTML(data.getExtText()));
        props.setProperty("TCURL", dataDrivenName + ".html");
        props.setProperty("TCMethod", test.getClass().getName() + "." + ((TestCase) test).getName() + "()");
        return props;
    }

    private String determineExtText(final boolean isDummy) {
        String extText = "";
        if (isDummy) {
            extText = "(Dummy test)";
        }
        return extText;
    }

    private String determineTestResult(final boolean isNA) {
        String result;
        if (isNA) {
            result = "N/A";
        } else {
            result = "Passed";
        }
        return result;
    }

    private String determineExtColor(final boolean isDummy, final boolean isNA) {
        String extColor = "#00AA00";
        if (isNA) {
            extColor = "#0000AA";
        }
        if (isDummy) {
            extColor = "#999999";
        }
        return extColor;
    }

    private String determineFailureExtText(final boolean isDummy, final boolean isNA) {
        String extText = "";
        if (isNA) {
            extText = "N/A";
        }
        if (isDummy) {
            extText = "(Dummy test)";
        }
        return extText;
    }

    private String determineFailureExtColor(final boolean isDummy, final boolean isNA) {
        String extColor = "#AA0000";
        if (isNA) {
            extColor = "#AA0000";
        }
        if (isDummy) {
            extColor = "#999999";
        }
        return extColor;
    }

    /**
     * Returns the first test in a suite. If the suite is empty, it returns 'fake' test
     * with a warning message.
     *
     * @return with a fake TC.
     */
    protected Test getFakeInstance() {
        Enumeration<Test> tests = tests();
        Test ob = tests.nextElement();
        if (ob instanceof TestCase) {
            TestCase testCase = (TestCase) ob;
            if ("warning".equals(testCase.getName())) {
                return warningNoTestMethodFound();
            }
        }
        return ob;
    }

    /**
     * Returns a fake test, which does nothing but fails with the message in the parameter.
     *
     * @return with fake TC, that fails.
     */
    public static Test warningNoTestMethodFound() {
        return new CommonTestCase("Gepard: WARNING") {
            @Override
            protected void runTest() {
                fail("No test method found.");
            }
        };
    }
}
