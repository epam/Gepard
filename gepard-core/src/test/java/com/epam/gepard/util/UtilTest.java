package com.epam.gepard.util;

import static org.mockito.BDDMockito.given;
import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit test for {@link Util}.
 * @author Adam_Csaba_Kiraly
 */
public class UtilTest extends TestCase {

    private static final String NOT_FOUND = "unknown (no manifest file)";

    @Mock
    private PackageProvider packageProvider;

    @Mock
    private Package packageOfUnderTest;

    @InjectMocks
    private Util underTest;

    @Override
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    public void testEscapeHTML() throws Exception {
        //given
        String stringToTransfer = "12blah\"'&<>";
        //when
        String string = underTest.escapeHTML(stringToTransfer);
        //then
        String expectedString = "12blah&quot;&#39;&amp;&lt;&gt;";
        assertEquals(string, expectedString);
    }

    public void testGetStackTrace() throws Exception {
        //given
        String exceptionText = "Something is wrong.";
        Exception e = new Exception("Something is wrong.");
        //when
        String response = Util.getStackTrace(e);
        //then
        Assert.assertNotNull(response);
        Assert.assertTrue(response.contains(exceptionText));
        Assert.assertTrue(response.contains(this.getClass().getCanonicalName()));
    }

    public void testGetGepardVersionNotFound() throws Exception {
        //given
        given(packageProvider.getPackageOfObject(underTest)).willReturn(packageOfUnderTest);
        given(packageOfUnderTest.getImplementationTitle()).willReturn(null);
        given(packageOfUnderTest.getImplementationVersion()).willReturn(null);
        //when
        String response = underTest.getGepardVersion();
        //then
        Assert.assertNotNull(response);
        Assert.assertTrue(response.contains(NOT_FOUND));
    }

    public void testGetGepardVersionFound() throws Exception {
        //given
        String appTitle = "AppTitle";
        String appVersion = "appVersion";
        given(packageProvider.getPackageOfObject(underTest)).willReturn(packageOfUnderTest);
        given(packageOfUnderTest.getImplementationTitle()).willReturn(appTitle);
        given(packageOfUnderTest.getImplementationVersion()).willReturn(appVersion);
        //when
        String response = underTest.getGepardVersion();
        //then
        Assert.assertNotNull(response);
        Assert.assertTrue(response.equals(appTitle + " " + appVersion));
    }

    public void testAlertText() throws Exception {
        //given
        String initText = "init";
        String expectedText = "<font color=\"#AA0000\"><b>" + initText + "</b></font>";
        //when
        String response = underTest.alertText(initText);
        //then
        Assert.assertNotNull(response);
        Assert.assertTrue(response.equals(expectedText));
    }

}
