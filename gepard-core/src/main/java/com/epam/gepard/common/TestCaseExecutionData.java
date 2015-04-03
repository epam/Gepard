package com.epam.gepard.common;
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

import java.util.Date;

import com.epam.gepard.AllTestRunner;
import com.epam.gepard.generic.CommonTestCase;
import com.epam.gepard.generic.GenericListTestSuite;
import com.epam.gepard.util.ExitCode;

/**
 * This class holds Test Case execution information.
 * The purpose is:
 * - time measurements
 * - TC exec status
 * - dependency handling
 * - info on how the beforeTestCase is executed
 *
 * @author Tamas_Kohegyi
 */
public final class TestCaseExecutionData {

    /**
     * Store the execution start date.
     */
    private Date execStartDate;
    /**
     * Store the execution end date.
     */
    private Date execEndDate;

    /**
     * Store the startup info.
     */
    private boolean startProper;
    /**
     * Store the tearing down info.
     */
    private boolean endProper;

    /**
     * Store the startup info.
     */
    private int exitStatus; //default is OK

    /**
     * Store the dependency info.
     */
    private boolean dependencyReady;

    /**
     * Store the beforeTestCase exit status.
     * 0 = everything is ok.
     * 1 = ERROR/FAILURE happened.
     * 2 = N/A is requested
     */
    private int beforeTestCase; //default 0 is OK

    /**
     * Store the data to be transferred.
     */
    private Object o;
    private String id;

    /**
     * This class hold status information about the running test class, like duration of the running test.
     *
     * @param id is the test Method ID within Gepard.
     */
    public TestCaseExecutionData(String id) {
        this.id = id;
    }

    public Object getObject() {
        return o;
    }


    /**
     * Get the exit status of the beforeTestCase call.
     * 0 = everything is ok.
     * 1 = ERROR/FAILURE happened.
     * 2 = N/A is requested
     *
     * @param tc is the relevant Test Class.
     * @return with the info.
     */
    public static int getBeforeTestCaseInfo(CommonTestCase tc) {
        String id = constructID(tc.getClass().getName(), tc.getName(), Integer.toString(tc.getTestDataRow()));
        TestCaseExecutionData data = GenericListTestSuite.getTestCaseMap().get(id);
        if (data == null) {
            AllTestRunner.exitFromGepard(ExitCode.EXIT_CODE_THIS_SHOULD_NOT_HAPPEN_CONTACT_MAINTAINERS); //fatal error
        }
        return data.beforeTestCase;
    }

    /**
     * Set status of the beforeTestCase call.
     * Called by Gepard only, do NOT call it from the test code!
     *
     * @param tc   is the caller test class.
     * @param info is the exit status to be stored.
     */
    public static void setBeforeTestCaseInfo(CommonTestCase tc, int info) {
        String id = constructID(tc.getClass().getName(), tc.getName(), Integer.toString(tc.getTestDataRow()));
        TestCaseExecutionData data = GenericListTestSuite.getTestCaseMap().get(id);
        if (data == null) {
            AllTestRunner.exitFromGepard(ExitCode.EXIT_CODE_THIS_SHOULD_NOT_HAPPEN_CONTACT_MAINTAINERS); //fatal error
        }
        data.beforeTestCase = info;
    }

    public Date getEndDate() {
        return execEndDate;
    }

    public Date getStartDate() {
        return execStartDate;
    }

    public boolean isProperStart() {
        return startProper;
    }

    public boolean isProperEnd() {
        return endProper;
    }

    public int getExitStatus() {
        return exitStatus;
    }

    public boolean isDependencyReady() {
        return dependencyReady;
    }

    public String getID() {
        return id;
    }

    /**
     * Constructs the ID for the specific test class/method/datarow.
     *
     * @param tcName     is the TC class name the TC belongs to.
     * @param methodName is the name of the test method itself.
     * @param row        is the name of the actual data row. Think about data driven tests, this should be info on which row is running.
     * @return with the generated idString to be used to identify a specific TC
     */
    public static String constructID(final String tcName, final String methodName, final String row) {
        return tcName + "/" + methodName + "/" + row;
    }

    /**
     * Call this method if test initialization was successful.
     * @param tc is the running TC.
     */
    public static void setupOK(final CommonTestCase tc) {
        String id = constructID(tc.getClass().getName(), tc.getName(), Integer.toString(tc.getTestDataRow()));
        TestCaseExecutionData data = GenericListTestSuite.getTestCaseMap().get(id);
        if (data == null) {
            AllTestRunner.exitFromGepard(ExitCode.EXIT_CODE_THIS_SHOULD_NOT_HAPPEN_CONTACT_MAINTAINERS); //fatal error
        }
        data.startProper = true;
        data.execStartDate = new Date();
    }

    /**
     * Call this method to set the exit status of the test.
     * @param tc is the running test
     * @param exitStatus is the exit status.
     */
    public static void setExitStatus(final CommonTestCase tc, final int exitStatus) {
        String id = constructID(tc.getClass().getName(), tc.getName(), Integer.toString(tc.getTestDataRow()));
        TestCaseExecutionData data = GenericListTestSuite.getTestCaseMap().get(id);
        if (data == null) {
            AllTestRunner.exitFromGepard(ExitCode.EXIT_CODE_THIS_SHOULD_NOT_HAPPEN_CONTACT_MAINTAINERS); //fatal error
        }
        data.exitStatus = exitStatus;
    }

    /**
     * Call this method if test finalization was successful.
     * @param tc is the running test.
     */
    public static void tearDownOK(final CommonTestCase tc) {
        String id = constructID(tc.getClass().getName(), tc.getName(), Integer.toString(tc.getTestDataRow()));
        TestCaseExecutionData data = GenericListTestSuite.getTestCaseMap().get(id);
        if (data == null) {
            AllTestRunner.exitFromGepard(ExitCode.EXIT_CODE_THIS_SHOULD_NOT_HAPPEN_CONTACT_MAINTAINERS); //fatal error
        }
        data.endProper = true;
        data.execEndDate = new Date();
    }

    /**
     * Gives info to the global TC dependency handler, that this TC is ok, other TCs can depend on it now.
     * Also stores an object in a global map to allow data transfer between TCs.
     * WARNING: Call this at the very end of the test, when it is clear that the TC will pass.
     *
     * @param tc               is the caller TC.
     * @param objectToTransfer is an object which may be transferred between the TCs.
     */
    public static void setDependencyOK(final CommonTestCase tc, final Object objectToTransfer) {
        String id = constructID(tc.getClass().getName(), tc.getName(), Integer.toString(tc.getTestDataRow()));
        TestCaseExecutionData data = GenericListTestSuite.getTestCaseMap().get(id);
        if (data == null) {
            AllTestRunner.exitFromGepard(ExitCode.EXIT_CODE_THIS_SHOULD_NOT_HAPPEN_CONTACT_MAINTAINERS); //fatal error
        }
        data.o = objectToTransfer;
        data.dependencyReady = true;
    }

    /**
     * Store an object in a global map to allow data transfer between TCs.
     * WARNING: do not use if setDependencyOK method is used in the same TC, too.
     *
     * @param tc               is the caller TC.
     * @param objectToTransfer is an object which may be transferred between the TCs.
     */
    public static void setObjectToTransfer(final CommonTestCase tc, final Object objectToTransfer) {
        String id = constructID(tc.getClass().getName(), tc.getName(), Integer.toString(tc.getTestDataRow()));
        TestCaseExecutionData data = GenericListTestSuite.getTestCaseMap().get(id);
        if (data == null) {
            AllTestRunner.exitFromGepard(ExitCode.EXIT_CODE_THIS_SHOULD_NOT_HAPPEN_CONTACT_MAINTAINERS); //fatal error
        }
        data.o = objectToTransfer;
    }

    /**
     * Handle dependency on another TC.
     *
     * @param tc          is the caller TC.
     * @param idString    is the idString of the TC this caller TC depends on.
     * @param timeoutSec  is the timeout till this method is waiting for the other TC to be ready.
     * @param distanceSec is the expected distance between the previous TC ended (after ready method is called) and this TC continues.
     * @return with the transferred object, if any.
     * @throws InterruptedException in case of error during the waits.
     */
    public static Object dependsOn(final CommonTestCase tc, final String idString, long timeoutSec, long distanceSec) throws InterruptedException {
        tc.logComment("Handling dependency on TC: " + idString
                + ", with Timeout: " + timeoutSec + "sec, and Distance from the previous TC: " + distanceSec + "sec.");
        Date startDate = new Date(System.currentTimeMillis()); //used for timeout calculation
        Date timeout = new Date(startDate.getTime() + timeoutSec * GepardConstants.ONE_SECOND_LENGTH.getConstant());
        if (!GenericListTestSuite.getTestCaseMap().containsKey(idString)) {
            //it is not in the map
            CommonTestCase.fail("This TC depends on a non existing TC, please ensure the existence of that TC first! Referenced TC Id:" + idString);
        }
        while (true) {
            TestCaseExecutionData data = GenericListTestSuite.getTestCaseMap().get(idString);
            if (data.execEndDate == null) {
                //it is not yet started or still running, so we are waiting for the timeout
                if ((new Date(System.currentTimeMillis())).after(timeout)) {
                    //timeout has happened. we should consider it as failure of the previous TC, therefore this TC should be N/A
                    tc.naTestCase("Timeout has happened, the TC we are waiting for is failed or still running. Setting therefore this TC as N/A.");
                } else {
                    Thread.sleep(GepardConstants.ONE_SECOND_LENGTH.getConstant()); //wait for a sec
                }
            } else {
                //end endDate is not null, therefore the previous TC is ended!
                //now see if it is ok to depend on
                if (!data.dependencyReady) {
                    //ups. it is ready, but probably failed. Need to exit with N/A.
                    tc.naTestCase("The TC we are depending on is executed, but did not give permission to continue. Setting therefore this TC as N/A.");
                }
                //everything is ok now wait for the distance and continue
                Date d = data.getEndDate();
                if (d == null) {
                    //ups, we have no info on when it was finished, cannot wait for distance ...
                    CommonTestCase.fail("No information on when the previous TC finished, COMMON MODULE ERROR, please contact to TAEG! Referenced TC Id:" + idString);
                }
                Date distance = new Date(d.getTime() + distanceSec * GepardConstants.ONE_SECOND_LENGTH.getConstant());
                if ((new Date(System.currentTimeMillis())).after(distance)) {
                    //distance is reached, may continue the test
                    Date readyBy = new Date(System.currentTimeMillis());
                    long elapsed = readyBy.getTime() - startDate.getTime();
                    tc.logComment("Dependency is ok, we waited for the previous TC for: " + elapsed + "msecs.");
                    return data.getObject(); // EXIT from this method, and continue the test now
                }
                Thread.sleep(GepardConstants.ONE_SECOND_LENGTH.getConstant()); //wait for a sec
            }
        }
    }

}

