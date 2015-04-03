package com.epam.gepard.inspector.dummy;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import com.epam.gepard.inspector.Constants;
import com.epam.gepard.inspector.Statusable;

/**
 * Unit test for {@link DummyChildren}.
 * @author Adam_Csaba_Kiraly
 *
 */
public class DummyChildrenTest {

    private DummyChildren underTest;

    @Before
    public void setUp() throws Exception {
        underTest = new DummyChildren(new ArrayList<Statusable>());
    }

    @Test
    public void testAddWhenAlreadyAddedShouldReturnFalse() {
        //GIVEN
        Statusable child = Mockito.mock(Statusable.class);
        underTest.add(child);
        //WHEN
        boolean result = underTest.add(child);
        //THEN
        Assert.assertFalse(result);
    }

    @Test
    public void testAddWhenNotAlreadyAddedShouldReturnTrue() {
        //GIVEN
        Statusable child = Mockito.mock(Statusable.class);
        //WHEN
        boolean result = underTest.add(child);
        //THEN
        Assert.assertTrue(result);
    }

    @Test
    public void testDetermineNewStatusWhenSuccessAndThereAreNoChildrenShouldReturnSuccess() {
        //GIVEN
        int status = Constants.SUCCEEDED;
        //WHEN
        int result = underTest.determineNewStatus(status);
        //THEN
        Assert.assertEquals(Constants.SUCCEEDED, result);
    }

    @Test
    public void testDetermineNewStatusWhenFailedAndThereAreNoChildrenShouldReturnFailed() {
        //GIVEN
        int status = Constants.FAILED;
        //WHEN
        int result = underTest.determineNewStatus(status);
        //THEN
        Assert.assertEquals(Constants.FAILED, result);
    }

    @Test
    public void testDetermineNewStatusWhenFailedAndThereAreTwoSucceededChildrenShouldReturnSucceeded() {
        //GIVEN
        int status = Constants.FAILED;
        Statusable statusableOne = Mockito.mock(Statusable.class);
        Statusable statusableTwo = Mockito.mock(Statusable.class);
        BDDMockito.given(statusableOne.getStatus()).willReturn(Constants.SUCCEEDED);
        BDDMockito.given(statusableOne.getStatus()).willReturn(Constants.SUCCEEDED);
        underTest.add(statusableOne);
        underTest.add(statusableTwo);
        //WHEN
        int result = underTest.determineNewStatus(status);
        //THEN
        Assert.assertEquals(Constants.FAILED, result);
    }

    @Test
    public void testDetermineNewStatusWhenSucceededAndThereAreTwoSucceededChildrenShouldReturnSucceeded() {
        //GIVEN
        int status = Constants.SUCCEEDED;
        Statusable statusableOne = Mockito.mock(Statusable.class);
        Statusable statusableTwo = Mockito.mock(Statusable.class);
        BDDMockito.given(statusableOne.getStatus()).willReturn(Constants.SUCCEEDED);
        BDDMockito.given(statusableOne.getStatus()).willReturn(Constants.SUCCEEDED);
        underTest.add(statusableOne);
        underTest.add(statusableTwo);
        //WHEN
        int result = underTest.determineNewStatus(status);
        //THEN
        Assert.assertEquals(Constants.FAILED, result);
    }

    @Test
    public void testDetermineNewStatusWhenSucceededAndOneChildSucceededTheOtherFailedShouldReturnFailed() {
        //GIVEN
        int status = Constants.SUCCEEDED;
        Statusable statusableOne = Mockito.mock(Statusable.class);
        Statusable statusableTwo = Mockito.mock(Statusable.class);
        BDDMockito.given(statusableOne.getStatus()).willReturn(Constants.SUCCEEDED);
        BDDMockito.given(statusableOne.getStatus()).willReturn(Constants.FAILED);
        underTest.add(statusableOne);
        underTest.add(statusableTwo);
        //WHEN
        int result = underTest.determineNewStatus(status);
        //THEN
        Assert.assertEquals(Constants.FAILED, result);
    }

    @Test
    public void testDetermineNewStatusWhenFailedAndOneChildSucceededTheOtherFailedShouldReturnFailed() {
        //GIVEN
        int status = Constants.FAILED;
        Statusable statusableOne = Mockito.mock(Statusable.class);
        Statusable statusableTwo = Mockito.mock(Statusable.class);
        BDDMockito.given(statusableOne.getStatus()).willReturn(Constants.SUCCEEDED);
        BDDMockito.given(statusableOne.getStatus()).willReturn(Constants.FAILED);
        underTest.add(statusableOne);
        underTest.add(statusableTwo);
        //WHEN
        int result = underTest.determineNewStatus(status);
        //THEN
        Assert.assertEquals(Constants.FAILED, result);
    }

}
