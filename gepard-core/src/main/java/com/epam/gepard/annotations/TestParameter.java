package com.epam.gepard.annotations;
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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for a test parameter. It means that the value will come from the test file.
 *
 * @author Lajos_Kesztyus, Istvan_Pamer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TestParameter {

    /**
     * Optional. ID of the test parameter. This identifies a parameter to be used from the parameter set.
     * The ID is represented as a String - Gepard will use this to identify the test parameter column.
     * e.g.: TestParameter(id = "column1")
     * If not given, then the field's name will be used to find the column.
     */
    String id() default "";

    /**
     * Optional. If the value is string, separated with a separator character it should be set here.
     */
    String separator() default "";

}
