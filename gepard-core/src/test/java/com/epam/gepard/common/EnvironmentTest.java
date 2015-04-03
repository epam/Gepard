package com.epam.gepard.common;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link Environment}.
 * @author Adam_Csaba_Kiraly
 *
 */
public class EnvironmentTest {

    @After
    public void tearDown() {
        Environment.getProperties().clear();
    }

    @Test
    public void testReadingMultipleValidPropertyFilesShouldLoadProperly() {
        //GIVEN nothing
        //WHEN
        boolean result = Environment.setUp("src/test/resources/testprj.properties,src/test/resources/a.txt,src/test/resources/b.txt");
        //THEN
        Assert.assertTrue(result);
    }

    @Test
    public void testReadingMultipleValidPropertyFilesShouldSetProperties() {
        //GIVEN nothing
        //WHEN
        Environment.setUp("src/test/resources/testprj.properties,src/test/resources/a.txt,src/test/resources/b.txt");
        //THEN
        String result = Environment.getProperty("URL." + Environment.getProperty("TSID"));
        Assert.assertEquals(result, "http://blahqa1.com");
    }

    @Test
    public void testSetUpWithEmptyStringShouldNotTryToLoadProperties() {
        //GIVEN nothing
        //WHEN
        boolean result = Environment.setUp("");
        //THEN
        Assert.assertFalse(result);
    }

    @Test
    public void testSetUpWithNonExistingPropertyFileShouldNotLoadProperties() {
        //GIVEN
        //WHEN
        boolean result = Environment.setUp("idontexist.properties");
        //THEN
        Assert.assertFalse(result);
    }

    @Test
    public void testGetBooleanPropertyWhenPropertyValueStartsWithTrueShouldReturnTrue() {
        //GIVEN
        Environment.setProperty("xyz", "true programmers drink juice");
        //WHEN
        boolean result = Environment.getBooleanProperty("xyz");
        //THEN
        Assert.assertTrue(result);
    }

    @Test
    public void testGetBooleanPropertyWhenPropertyValueDoesNotStartWithTrueShouldReturnFalse() {
        //GIVEN
        Environment.setProperty("xyz", "poser programmers drink juice");
        //WHEN
        boolean result = Environment.getBooleanProperty("xyz");
        //THEN
        Assert.assertFalse(result);
    }

    @Test
    public void testGetBooleanPropertyWhenPropertyValueDoesNotExistShouldReturnFalse() {
        //GIVEN nothing
        //WHEN
        boolean result = Environment.getBooleanProperty("xyz");
        //THEN
        Assert.assertFalse(result);
    }
}
