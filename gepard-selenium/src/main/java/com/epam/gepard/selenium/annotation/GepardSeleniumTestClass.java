package com.epam.gepard.selenium.annotation;
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
 * Annotates a class as a SeleniumTestClass.
 *
 * @author Istvan_Pamer, Tamas_Kohegyi
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GepardSeleniumTestClass {
    String UNDEFINED = "";

    /**
     * Base URL for the test. Leave it blank to get the default.
     */
    String baseUrl() default UNDEFINED;

    /**
     * Browser for the test. Leave it blank to get the default.
     */
    String browser() default UNDEFINED;

}
