package com.epam.gepard.util;

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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

/**
 * Unit test for {@link ReflectionUtilsExtension}.
 * @author Adam_Csaba_Kiraly
 *
 */
public class ReflectionUtilsExtensionTest {

    @Test
    public void testValueOfWhenInt() throws Exception {
        //GIVEN nothing
        //WHEN
        Object result = ReflectionUtilsExtension.valueOf(int.class, "12");
        //THEN
        assertEquals(12, result);
    }

    @Test
    public void testValueOfWhenInteger() throws Exception {
        //GIVEN nothing
        //WHEN
        Object result = ReflectionUtilsExtension.valueOf(Integer.class, "12");
        //THEN
        assertEquals(12, result);
    }

    @Test
    public void testValueOfWhenValuerNotFoundButMethodIsFound() throws Exception {
        //GIVEN nothing
        //WHEN
        Object result = ReflectionUtilsExtension.valueOf(Byte.class, "12");
        //THEN
        byte expected = 12;
        assertEquals(expected, result);
    }

    @Test(expected = Exception.class)
    public void testValueOfWhenValuerNotFoundAndMethodNotFoundShouldThrowException() throws Exception {
        //GIVEN nothing
        //WHEN
        ReflectionUtilsExtension.valueOf(Math.class, "12");
        //THEN exception is exopected
    }

    @Test
    public void testValueOfWhenClassIsAnnotatedAndFieldIsArray() throws Exception {
        //GIVEN
        Field field = FieldMockOne.class.getDeclaredField("array");
        String value = "1,2,3,4";
        String separator = "doesNotMatter";
        String[] expected = new String[]{"1", "2", "3", "4"};
        //WHEN
        String[] result = (String[]) ReflectionUtilsExtension.valueOf(field, value, separator);
        //THEN
        assertArrayEquals(expected, result);
    }

    @Test
    public void testValueOfWhenClassIsAnnotatedAndFieldIsList() throws Exception {
        //GIVEN
        Field field = FieldMockOne.class.getDeclaredField("list");
        String value = "1,2,3,4";
        String separator = ",";
        List<String> expected = new ArrayList<String>();
        expected.add("1");
        expected.add("2");
        expected.add("3");
        expected.add("4");
        //WHEN
        @SuppressWarnings("unchecked")
        List<String> result = (List) ReflectionUtilsExtension.valueOf(field, value, separator);
        //THEN
        assertEquals(expected, result);
    }

    @Test
    public void testValueOfWhenClassIsAnnotatedAndFieldIsSet() throws Exception {
        //GIVEN
        Field field = FieldMockOne.class.getDeclaredField("set");
        String value = "1,2,3,4";
        String separator = ",";
        Set<String> expected = new HashSet<String>();
        expected.add("1");
        expected.add("2");
        expected.add("3");
        expected.add("4");
        //WHEN
        @SuppressWarnings("unchecked")
        Set<String> result = (Set) ReflectionUtilsExtension.valueOf(field, value, separator);
        //THEN
        assertEquals(expected, result);
    }

}
