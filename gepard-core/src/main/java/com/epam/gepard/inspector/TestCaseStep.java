package com.epam.gepard.inspector;

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

/**
 * It's a single test step which checks a simple step in the running flow and reports the result.
 */
public interface TestCaseStep extends Statusable {

    /**
     * Adds a description to this TestCaseStep. Basically it has to be
     * set to FAILED because on this way we don't have to check the failures
     * in the assertions just we register the success assertions.
     * @param desc is the description.
     * @param ref is the reference.
     * @return returns the success of the process
     */
    boolean addDesc(String desc, String ref);

}
