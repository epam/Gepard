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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.epam.gepard.AllTestRunner;
import com.epam.gepard.annotations.TestClass;
import com.epam.gepard.common.Environment;
import com.epam.gepard.common.TestCaseExecutionData;
import com.epam.gepard.common.TestClassExecutionData;
import com.epam.gepard.common.threads.BlockingInfo;
import com.epam.gepard.common.threads.TestClassExecutionThread;
import com.epam.gepard.datadriven.DataDrivenParameterArray;
import com.epam.gepard.datadriven.DataFeederLoader;
import com.epam.gepard.filter.ExpressionTestFilter;
import com.epam.gepard.generic.helper.TestClassData;
import com.epam.gepard.util.ExitCode;

/**
 * This class loads the testlist file, evaluates it ad builds up the test suite to be executed.
 */
public class GenericListTestSuite extends TestSuite {

    /**
     * Gepard level global map, to store anything you believe is important to be stored during the tests.
     * So you may use it.
     * Uniqueness of the key is your responsibility. Having a key naming convention is useful.
     */
    private static Map<String, Object> globalDataStorage = Collections.synchronizedMap(new LinkedHashMap<String, Object>());

    /**
     * Gepard level global map, to store all the test classes those are executed.
     * Do NOT touch it, otherwise you will do nasty things. It is used internally by Gepard.
     */
    private static Map<String, TestClassExecutionData> testClassMap = new LinkedHashMap<>(); //global TestClass exec info

    /**
     * Gepard level global map, to store all the test cases those are executed.
     * Do NOT touch it, otherwise you will do nasty things. It is used internally by Gepard.
     */
    private static Map<String, TestCaseExecutionData> testCaseMap = new LinkedHashMap<>(); //global TestCase exec info

    private static final int TESTLIST_CLASS_NAME_FIELD = 0;
    private static final int TESTLIST_FEEDER_DESCRIPTOR_FIELD = 1;
    private static final int TESTLIST_TIMEOUT_FIELD = 2;
    private static final int TESTLIST_BLOCKER_FIELD = 3;

    private static final String DEFAULT_TIMEOUT_IN_SECS = "120";
    private int usedTc; // = 0; //number of used Test Classes - will be used at report
    private int numberTc; // = 0; //number of TCs

    /**
     * This class builds up the Junit Test suite (including loading data-driven tests).
     *
     * @param testListFile is the filename of the testlist file.
     * @param filter       is the filter of the test classes.
     * @throws IOException in case testlist file cannot be accessed properly
     * @throws ClassNotFoundException in case the specified feeder class is not available.
     */
    public GenericListTestSuite(final String testListFile, final ExpressionTestFilter filter) throws IOException, ClassNotFoundException {
        super("GenericListTestSuite");
        LineNumberReader listReader = new LineNumberReader(new InputStreamReader(new FileInputStream(testListFile)));
        String originalLine;
        while ((originalLine = listReader.readLine()) != null) {
            originalLine = originalLine.trim();
            if ("".equals(originalLine) || originalLine.startsWith("//") || originalLine.startsWith("#")) {
                continue;
            }
            String line = originalLine.replace(File.separatorChar, '.');
            // if: classname   -> 1 run is expected
            // if: classname,3 -> 3 run is expected
            // if: classname,,10 -> 1 run is expected, but class timeout is 10 secs
            // if: classname,3,10 -> 3 run is expected, + class timeout is 10 secs
            // if: classname,,,AAA -> AAA is used as a blocker id
            // if: classname,feederdescriptor,...-> loader class defines the number of execution and provides the tests
            String[] testDescriptor = line.split(",");
            Class<?> clazz = Class.forName(testDescriptor[TESTLIST_CLASS_NAME_FIELD]);
            //add as many classes to the stack as data driven approach requires
            if (TestCase.class.isAssignableFrom(clazz) && filter.accept(clazz)) {
                int count = 1;
                DataFeederLoader dataFeeder = null;
                if ((testDescriptor.length > TESTLIST_FEEDER_DESCRIPTOR_FIELD) && (!testDescriptor[TESTLIST_FEEDER_DESCRIPTOR_FIELD].isEmpty())) {
                    //this is a data driven TC
                    dataFeeder = new DataFeederLoader(clazz.getName(), testDescriptor[TESTLIST_FEEDER_DESCRIPTOR_FIELD]);
                    count = dataFeeder.calculateRuns(clazz.getName(), count);
                    DataDrivenParameterArray parameterArray = dataFeeder.calculateParameterArray(clazz.getName(), null);
                    dataFeeder.reserveParameterArray(parameterArray);
                }

                //detect timeout
                Integer timeout = detectTestClassTimeout(clazz, testDescriptor);

                //detect blocker class
                String blocker = null;
                if (testDescriptor.length > TESTLIST_BLOCKER_FIELD) {
                    //has blocker value
                    blocker = testDescriptor[TESTLIST_BLOCKER_FIELD];
                }

                //and add it to the suite
                TestClassData testClassData = new TestClassData(clazz, count, timeout, blocker);
                int tcMethods = add(testClassData, dataFeeder, originalLine);
                usedTc++; //count the used test classes
                numberTc += tcMethods; //count the real number of the used TCs (ignoring data driven duplications)
            }
        }
        listReader.close();
    }

    private Integer detectTestClassTimeout(final Class<?> clazz, final String[] testDescriptor) {
        String toString = Environment.getProperty(Environment.GEPARD_TEST_TIMEOUT, DEFAULT_TIMEOUT_IN_SECS);
        Integer timeout = Integer.valueOf(DEFAULT_TIMEOUT_IN_SECS);
        if ((testDescriptor.length > TESTLIST_TIMEOUT_FIELD) && (!testDescriptor[TESTLIST_TIMEOUT_FIELD].isEmpty())) {
            //has timeout value
            toString = testDescriptor[2];
        }
        try {
            timeout = Integer.valueOf(toString);
        } catch (NumberFormatException e) {
            AllTestRunner.CONSOLE_LOG.info("\nERROR: Bad Timeout value at Class in testlist: " + clazz.getName()
                    + "\nPlease fix the timeout data to be used in list!\nNow exiting...");
            AllTestRunner.exitFromGepard(ExitCode.EXIT_CODE_TEST_CLASS_BAD_TIMEOUT);
        }
        return timeout;
    }

    /**
     * Add a Test Class to the suite.
     *
     * @param testClassData holds the data necessary for adding the test class
     * @param dataFeeder   is the Data Feeder class that should be used to load the data-driven values.
     * @param originalLine is the original testlist row.
     * @return with the number of the registered test methods.
     */
    public int add(final TestClassData testClassData, final DataFeederLoader dataFeeder, final String originalLine) {
        int rowNo = 0;
        int counter = testClassData.getCount();
        int testMethods = 0;
        Class<?> cls = testClassData.getClassOfTestClass();
        String blocker = testClassData.getBlocker();
        while (counter > 0) {
            testMethods = registerMethodsInGlobalMap(cls, rowNo, testClassData.getTimeout(), dataFeeder);
            if (testMethods == 0) {
                //this should not happen, as this means no test method to execute within the test class.
                // exiting now with error.
                AllTestRunner.CONSOLE_LOG.info("\nERROR: No test method at Class in testlist: " + cls.getName()
                        + "\nPlease implement at least one test method!\nNow exiting...");
                AllTestRunner.exitFromGepard(ExitCode.EXIT_CODE_TEST_CLASS_WITHOUT_TEST_METHOD);
            }
            Test t = getTestForClass(cls);
            addTest(t);

            //set test for parallel execution
            String id = cls.getName() + "/" + rowNo;
            TestClassExecutionData classData = GenericListTestSuite.getTestClassExecutionData(id); //get the class exec object
            classData.setTC(t);
            //set blocker parameters
            String blockerString = null;
            boolean blockerSelfEnabled = false;
            if ((blocker != null) && (blocker.length() > 0)) {
                if (blocker.endsWith("*")) {
                    blockerString = blocker.substring(0, blocker.length() - 1);
                    blockerSelfEnabled = true;
                } else {
                    blockerString = blocker;
                }
                //take care about the blocker map, too
                if (!TestClassExecutionThread.containsClassBlockingInfo(blockerString)) {
                    //need a new blocker element
                    TestClassExecutionThread.putClassBlockingInfo(blockerString, new BlockingInfo());
                }
            }
            classData.setBlockerString(blockerString);
            classData.setSelfEnabledBlocker(blockerSelfEnabled);
            classData.setOriginalLine(originalLine);
            checkDataDrivenParameters(classData, dataFeeder);
            counter--;
            rowNo++;
        }
        return testMethods;
    }

    private void checkDataDrivenParameters(final TestClassExecutionData classData, final DataFeederLoader dataFeeder) {
        if (classData.getDrivenData() == null) { // this must not be data driven
            if (classData.getDrivenDataRowNo() > 0) {
                AllTestRunner.CONSOLE_LOG.info("\nERROR: Parameters are not loaded for a data driven test class."
                        + "\nPlease check and fix it!\nNow exiting...");
                AllTestRunner.exitFromGepard(ExitCode.EXIT_CODE_DATA_DRIVEN_TEST_CLASS_WITHOUT_DATA);
            }
            if (dataFeeder != null) {
                AllTestRunner.CONSOLE_LOG.info("\nERROR: DataFeederLoader is used on a non-data driven test class."
                        + "\nPlease check and fix it!\nNow exiting...");
                AllTestRunner.exitFromGepard(ExitCode.EXIT_CODE_NON_DATA_DRIVEN_TEST_CLASS_WITH_DATA);
            }
            return;
        }
        //we have parameters, now check its correctness
        String[] parameterNames = classData.getDrivenData().getParameterNames();
        if (parameterNames == null) { //we must have names for the parameters
            AllTestRunner.CONSOLE_LOG.info("\nERROR: Parameters are not loaded correctly for a data driven test class. ParameterNames are missing."
                    + "\nPlease check and fix it!\nNow exiting...");
            AllTestRunner.exitFromGepard(ExitCode.EXIT_CODE_DATA_DRIVEN_TEST_CLASS_WITHOUT_DATA_NAMES);
        }
        int columns = classData.getDrivenData().getParameters().length;
        int namesNo = parameterNames.length;
        if (columns != namesNo) { //we must have as many parameter names as parameters we have
            AllTestRunner.CONSOLE_LOG.info("\nERROR: Parameters are not loaded correctly for a data driven test class. "
                    + "Number of ParameterNames differs from the number of parameters." + "\nPlease check and fix it!\nNow exiting...");
            AllTestRunner.exitFromGepard(ExitCode.EXIT_CODE_DATA_DRIVEN_TEST_CLASS_INCORRECT_NUMBER_OF_DATA_NAMES);
        }
    }

    /**
     * This class ensures that the test class receives all data-driven (and other)information before its execution.
     *
     * @param clazz is the test class to be initialized.
     * @return a @GenericTestSuite class.
     */
    protected Test getTestForClass(final Class<?> clazz) {
        Test returnSuite = new TestSuite(clazz);
        try {
            final Method method = clazz.getDeclaredMethod("suite");
            //calls the static suite method of the test class, where the Enums are defined
            returnSuite = (Test) method.invoke(null); // this call loads the parameters!
        } catch (final NoSuchMethodException e) {
            // Handle annotated classes
            // More annotation handling should be added here to handle more test types
            if (clazz.isAnnotationPresent(TestClass.class)) {
                returnSuite = CommonTestCase.suiteHelper(clazz, clazz.getAnnotation(TestClass.class).id(), clazz.getAnnotation(TestClass.class)
                        .name(), null);
            } else {
                //no proper annotation at Test Class, cannot continue
                AllTestRunner.CONSOLE_LOG.info("\nERROR: @TestClass annotation is missing at class: " + clazz.getCanonicalName() + "Please fix!");
                AllTestRunner.exitFromGepard(ExitCode.EXIT_CODE_TEST_CLASS_ANNOTATION_MISSING);
            }
        } catch (final Exception e) {
            //something has happened during the data class load
            AllTestRunner.exitFromGepardWithCriticalException("\nERROR: Unknown error occurred, please fix!", e, true,
                    ExitCode.EXIT_CODE_TEST_CLASS_INITIALIZATION_ERROR);
        }
        return returnSuite;
    }

    /**
     * This class registers the test methods in the global map.
     *
     * @param clazz      in which class we search for the test methods.
     * @param rowNo      in case of data-driven test, when the test class is repeated, this specifies the actual repetition.
     * @param timeout    to be used as timeout for the test methods (inherits class timeouts).
     * @param dataFeeder is the data feeder class in order to load the proper test data.
     * @return the number of the registered test methods.
     */
    protected int registerMethodsInGlobalMap(final Class<?> clazz, final int rowNo, final int timeout, final DataFeederLoader dataFeeder) {
        //register this class in the global class list
        String id = clazz.getName() + "/" + rowNo;
        if (testClassMap.containsKey(id)) {
            //this is bad, this means ...
            AllTestRunner.CONSOLE_LOG.info("\nERROR: Duplicated Class found in testlist: " + clazz.getName()
                    + "\nPlease ensure that a class is listed only one time in the list!\nNow exiting...");
            AllTestRunner.exitFromGepard(ExitCode.EXIT_CODE_TEST_CLASS_DUPLICATED);
        }

        TestClassExecutionData classData = new TestClassExecutionData(id);
        classData.setTimeout(timeout);
        classData.setClassName(clazz.getName());
        classData.setDataFeederLoader(dataFeeder);
        classData.setDataRow(rowNo); //note: to load the parameters we just waiting for the paramnames
        testClassMap.put(id, classData);

        //register test methods, and ancestor test methods, too
        int testMethod = 0;
        Class<?> superClass = clazz;
        while (Test.class.isAssignableFrom(superClass)) {
            for (Method method : superClass.getDeclaredMethods()) {
                if (method.getName().startsWith("test")) {
                    String methodId = TestCaseExecutionData.constructID(clazz.getName(), method.getName(), Integer.toString(rowNo));
                    getTestCaseMap().put(methodId, new TestCaseExecutionData(methodId));
                    testMethod++; //count the test methods, i.e. the physical TCs
                }
            }
            superClass = superClass.getSuperclass();
        }

        return testMethod;
    }

    /**
     * Format a Calendar object as date.
     *
     * @param cal is the source of the calendar.
     * @return Date string
     */
    public String formatDate(final Calendar cal) {
        NumberFormat df = DecimalFormat.getInstance();
        df.setMinimumIntegerDigits(2);
        df.setMaximumIntegerDigits(2);
        return cal.get(Calendar.YEAR) + "-" + df.format((long) cal.get(Calendar.MONTH) + 1) + "-" + df.format(cal.get(Calendar.DATE));
    }

    /**
     * Format a Calendar object as date and time.
     *
     * @param cal is the source of the calendar.
     * @return Date and time string
     */
    public static String formatDateTime(final Calendar cal) {
        NumberFormat df = DecimalFormat.getInstance();
        df.setMinimumIntegerDigits(2);
        df.setMaximumIntegerDigits(2);
        return cal.get(Calendar.YEAR) + "-" + df.format((long) cal.get(Calendar.MONTH) + 1) + "-" + df.format(cal.get(Calendar.DATE)) + " "
                + df.format(cal.get(Calendar.HOUR_OF_DAY)) + ":" + df.format(cal.get(Calendar.MINUTE)) + ":" + df.format(cal.get(Calendar.SECOND));
    }

    public int getUsedTc() {
        return usedTc;
    }

    public void setUsedTc(final int usedTc) {
        this.usedTc = usedTc;
    }

    public int getNumberTc() {
        return numberTc;
    }

    public void setNumberTc(final int numberTc) {
        this.numberTc = numberTc;
    }

    public static Map<String, Object> getGlobalDataStorage() {
        return globalDataStorage;
    }

    public static void setGlobalDataStorage(final Map<String, Object> globalDataStorage) {
        GenericListTestSuite.globalDataStorage = globalDataStorage;
    }

    /**
     * Returns the {@link TestClassExecutionData} that belongs to the given id.
     * @param testClassId the given id of the test class
     * @return the {@link TestClassExecutionData} that belongs to the given id.
     */
    public static TestClassExecutionData getTestClassExecutionData(final String testClassId) {
        return testClassMap.get(testClassId);
    }

    public static Set<String> getTestClassIds() {
        return testClassMap.keySet();
    }

    public static int getTestClassCount() {
        return testClassMap.size();
    }

    public static Map<String, TestCaseExecutionData> getTestCaseMap() {
        return testCaseMap;
    }

    public static void setTestCaseMap(final Map<String, TestCaseExecutionData> testCaseMap) {
        GenericListTestSuite.testCaseMap = testCaseMap;
    }

    public static void setTestClassMap(final Map<String, TestClassExecutionData> testClassMap) {
        GenericListTestSuite.testClassMap =  new LinkedHashMap<>(testClassMap);
    }

}
