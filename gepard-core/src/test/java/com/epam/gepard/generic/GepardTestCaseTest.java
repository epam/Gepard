package com.epam.gepard.generic;

import static org.mockito.BDDMockito.given;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import com.epam.gepard.common.Environment;
import com.epam.gepard.common.NATestCaseException;
import com.epam.gepard.common.TestClassExecutionData;
import com.epam.gepard.datadriven.DataDrivenParameters;
import com.epam.gepard.filter.ExpressionTestFilter;
import com.epam.gepard.logger.LogFileWriter;
import com.epam.gepard.logger.helper.LogFileWriterFactory;
import com.epam.gepard.util.FileUtil;

/**
 * Unit test for {@link CommonTestCase}.
 * @author Adam_Csaba_Kiraly, tkohegyi
 */
public class GepardTestCaseTest {

    @Mock
    private ExpressionTestFilter filter;
    private GenericListTestSuite genericListTestSuite;

    @Mock
    private LogFileWriter mainTestLogger;
    @Mock
    private FileUtil fileUtil;

    private Environment environment;

    private CommonGepardTestClassForTesting underTest;

    private Map<String, TestClassExecutionData> createTestClassMap() {
        Map<String, TestClassExecutionData> testClassMap = new LinkedHashMap<>();
        TestClassExecutionData classData = new TestClassExecutionData("id", environment);
        testClassMap.put("com.epam.gepard.generic.TestMock/0", classData);
        return testClassMap;
    }

    private void givenBasicTestClassExecutionData() {
        TestClassExecutionData classData = new TestClassExecutionData("id", environment);
        Whitebox.setInternalState(underTest, "classData", classData);
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        environment = new Environment();
        genericListTestSuite = new GenericListTestSuite("src/test/resources/testlist.txt", filter, environment);
        underTest = new CommonGepardTestClassForTesting();
    }

    @Ignore
    @Test
    public void testSuiteHelperWithNonDataDrivenTestClassShouldCreateTest() {
        //GIVEN
        Class<?> testClass = TestMock.class;
        String scriptID = "id";
        String scriptName = "name";
        String[] parameterNames = new String[]{"one", "two"};
        Map<String, TestClassExecutionData> testClassMap = createTestClassMap();
        Whitebox.setInternalState(genericListTestSuite, "testClassMap", testClassMap);
        //WHEN
//??        GenericTestSuite result = (GenericTestSuite) GenericListTestSuite.suiteHelper(testClass, scriptID, scriptName, parameterNames, environment);
        //THEN
//??        Assert.assertEquals("name", result.getTestName());
//??        Assert.assertEquals("id", result.getTestID());
    }

    @Test
    public void testLogComment() {
        givenBasicTestClassExecutionData();
        //WHEN
        underTest.logComment("owl");
        //THEN
        Mockito.verify(mainTestLogger).insertText("<tr><td>&nbsp;</td><td bgcolor=\"#F0F0E0\">owl</td></tr>");
    }

    @Test
    public void testLogCommentWithDescription() {
        //GIVEN
        givenBasicTestClassExecutionData();
        //WHEN
        underTest.logComment("owl", "desc");
        //THEN
        Mockito.verify(mainTestLogger).insertText(
                "<tr><td>&nbsp;</td><td bgcolor=\"#F0F0E0\">owl <small>[<a href=\"javascript:showhide('div_1');\">details</a>]</small>"
                        + "<div id=\"div_1\" style=\"display:none\"><br>\n" + "desc</div></td></tr>\n");
    }

    @Test
    public void testLogCommentWithDescriptionAndComment() {
        //GIVEN
        givenBasicTestClassExecutionData();
        //WHEN
        underTest.logComment("comment", "desc");
        //THEN
        Mockito.verify(mainTestLogger).insertText(
                "<tr><td>&nbsp;</td><td bgcolor=\"#F0F0E0\">comment <small>[<a href=\"javascript:showhide('div_1');\">details</a>]</small>"
                        + "<div id=\"div_1\" style=\"display:none\"><br>\n" + "desc</div></td></tr>\n");
    }

    @Test
    public void testLogStep() {
        givenBasicTestClassExecutionData();
        //WHEN
        underTest.logStep("comment");
        //THEN
        Mockito.verify(mainTestLogger).insertText(
                "<tr><td align=\"center\">&nbsp;&nbsp;1.&nbsp;&nbsp;</td><td bgcolor=\"#E0E0F0\">comment</td></tr>\n");
    }

    @Ignore
    @Test
    public void testSetUpLogger() {
        //GIVEN
        environment.setProperty(Environment.GEPARD_RESULT_TEMPLATE_PATH, "ABC123");
        environment.setProperty(Environment.GEPARD_HTML_RESULT_PATH, "DEF456");
        TestClassExecutionData classData = new TestClassExecutionData("id", environment);
        Whitebox.setInternalState(underTest, "classData", classData);
        LogFileWriterFactory logFileWriterFactory = Mockito.mock(LogFileWriterFactory.class);
        Whitebox.setInternalState(underTest, "logFileWriterFactory", logFileWriterFactory);
        Whitebox.setInternalState(underTest, "fileUtil", fileUtil);
        //WHEN
//???        underTest.setUpLogger();
        //THEN
        Mockito.verify(logFileWriterFactory).createCustomWriter("ABC123/temp_generictestcase.html",
                "DEF456/com/epam/gepard/generic/CommonTestCaseImplementationForTesting0//testPassed0.html", environment);
    }

    @Ignore
    @Test
    public void testRunTestShouldSetTask() throws Throwable {
        givenBasicTestClassExecutionData();
        //WHEN
//???        underTest.runTest();
        //THEN
//???        Assert.assertNotNull(underTest.getClassData().getTask());
    }

    @Ignore
    @Test
    public void testReadDirectoryShouldConvertClassNameWithPackageToDirectoryPath() {
        givenBasicTestClassExecutionData();
        //WHEN
//???        String result = underTest.readDirectory();
        //THEN
//???        Assert.assertEquals("com/epam/gepard/generic/CommonTestCaseImplementationForTesting0/", result);
    }

    @Ignore
    @Test(expected = NATestCaseException.class)
    public void testGetTestParametersWithNonDataDrivenTestShouldThrowNa() {
        //GIVEN
        givenBasicTestClassExecutionData();
        //WHEN
//???        underTest.getTestParameters();
        //THEN exception is expected
    }

    @Ignore
    @Test
    public void testGetTestParametersWithDataDrivenTestShouldReturnTestParameters() {
        //GIVEN
        String[] expected = new String[]{"1", "2"};
        TestClassExecutionData classData = new TestClassExecutionData("id", environment);
        DataDrivenParameters drivenData = Mockito.mock(DataDrivenParameters.class);
        given(drivenData.getParameters()).willReturn(expected);
        classData.setDrivenData(drivenData);
        Whitebox.setInternalState(underTest, "classData", classData);
        //WHEN
//???        String[] result = underTest.getTestParameters();
        //THEN
//???        Assert.assertArrayEquals(expected, result);
    }

}
