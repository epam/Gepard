package com.epam.gepard.inspector.dummy;

import java.util.List;

import com.epam.gepard.inspector.Constants;
import com.epam.gepard.inspector.Statusable;

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
 * Holds the children for the dummy tests.
 * @author Adam_Csaba_Kiraly
 */
public class DummyChildren {
    private final List<Statusable> children;

    /**
     * Constructs a new instance of {@link DummyChildren}.
     * @param children the backing list of the object
     */
    public DummyChildren(final List<Statusable> children) {
        this.children = children;
    }

    /**
     * Determines the new status from the current status and the children's statuses.
     * @param status the current status
     * @return the new status
     */
    public int determineNewStatus(final int status) {
        int result = status;
        if (!children.isEmpty()) {
            result = Constants.SUCCEEDED;
        }
        for (Statusable aChildren : children) {
            if (result == aChildren.getStatus() && result == Constants.SUCCEEDED) {
                result = Constants.SUCCEEDED;
            } else {
                result = Constants.FAILED;
            }
        }
        return result;
    }

    /**
     * Adds a child {@link Statusable} to the list of children if there is no child object with such name.
     * @param child statusable
     * @return true if the child has been added
     */
    boolean add(final Statusable child) {
        boolean retval = false;
        if (!children.contains(child)) {
            retval = children.add(child);
        }
        return retval;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((children == null) ? 0 : children.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof DummyChildren)) {
            return false;
        }
        DummyChildren other = (DummyChildren) obj;
        if (children == null) {
            if (other.children != null) {
                return false;
            }
        } else if (!children.equals(other.children)) {
            return false;
        }
        return true;
    }

}
