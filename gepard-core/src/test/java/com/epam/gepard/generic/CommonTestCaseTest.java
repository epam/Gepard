package com.epam.gepard.generic;

import static org.mockito.BDDMockito.given;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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
 * @author Adam_Csaba_Kiraly
 */
public class CommonTestCaseTest {

    @Mock
    private ExpressionTestFilter filter;
    private GenericListTestSuite genericListTestSuite;

    @Mock
    private LogFileWriter mainTestLogger;
    @Mock
    private FileUtil fileUtil;

    private CommonTestCase underTest;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        genericListTestSuite = new GenericListTestSuite("src/test/resources/testlist.txt", filter);
        underTest = new CommonTestCaseImplementationForTesting();
        underTest.setMainTestLogger(mainTestLogger);
    }

    @After
    public void tearDown() {
        Environment.getProperties().clear();
    }

    @Test
    public void testSuiteHelperWithNonDataDrivenTestClassShouldCreateTest() {
        //GIVEN
        Environment.setProperty(Environment.GEPARD_TEST_TIMEOUT, "1");
        Class<?> testClass = TestMock.class;
        String scriptID = "id";
        String scriptName = "name";
        String[] parameterNames = new String[]{"one", "two"};
        Map<String, TestClassExecutionData> testClassMap = createTestClassMap();
        Whitebox.setInternalState(genericListTestSuite, "testClassMap", testClassMap);
        //WHEN
        GenericTestSuite result = (GenericTestSuite) CommonTestCase.suiteHelper(testClass, scriptID, scriptName, parameterNames);
        //THEN
        Assert.assertEquals("name", result.getTestName());
        Assert.assertEquals("id", result.getTestID());
    }

    private Map<String, TestClassExecutionData> createTestClassMap() {
        Map<String, TestClassExecutionData> testClassMap = new LinkedHashMap<>();
        TestClassExecutionData classData = new TestClassExecutionData("id");
        testClassMap.put("com.epam.gepard.generic.TestMock/0", classData);
        return testClassMap;
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
        underTest.logComment("owl", "comment", "desc");
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

    @Test
    public void testSetUpLogger() {
        //GIVEN
        Environment.setProperty(Environment.GEPARD_TEST_TIMEOUT, "1");
        Environment.setProperty(Environment.GEPARD_RESULT_TEMPLATE_PATH, "ABC123");
        Environment.setProperty(Environment.GEPARD_HTML_RESULT_PATH, "DEF456");
        TestClassExecutionData classData = new TestClassExecutionData("id");
        Whitebox.setInternalState(underTest, "classData", classData);
        LogFileWriterFactory logFileWriterFactory = Mockito.mock(LogFileWriterFactory.class);
        Whitebox.setInternalState(underTest, "logFileWriterFactory", logFileWriterFactory);
        Whitebox.setInternalState(underTest, "fileUtil", fileUtil);
        //WHEN
        underTest.setUpLogger();
        //THEN
        Mockito.verify(logFileWriterFactory).createCustomWriter("ABC123/temp_generictestcase.html",
                "DEF456/com/epam/gepard/generic/CommonTestCaseImplementationForTesting0//testPassed0.html");
    }

    @Test
    public void testRunTestShouldSetTask() throws Throwable {
        givenBasicTestClassExecutionData();
        //WHEN
        underTest.runTest();
        //THEN
        Assert.assertNotNull(underTest.getClassData().getTask());
    }

    @Test
    public void testReadDirectoryShouldConvertClassNameWithPackageToDirectoryPath() {
        givenBasicTestClassExecutionData();
        //WHEN
        String result = underTest.readDirectory();
        //THEN
        Assert.assertEquals("com/epam/gepard/generic/CommonTestCaseImplementationForTesting0/", result);
    }

    @Test(expected = NATestCaseException.class)
    public void testGetTestParametersWithNonDataDrivenTestShouldThrowNa() {
        //GIVEN
        givenBasicTestClassExecutionData();
        //WHEN
        underTest.getTestParameters();
        //THEN exception is expected
    }

    @Test
    public void testGetTestParametersWithDataDrivenTestShouldReturnTestParameters() {
        //GIVEN
        String[] expected = new String[]{"1", "2"};
        Environment.setProperty(Environment.GEPARD_TEST_TIMEOUT, "1");
        TestClassExecutionData classData = new TestClassExecutionData("id");
        DataDrivenParameters drivenData = Mockito.mock(DataDrivenParameters.class);
        given(drivenData.getParameters()).willReturn(expected);
        classData.setDrivenData(drivenData);
        Whitebox.setInternalState(underTest, "classData", classData);
        //WHEN
        String[] result = underTest.getTestParameters();
        //THEN
        Assert.assertArrayEquals(expected, result);
    }

    private void givenBasicTestClassExecutionData() {
        Environment.setProperty(Environment.GEPARD_TEST_TIMEOUT, "1");
        TestClassExecutionData classData = new TestClassExecutionData("id");
        Whitebox.setInternalState(underTest, "classData", classData);
    }

}
