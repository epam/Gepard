package com.epam.gepard.logger;

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
import com.epam.gepard.annotations.TestClass;
import com.epam.gepard.annotations.TestParameter;
import com.epam.gepard.common.Environment;
import com.epam.gepard.common.TestClassExecutionData;
import com.epam.gepard.generic.GenericResult;
import com.epam.gepard.logger.helper.LogFileWriterFactory;
import com.epam.gepard.util.FileUtil;
import com.epam.gepard.util.ReflectionUtilsExtension;
import com.epam.gepard.util.Util;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Properties;

/**
 * This reporter generates HML page for the class.
 *
 * @author Taams Kohegyi
 */
public final class HtmlRunReporter extends RunListener {

    TestClassExecutionData classData;
    private String classDir = "";
    private Environment environment;
    private LogFileWriter testClassHtmlLog; //per test class
    private LogFileWriter testMethodHtmlLog; //per test case
    private final LogFileWriterFactory logFileWriterFactory = new LogFileWriterFactory();
    int step; //test step, restarts from 1 in every executed test method
    int divStep = 1;
    GenericResult gr;
    Properties props = new Properties();
    boolean testFailed = false;
    private FileUtil fileUtil = new FileUtil();


    /**
     * Set-up the HTML logger.
     *
     * @param classData is the Test class object within Gepard.
     */
    public HtmlRunReporter(final TestClassExecutionData classData) {
        this.classData = classData;
        this.environment = classData.getEnvironment();
        Class<?> clazz = classData.getTestClass();
        unPackClassNameAndDir(clazz);
        fileUtil.createDirectory(environment.getProperty(Environment.GEPARD_HTML_RESULT_PATH) + "/" + classDir);
        classData.setTestURL(getTestURL());

        if (clazz.isAnnotationPresent(TestClass.class)) {
            classData.setTestStriptId(clazz.getAnnotation(TestClass.class).id());
            classData.setTestStriptName(clazz.getAnnotation(TestClass.class).name());
        }
        gr = new GenericResult(classData.getTestStriptId(), classData.getTestStriptName());
        classData.setTestCaseSet(Environment.createTestCaseSet(gr.getName(), Environment.getScript()));
    }

    /**
     * Called when a Test Case Set = Test Class is started by the executor
     * @param description
     * @throws Exception
     */
    @Override
    public void testRunStarted(final Description description) throws Exception {
        String dataDrivenName = getDataDrivenFullClassName();
        AllTestRunner.CONSOLE_LOG.info("testRunStarted" + dataDrivenName);
        testClassHtmlLog = new LogFileWriter(environment.getProperty(Environment.GEPARD_RESULT_TEMPLATE_PATH) + "/" + "temp_generictestsuite.html",
                environment.getProperty(Environment.GEPARD_HTML_RESULT_PATH) + "/" + classDir + "/" + getDataDrivenSimpleClassName() + ".html", environment);
        props.setProperty("ID", gr.getID());
        props.setProperty("Name", gr.getName());
        testClassHtmlLog.insertBlock("Header", props);
        testClassHtmlLog.insertBlock("TableHead", props);
        initDataDrivenLogAndAnnotations();
    }

    @Override
    public void testStarted(final Description description) throws Exception {
        AllTestRunner.CONSOLE_LOG.info("testStarted");
        fileUtil.createDirectory(environment.getProperty(Environment.GEPARD_HTML_RESULT_PATH) + "/" + readDirectory());
        String methodName = description.getMethodName();
        testMethodHtmlLog = logFileWriterFactory.createCustomWriter(environment.getProperty(Environment.GEPARD_RESULT_TEMPLATE_PATH) + "/"
                        + "temp_generictestcase.html", environment.getProperty(Environment.GEPARD_HTML_RESULT_PATH) + "/"
                        + readDirectory() + "/" + methodName + classData.getDrivenDataRowNo() + ".html",
                classData.getEnvironment());
        step = 1;
        classData.addSysOut("\nRunning test: " + classData.getClassName() + "." + methodName + "\nName: " + classData.getTestStriptName());
        testFailed = false;
        Properties props = new Properties();
        props.setProperty("ID", classData.getTestStriptId());
        props.setProperty("Name", classData.getTestStriptName());
        props.setProperty("TestCase", methodName);
        props.setProperty("ScriptNameRow", getDataDrivenFullClassName());
        testMethodHtmlLog.insertBlock("Header", props);
    }

    @Override
    public void testFailure(final Failure failure) throws Exception {
        AllTestRunner.CONSOLE_LOG.info("failure: " + failure.getDescription().getTestClass().getCanonicalName());
        testFailed = true;
    }

    @Override
    public void testIgnored(final Description description) throws Exception {
        AllTestRunner.CONSOLE_LOG.info("testIgnored" + description.getTestClass().getCanonicalName() + " IGNORED");
    }

    @Override
    public void testFinished(final Description description) throws Exception {
        AllTestRunner.CONSOLE_LOG.info("testFinished");
        //After running the test case
        Util u = new Util();
        boolean isDummy = false; //checkIfDummyTestCase(test);
        boolean isNA = false; //checkIfNaTestCase(test, result);
        String dataDrivenName = getDataDrivenSimpleClassName() + "/" + description.getMethodName() + classData.getDrivenDataRowNo();
        Properties props;
        if (isNA) { //NA test case
            PropertiesData data = createPropertiesData(isDummy, true);
            props = createProperties(classData, description.getMethodName(), u, data, dataDrivenName);
            logEvent("<font color=\"#0000AA\"><b>N/A</b></font>");
            systemOutPrintLn("Test is N/A.");
        } else if (testFailed) {  // failed test case
            //The test failed (note that tr contains only ONE failure or error).
            classData.increaseCountFailed();
            String errorMsg = "---No error message---";
            PropertiesData data = createFailurePropertiesData(isDummy, false, errorMsg);
            props = createProperties(classData, description.getMethodName(), u, data, dataDrivenName);
            logEvent("<font color=\"#AA0000\"><b>Test failed.</b></font>");
            systemOutPrintLn("Test failed.");
        } else { // passed test case
            PropertiesData data = createPropertiesData(isDummy, false);
            props = createProperties(classData, description.getMethodName(), u, data, dataDrivenName);
            classData.increaseCountPassed();
            logEvent("<font color=\"#00AA00\"><b>Test passed.</b></font>");
            systemOutPrintLn("Test passed.");
        }
        testClassHtmlLog.insertBlock("TestRow", props);
        testMethodHtmlLog.insertBlock("Footer", null);
        testMethodHtmlLog.close();
        testMethodHtmlLog = null;
    }

    /**
     * Called when a Test Case Sat = TestClass execution is finished by the executor.
     * @param result
     * @throws Exception
     */
    @Override
    public void testRunFinished(final Result result) throws Exception {
        AllTestRunner.CONSOLE_LOG.info("testRunFinished RESULT ARRIVED");
        if (result.getRunCount() == 0) {
            testClassHtmlLog.insertBlock("NoTestCases", null);
        }
        testClassHtmlLog.insertBlock("TableEnd", props);
        testClassHtmlLog.insertBlock("Footer", null);
        classData.getTestCaseSet().updateStatus();
    }


    private void initDataDrivenLogAndAnnotations() {
        Util util = new Util();
        if ((classData.getDrivenData() != null) && (classData.getDrivenData().getParameters() != null)) {
            //we have parameters to be logged, so build the text
            String s = "<table border=\"1\"><tr><td><b>Parameter Name</b></td><td><b>Parameter Value</b></td></tr>";
            for (int i = 0; i < classData.getDrivenData().getParameters().length; i++) {
                String name = classData.getDrivenData().getParameterName(i);
                String parValue = util.escapeHTML(classData.getDrivenData().getTestParameter(name));
                parValue = parValue.replaceAll("\n", "<br/>").replaceAll(" ", "&nbsp;");
                s = s + "<tr><td>" + util.escapeHTML(name) + "</td><td>" + parValue + "</td></tr>";
            }
            s = s + "</table>";
            logComment("Data Driven Test, with " + classData.getDrivenData().getParameters().length
                    + " parameters. Data row:" + classData.getDrivenDataRowNo(), s);
        }
        processTestParameterAnnotation();
    }

    /**
     * Write a comment message to the log, without the step number, but with a description.
     * Can be used to dump stack trace for example.
     *
     * @param comment     Comment message
     * @param description is a multi-row string description for the comment.
     */
    void logComment(final String comment, final String description) {
        systemOutPrintLn(comment);

        String addStr = " <small>[<a href=\"javascript:showhide('div_" + divStep + "');\">details</a>]</small>";

        testMethodHtmlLog.insertText("<tr><td>&nbsp;</td><td bgcolor=\"#F0F0E0\">" + comment + addStr + "<div id=\"div_" + divStep
                + "\" style=\"display:none\"><br>\n" + description + "</div></td></tr>\n");
        divStep++;
    }

    /**
     * Write a comment message to the log, to the html output (different text can be used), without the step number, but with a description.
     *
     * @param comment     Comment message, to put to console
     * @param htmlComment same as the Comment message, but HTML formatted text
     * @param description is a multi-row string description for the comment.
     */
    public void logComment(final String comment, final String htmlComment, final String description) {
        systemOutPrintLn(comment);

        String addStr = " <small>[<a href=\"javascript:showhide('div_" + divStep + "');\">details</a>]</small>";

        testMethodHtmlLog.insertText("<tr><td>&nbsp;</td><td bgcolor=\"#F0F0E0\">" + htmlComment + addStr + "<div id=\"div_" + divStep
                + "\" style=\"display:none\"><br>\n" + description + "</div></td></tr>\n");
        divStep++;
    }

    /**
     * Initialize the variables that has {@link com.epam.gepard.annotations.TestParameter} annotation from the test parameters.
     */
    private void processTestParameterAnnotation() {
        Class<?> actual = this.getClass();
        do { // Processing @TestParameter annotations
            for (Field field : actual.getDeclaredFields()) {
                // Setting test parameters
                if (field.isAnnotationPresent(TestParameter.class)) {
                    // Feeder or normal parameter
                    if (classData.getDataFeederLoader() != null) {
                        String key = ("".equals(field.getAnnotation(TestParameter.class).id())) ? field.getName() : field.getAnnotation(
                                TestParameter.class).id();
                        setTestParameter(field, classData.getDrivenData().getTestParameter(key));
                    } else {
                        logComment("WARNING: No data feeder but there are test parameters! Check the testlist file - 'classname,X' need to be used to have parameters.");
                    }
                }
            }
            actual = actual.getSuperclass();
        } while (!actual.equals(Object.class));
    }

    /**
     * Sets the test parameter.
     *
     * @param field the field to be set
     * @param value with this value
     */
    private void setTestParameter(final Field field, final String value) {
        Object fieldValue;
        ReflectionUtils.makeAccessible(field);

        try {
            // Converting the value for the appropriate type
            if ("".equals(field.getAnnotation(TestParameter.class).separator())) {
                fieldValue = ReflectionUtilsExtension.valueOf(field.getType(), value);
            } else {
                fieldValue = ReflectionUtilsExtension.valueOf(field, value, field.getAnnotation(TestParameter.class).separator());
            }

            //Setting the field
            ReflectionUtils.setField(field, this, fieldValue);
        } catch (Exception e) {
            logComment("No test parameter for field: " + field.getName());
        }
    }

    /**
     * Write a comment message to the log, without the step number.
     *
     * @param comment Comment message
     */
    public void logComment(final String comment) {
        systemOutPrintLn(comment);
        testMethodHtmlLog.insertText("<tr><td>&nbsp;</td><td bgcolor=\"#F0F0E0\">" + comment + "</td></tr>");
    }

    /**
     * Write an event message to the log.
     *
     * @param text Event message
     */
    public void logEvent(final String text) {
        if (!text.startsWith("<font")) {
            systemOutPrintLn(text);
        }
        testMethodHtmlLog.insertText("<tr><td>&nbsp;</td><td bgcolor=\"#F0F0F0\">" + text + "</td></tr>\n");
    }

    /**
     * Writes a message to the future console stream. Can be used for parallel exec.
     *
     * @param message is the string to be printed out
     */
    public void systemOutPrintLn(final String message) {
        classData.addSysOut(message);
    }

    private Properties createProperties(final TestClassExecutionData testData, final String methodName, final Util u, final PropertiesData data, final String dataDrivenName) {
        Properties props = new Properties();
        props.setProperty("TestCase", methodName);
        props.setProperty("TestResult", data.getTestResult());
        props.setProperty("ErrorMsg", u.escapeHTML(data.getErrorMsg()));
        props.setProperty("TestResultColor", data.getExtColor());
        props.setProperty("DummyText", u.escapeHTML(data.getExtText()));
        props.setProperty("TCURL", dataDrivenName + ".html");
        props.setProperty("TCMethod", testData.getClassName() + "." + methodName + "()");
        return props;
    }

    private PropertiesData createPropertiesData(final boolean isDummy, final boolean isNA) {
        String extColor = determineExtColor(isDummy, isNA);
        String testResult = determineTestResult(isNA);
        String extText = determineExtText(isDummy);
        String errorMsg = "";
        return new PropertiesData(extText, extColor, testResult, errorMsg);
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

    /**
     * Converts class name to directory path.
     *
     * @return directory path
     */
    public String readDirectory() {
        String pname = classData.getClassName();
        String[] st = pname.split("\\.");
        String name = "";
        if ((st[0] != null) && !("test".equals(st[0]))) {
            name = name + st[0] + "/";
        }
        int i = 1;
        while (i < st.length) {
            name = name + st[i];
            if (i < st.length - 1) {
                name = name + "/";
            } else {
                name = name + classData.getDrivenDataRowNo() + "/";
            }
            i++;
        }
        return name;
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

    /**
     * Builds up the so called Data Driven ClassName, which is used at report generation.
     * In case the test is data driven (and therefore called several times), to ensure that the result shows all the runs individually,
     * the ClassName that is used to build up the result path should be driven by the number of the data rows currently used.
     *
     * @return the Data driven class name, i.e. returns with the classname + the actual data row.
     */
    private String getDataDrivenFullClassName() {
        return classData.getClassName() + classData.getDrivenDataRowNo();
    }

    /**
     * Builds up the so called Data Driven ClassName, which is used at report generation.
     * In case the test is data driven (and therefore called several times), to ensure that the result shows all the runs individually,
     * the ClassName that is used to build up the result path should be driven by the number of the data rows currently used.
     *
     * @return the Data driven class name, i.e. returns with the classname + the actual data row.
     */
    private String getDataDrivenSimpleClassName() {
        return classData.getTestClass().getSimpleName() + classData.getDrivenDataRowNo();
    }

    /**
     * Sets classDir attribute to the proper path of the given class.
     *
     * @param theClass Class to use
     */
    private void unPackClassNameAndDir(final Class<?> theClass) {
        String className = "";
        String pname = theClass.getName();

        String[] st = pname.split("\\.");

        if (st[0] != null) {
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
     * Returns test HTML log file path.
     *
     * @return path
     */
    public String getTestURL() {
        String dataDrivenName = getDataDrivenSimpleClassName();
        return classDir + ("".equals(classDir) ? "" : "/") + dataDrivenName + ".html";
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

}
