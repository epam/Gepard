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

import com.epam.gepard.annotations.TestParameter;
import com.epam.gepard.common.Environment;
import com.epam.gepard.common.NATestCaseException;
import com.epam.gepard.common.TestCaseExecutionData;
import com.epam.gepard.common.TestClassExecutionData;
import com.epam.gepard.exception.SimpleGepardException;
import com.epam.gepard.logger.LogFileWriter;
import com.epam.gepard.logger.helper.LogFileWriterFactory;
import com.epam.gepard.util.FileUtil;
import com.epam.gepard.util.ReflectionUtilsExtension;
import com.epam.gepard.util.Util;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

/**
 * Common Test case is the parent of any kind of Gepard test case.
 *
 * @author Ferenc Kemeny, Zsolt_Saskovy, Tamas Kohegyi
 */
public abstract class CommonTestCase extends TestCase {

    private static int actualDataRow; //this is used during the load of the tests, do NOT use it during execution
    private static String actualTestClassName; //this is used during the load of the tests, do NOT use it during execution
    private com.epam.gepard.inspector.TestCase tcase;
    /**
     * This is the main data storage of this test class. The storage can be reached via this id.
     * Filled by the constructor, never ever change it.
     */
    private final TestClassExecutionData classData;
    private LogFileWriter mainTestLogger;

    private int step = 1;
    private int divStep = 1; //used at show/hide divs
    private boolean notApplicable;
    private final Util util = new Util();
    private final LogFileWriterFactory logFileWriterFactory = new LogFileWriterFactory();

    private NATestCaseException naReason;

    /**
     * If a test case if not finished or you know it is not implemented as it should be
     * (for example the web functionality you should test is still not finished),
     * you can mark the test case as dummy. This will be reflected in the HTML log.
     */
    private boolean dummy;
    private FileUtil fileUtil = new FileUtil();

    /**
     * Use this constructor in case of not data driven tests.
     *
     * @param name is the given name of the test class
     */
    public CommonTestCase(final String name) {
        super(name);
        String testClassMapId = this.getClass().getName() + "/" + CommonTestCase.getActualDataRow();
        classData = GenericListTestSuite.getTestClassExecutionData(testClassMapId); //get the class exec object
    }

    /**
     * General suite method of the test class. Called by Gepard before any test execution.
     * Do not touch it, most of the cases this method should be unchanged.
     *
     * @param testClass      is the test class.
     * @param scriptID       is the ID of the script - may come from annotation.
     * @param scriptName     is the name of the script - may come from annotation.
     * @param parameterNames is the names of the parameters - may come from annotation too.
     * @param environment    holds the application properties
     * @return with the JUnit TestSuite object.
     */
    public static Test suiteHelper(final Class testClass, final String scriptID, final String scriptName, final String[] parameterNames,
                                   final Environment environment) {
        //first either reset the counter or increase it, based on the given test class name
        String testClassName = testClass.getName();
        if ((CommonTestCase.actualTestClassName == null) || (!CommonTestCase.actualTestClassName.contentEquals(testClassName))) {
            CommonTestCase.actualTestClassName = testClassName;
            CommonTestCase.setActualDataRow(0);
        } else {
            CommonTestCase.setActualDataRow(CommonTestCase.getActualDataRow() + 1);
        }
        //set script id and name
        String id = testClass.getName() + "/" + CommonTestCase.getActualDataRow();
        TestClassExecutionData classData = GenericListTestSuite.getTestClassExecutionData(id); //get the class exec object
        classData.setTestStriptId(scriptID);
        classData.loadParameters(parameterNames);
        String extension = "";
        if (classData.getDrivenData() != null) {
            extension = " - " + classData.getDrivenData().getParameters()[0]; //puts the first parameter into the test name
        }
        classData.setTestStriptName(scriptName + extension);

        return new GenericTestSuite(testClass, scriptID, scriptName + extension, environment);
    }

    /**
     * Set-ups the main test logger - the HTML logger.
     */
    protected void setUpLogger() {
        fileUtil.createDirectory(getProperty(Environment.GEPARD_HTML_RESULT_PATH) + "/" + readDirectory());
        String name;
        name = getName() + classData.getDrivenDataRowNo();
        mainTestLogger = logFileWriterFactory.createCustomWriter(getProperty(Environment.GEPARD_RESULT_TEMPLATE_PATH) + "/"
                        + "temp_generictestcase.html", getProperty(Environment.GEPARD_HTML_RESULT_PATH) + "/" + readDirectory() + "/" + name + ".html",
                classData.getEnvironment());
    }

    @Override
    public void run(final TestResult result) {
        step = 1;
        classData.addSysOut("\nRunning test: " + getClass().getName() + "." + getName() + "\nName: " + classData.getTestStriptName());
        super.run(result);
    }

    @Override
    //CHECKSTYLE.OFF
    protected void runTest() throws Throwable {
        //CHECKSTYLE.ON
        ExecutorService exec = Executors.newSingleThreadExecutor();
        Future<?> task = exec.submit(new Callable<Object>() {

            @Override
            public Object call() throws Exception {
                try {
                    CommonTestCase.super.runTest();
                } catch (Exception e) {
                    throw e;
                } catch (Throwable ex) {
                    throw (Error) ex;
                }
                return null;
            }
        });
        synchronized (classData) {
            classData.setTask(task); //now the time can interrupt it
        }
        try {
            task.get();
        } catch (ExecutionException ex) { //if the computation returns with exception
            throw ex.getCause();
        } catch (CancellationException e) { //the task execution is cancelled
            String txt = "Test Class Activity Timeout occurred.";
            throw new TimeoutException(txt);
        }
    }

    /**
     * Returns the name of the test case set.
     *
     * @return tcs name
     */
    public String getTestName() {
        return classData.getTestStriptName();
    }

    /**
     * Returns the ID of the test case set.
     *
     * @return tcs ID
     */
    public String getTestID() {
        return classData.getTestStriptId();
    }

    /**
     * Writes a message to the future console stream. Can be used for parallel exec.
     *
     * @param message is the string to be printed out
     */
    public void systemOutPrintLn(final String message) {
        classData.addSysOut(message);
    }

    /**
     * Get the exit status of the beforeTestCase call.
     * 0 = everything is ok.
     * 1 = ERROR/FAILURE happened.
     * 2 = N/A is requested
     *
     * @return with the info.
     */
    public int getBeforeTestCaseInfo() {
        return TestCaseExecutionData.getBeforeTestCaseInfo(this);
    }

    /**
     * Converts class name to directory path.
     *
     * @return directory path
     */
    public String readDirectory() {
        String pname = this.getClass().getName();
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

    /**
     * Write a comment message to the log, without the step number.
     *
     * @param comment Comment message
     */
    public void logComment(final String comment) {
        systemOutPrintLn(comment);
        mainTestLogger.insertText("<tr><td>&nbsp;</td><td bgcolor=\"#F0F0E0\">" + comment + "</td></tr>");
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

        mainTestLogger.insertText("<tr><td>&nbsp;</td><td bgcolor=\"#F0F0E0\">" + comment + addStr + "<div id=\"div_" + divStep
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

        mainTestLogger.insertText("<tr><td>&nbsp;</td><td bgcolor=\"#F0F0E0\">" + htmlComment + addStr + "<div id=\"div_" + divStep
                + "\" style=\"display:none\"><br>\n" + description + "</div></td></tr>\n");
        divStep++;
    }

    /**
     * Write an error message to the log, to the html output (different text can be used), without the step number, but with a description.
     *
     * @param comment     Comment message, to put to console
     * @param htmlComment same as the Comment message, but HTML formatted text
     * @param description is a multi-row string description for the comment.
     */
    public void logError(final String comment, final String htmlComment, final String description) {
        systemOutPrintLn(comment);

        String addStr = " <small>[<a href=\"javascript:showhide('div_" + divStep + "');\">details</a>]</small>";
        String message = "<tr><td>&nbsp;</td><td bgcolor=\"#F0F0E0\"><font color=\"#AA0000\">" + htmlComment + "</font>" + addStr + "<div id=\"div_" + divStep
                + "\" style=\"display:none\"><br>\n" + description + "</div></td></tr>";
        mainTestLogger.insertText(message);
        divStep++;
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
        if (getMainTestLogger() != null) {
            getMainTestLogger().insertText("<tr><td>&nbsp;</td><td bgcolor=\"#F0F0F0\">" + text + "</td></tr>\n");
        }
    }

    /**
     * Write an event message to the log.
     *
     * @param text        Event message
     * @param description Event description/info
     */
    public void logResult(final String text, final String description) {
        int step = getStep() + 1;
        if (getMainTestLogger() != null) {
            String addStr = " <small>[<a href=\"javascript:showhide('div_" + step + "');\">details</a>]</small>";
            getMainTestLogger().insertText("<tr><td>&nbsp;</td><td bgcolor=\"#F0F0F0\">" + text + addStr + "<div id=\"div_" + step
                    + "\" style=\"display:none\"><br>\n" + description + "</div></td></tr>\n");
        }

        increaseStep();
    }

    /**
     * Write a warning message to the log, without the step number.
     *
     * @param warning Comment message
     */
    public void logWarning(final String warning) {
        systemOutPrintLn("WARNING:" + warning);
        mainTestLogger.insertText("<tr><td>&nbsp;</td><td bgcolor=\"#F0D0D0\">" + warning + "</td></tr>");
    }

    /**
     * Put stack trace into the log.
     *
     * @param comment Comment message
     * @param t       is the exception object
     */
    void logStackTrace(final String comment, final Throwable t) {
        String description = "<code><small><br><pre>" + getFullStackTrace(t) + "</pre></small></code>";
        logComment(comment + " (dump stack trace)", description);
    }

    /**
     * Write a test step message to the log, and increase the step number.
     *
     * @param comment Comment message
     */
    public void logStep(final String comment) {
        String consoleComment = comment.replace('\uFF5F', '(').replace('\uFF60', ')'); //Unicode to Console (partial transfer)
        systemOutPrintLn(step + ". " + consoleComment);
        mainTestLogger.insertText("<tr><td align=\"center\">&nbsp;&nbsp;" + step + ".&nbsp;&nbsp;</td><td bgcolor=\"#E0E0F0\">" + comment
                + "</td></tr>\n");
        step++;
    }

    /**
     * Get the value of the specified property from the Environment class.
     *
     * @param name - refers to the name of property needed to return.
     * @return the value of the property represented as a String.
     */
    public String getProperty(final String name) {
        return classData.getEnvironment().getProperty(name);
    }

    /**
     * Get value of the Environment variable.
     *
     * @return the object that handles environment variables.
     */
    public Environment getEnvironment() {
        return classData.getEnvironment();
    }

    /**
     * Gets the full stack trace of a Throwable and returns it in HTML format.
     *
     * @param t is the throwable exception itself.
     * @return with the full stack trace, escaped for use in HTML.
     */
    protected String getFullStackTrace(final Throwable t) {
        return util.escapeHTML(Util.getStackTrace(t));
    }

    /**
     * Get the number of the actual row in case of data driven testing.
     *
     * @return with the value
     */
    public int getTestDataRow() {
        return classData.getDrivenDataRowNo();
    }

    /**
     * Get the values of the data parameters in a single array.
     *
     * @return with the parameter array
     */
    public String[] getTestParameters() {
        if (classData.getDrivenData() == null) {
            naTestCase("Test Script/Environment error: Non-Data Driven test tried to access Data Driven parameter.");
        }
        return classData.getDrivenData().getParameters();
    }

    /**
     * Test setup. This calculates the execution time of the individual test.
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
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
            logComment(
                    "Data Driven Test, with " + classData.getDrivenData().getParameters().length + " parameters. Data row:"
                            + classData.getDrivenDataRowNo(), s);
        }
        processTestParameterAnnotation();
        TestCaseExecutionData.setupOK(this);
    }

    /**
     * This is the second method at test setUp, should be called at XXXTestCase, where the setUp method must be final.
     * Ensures the call of beforeTestCase method, and ensures its safe execution.
     * Note: During beforeTestCase, N/A requests and failures are ignored.
     * In case you don1t need to execute beforeTestCase, no need to call it.
     */
    public final void setUp2() {
        boolean preservedNA = notApplicable;
        try {
            beforeTestCase();
            processBeforeAnnotation();
        } catch (NATestCaseException e) {
            TestCaseExecutionData.setBeforeTestCaseInfo(this, 2); //info about N/A
            logStackTrace("WARNING: N/A Request is ignored at beforeTestCase method. Original reason: " + e.getMessage(), e);
        } catch (Throwable e) {
            TestCaseExecutionData.setBeforeTestCaseInfo(this, 1); //info about Failure
            logStackTrace("WARNING: FAILURE is ignored at beforeTestCase method. Original reason: " + e.getMessage(), e);
        }
        notApplicable = preservedNA;
    }

    /**
     * jUnit tearDown should be used in order to clean up the test properly.
     */
    @Override
    protected void tearDown() throws Exception {
        TestCaseExecutionData.tearDownOK(this);
        super.tearDown();
    }

    /**
     * This is the first method at test tearDown, should be called at XXXTestCase, where the tearDown method must be final.
     * Ensures the call of afterTestCase method, and ensures its safe execution.
     * Note: During afterTestCase, N/A requests and failures are ignored.
     */
    public final void tearDown2() {
        boolean preservedNA = notApplicable;
        try {
            processAfterAnnotation();
            afterTestCase();
        } catch (NATestCaseException e) {
            logStackTrace("WARNING: N/A Request is ignored at afterTestCase method. Original reason: " + e.getMessage(), e);
        } catch (Throwable e) {
            logStackTrace("WARNING: FAILURE is ignored at afterTestCase method. Original reason: " + e.getMessage(), e);
        }
        notApplicable = preservedNA;
    }

    /**
     * Creates a test suite object - derived classes must override this method.
     *
     * @return test suite
     */
    public static Test suite() {
        return null;
    }

    /**
     * Initialization code executed on a dummy instance when the test case set starts.
     * Handles annotated @BeforeClass public [static] void method()
     * @throws java.lang.reflect.InvocationTargetException in case of problem with the @BeforeClass method call
     * @throws java.lang.IllegalAccessException in case of problem with the @BeforeClass method call
     */
    public final void beforeTestCaseSet() throws Throwable {
        Class<?> actual = this.getClass();
        do { // Processing test annotations
            for (Method method : actual.getDeclaredMethods()) {
                // calling @BeforeClass methods
                if (method.isAnnotationPresent(BeforeClass.class)) {
                    // if signature is ok, call it
                    if ((method.getReturnType().equals(Void.TYPE)) && (method.getGenericParameterTypes().length == 0)
                            && (Modifier.isPublic(method.getModifiers()))) {
                        try {
                            method.invoke(this);}
                        catch (InvocationTargetException e) {
                            throw e.getCause();
                        }
                    } else {
                        throw new SimpleGepardException("@BeforeClass method found with incorrect signature.");
                    }
                }
            }
            actual = actual.getSuperclass();
        } while (!actual.equals(Object.class));
    }

    /**
     * Cleanup code executed on a dummy instance when the test case set ends.
     * Handles annotated @AfterClass public [static] void method()
     * @throws java.lang.reflect.InvocationTargetException in case of problem with the @BeforeClass method call
     * @throws java.lang.IllegalAccessException in case of problem with the @BeforeClass method call
     */
    public final void afterTestCaseSet() throws Throwable {
        Class<?> actual = this.getClass();
        do { // Processing test annotations
            for (Method method : actual.getDeclaredMethods()) {
                // calling @AfterClass methods
                if (method.isAnnotationPresent(AfterClass.class)) {
                    // if signature is ok, call it
                    if ((method.getReturnType().equals(Void.TYPE)) && (method.getGenericParameterTypes().length == 0)
                            && (Modifier.isPublic(method.getModifiers()))) {
                        try {
                            method.invoke(this);}
                        catch (InvocationTargetException e) {
                            throw e.getCause();
                        }
                    } else {
                        throw new SimpleGepardException("@AfterClass method found with incorrect signature.");
                    }
                }
            }
            actual = actual.getSuperclass();
        } while (!actual.equals(Object.class));
    }

    /**
     * Initialization code executed on a dummy instance when the test case method starts.
     * Use this as PRECONDITION.
     * @deprecated - use @org.junit.Before annotation
     */
    @Deprecated
    public void beforeTestCase() {
    }

    /**
     * Initialization code executed on a dummy instance when the test case method ends.
     * Use this as POSTCONDITION.
     * @deprecated - use @org.junit.After annotation
     */
    @Deprecated
    public void afterTestCase() {
    }

    /**
     * Returns true if this is a dummy test case. Dummy test cases are unfinished testcases.
     * either because they're not fully written on because the functionality they supposed to
     * test is not ready.
     *
     * @return with the value of the dummy flag for this TC.
     */
    public boolean isDummy() {
        return dummy;
    }

    /**
     * Returns true if this is a N/A test case. N/A test cases are real tests with Not Applicable result.
     *
     * @return with the value of the NA flag for this TC.
     */
    public boolean isNA() {
        return notApplicable;
    }

    /**
     * Sets the notApplicable property.
     *
     * @param notApplicable is the value to set.
     */
    public void setNA(final boolean notApplicable) {
        this.notApplicable = notApplicable;
    }

    public NATestCaseException getNaReason() {
        return naReason;
    }

    /**
     * Sets the testcase. Dummy test cases are unfinished testcases
     * either because they're not fully written on because the functionality they supposed to
     * test is not ready.
     */
    protected void dummyTestCase() {
        dummy = true;
        logComment("This is a dummy test case");
    }

    /**
     * Sets the testcase. N/A test cases are tests with Not Applicable results.
     *
     * @param reason The reason why this TC is N/A
     */
    public void naTestCase(final String reason) {
        setNA(true);
        String comment = "This test case is N/A";
        if (reason != null) {
            comment = reason;
        }
        naReason = new NATestCaseException(comment);
        throw naReason;
    }

    /**
     * Initialize the variables that has {@link com.epam.gepard.annotations.TestParameter} annotation from the test parameters.
     *
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    private void processTestParameterAnnotation() throws Exception {
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

    private void processBeforeAnnotation() {
        Class<?> actual = this.getClass();
        do { // Processing @Before annotations
            for (Method method : actual.getMethods()) {
                // Setting test parameters
                if (method.isAnnotationPresent(Before.class)) {
                    //call it if ok, or log problem
                    if ((method.getGenericReturnType() == void.class) && (method.getGenericParameterTypes().length == 0)) {
                        try {
                            method.invoke(this);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            naTestCase("TEST CODE ERROR: " + e.getLocalizedMessage());
                        }
                    } else {
                        logComment("WARNING: cannot invoke method: " + method.getName() + " annotated with @Before, as its signature is not acceptable.");
                    }
                }
            }
            actual = actual.getSuperclass();
        } while (!actual.equals(Object.class));
    }

    private void processAfterAnnotation() {
        Class<?> actual = this.getClass();
        do { // Processing @After annotations
            for (Method method : actual.getMethods()) {
                // Setting test parameters
                if (method.isAnnotationPresent(After.class)) {
                    //call it if ok, or log problem
                    if ((method.getGenericReturnType() == void.class) && (method.getGenericParameterTypes().length == 0)) {
                        try {
                            method.invoke(this);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            naTestCase("TEST CODE ERROR: " + e.getLocalizedMessage());
                        }
                    } else {
                        logComment("WARNING: cannot invoke method: " + method.getName() + " annotated with @After, as its signature is not acceptable.");
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
     * Gets the actual test step value.
     *
     * @return with the actual test step value.
     */
    public int getStep() {
        return step;
    }

    /**
     * Step one ahead in the test steps.
     */
    public void increaseStep() {
        step++;
    }

    /**
     * Gets the step value used in HTML divs - used to hold explanations for a comments/events.
     *
     * @return with the actual value.
     */
    public int getDivStep() {
        return divStep;
    }

    /**
     * Step one ahead for HTML divs.
     */
    public void increaseDivStep() {
        divStep++;
    }

    /**
     * Returns with the actual data row used from the full data array, by the specific TC.
     *
     * @return with the number.
     */
    public static int getActualDataRow() {
        return actualDataRow;
    }

    /**
     * Sets the actual data row within the data array for this TC.
     *
     * @param actualDataRow is the specific row.
     */
    public static void setActualDataRow(final int actualDataRow) {
        CommonTestCase.actualDataRow = actualDataRow;
    }

    protected com.epam.gepard.inspector.TestCase getTcase() {
        return tcase;
    }

    protected void setTcase(final com.epam.gepard.inspector.TestCase tcase) {
        this.tcase = tcase;
    }

    protected TestClassExecutionData getClassData() {
        return classData;
    }

    protected LogFileWriter getMainTestLogger() {
        return mainTestLogger;
    }

    protected void setMainTestLogger(final LogFileWriter mainTestLogger) {
        this.mainTestLogger = mainTestLogger;
    }
}
