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
import com.epam.gepard.common.TestClassExecutionData;
import com.epam.gepard.generic.GenericResult;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.io.File;

/**
 * This reporter generates HML page for the class.
 *
 * @author Taams Kohegyi
 */
public final class HtmlRunReporter extends RunListener {

    File logPath;
    TestClassExecutionData classData;

    /**
     * Set-up the HTML logger.
     * @param logPath is the path of the xml file.
     */
    public HtmlRunReporter(final File logPath, TestClassExecutionData classData) {
        this.logPath = logPath;
        this.classData = classData;
        Class<?> clazz = classData.getTestClass();
        if (clazz.isAnnotationPresent(TestClass.class)) {
            classData.setTestStriptId(clazz.getAnnotation(TestClass.class).id());
            classData.setTestStriptName(clazz.getAnnotation(TestClass.class).name());
        }
        GenericResult gr = new GenericResult(classData.getTestStriptId(), classData.getTestStriptName());

    }

    @Override
    public void testFinished(final Description description) throws Exception {
        AllTestRunner.CONSOLE_LOG.info("testFinished");
    }

    @Override
    public void testRunFinished(final Result result) throws Exception {
        AllTestRunner.CONSOLE_LOG.info("testRunFinished RESULT ARRIVED");
    }

    @Override
    public void testRunStarted(final Description description) throws Exception {
        AllTestRunner.CONSOLE_LOG.info("testRunStarted");
    }

    @Override
    public void testStarted(final Description description) throws Exception {
        AllTestRunner.CONSOLE_LOG.info("testStarted");
    }

    @Override
    public void testFailure(final Failure failure) throws Exception {
        AllTestRunner.CONSOLE_LOG.info("failure: " + failure.getDescription().getTestClass().getCanonicalName());
    }

    @Override
    public void testIgnored(final Description description) throws Exception {
        AllTestRunner.CONSOLE_LOG.info("testIgnored" + description.getTestClass().getCanonicalName() + " IGNORED");
    }

}
