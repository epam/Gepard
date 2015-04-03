package com.epam.gepard.inspector.simplesql;

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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class creates and initialize the database which will be used by
 * the SimpleXXX classes to store the results of each steps of tests.
 */
public final class CreateDataBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateDataBase.class);

    private CreateDataBase() {
    }

    /**
     * Makes statements from the commands and executes them.
     * @param stat statement
     * @param com command
     * @throws IOException in case of error.
     */
    private static void executeSQL(final Statement stat, final StringBuilder com) throws IOException {
        try {
            LOGGER.debug(com.toString());
            stat.execute(com.toString());
            LOGGER.debug("Succeded.");
        } catch (SQLException ex) {
            LOGGER.error("Failed. " + ex);
        }
    }

    /**
     * Verifies if there is an sql file as a parameter, and
     * if there is, reads the commands in from the sql file.
     * @param args are the command line arguments.
     */
    public static void main(final String[] args) {
        if (args.length != 0) {
            // if that parameter is an sql file
            String temp = args[0];
            if (new File(temp + ".sql").exists()) {
                try {
                    Connection con = SimpleFactory.getConnection();
                    Statement stat = con.createStatement();

                    // reading in the commands
                    FileReader fr = new FileReader(temp + ".sql");
                    BufferedReader br = new BufferedReader(fr);
                    StringBuilder com = new StringBuilder();
                    while ((temp = br.readLine()) != null) {
                        temp = temp.trim();
                        com.append(temp);
                        if (temp.endsWith(";")) {
                            executeSQL(stat, com);
                            com = new StringBuilder();
                        }
                    }
                    br.close();
                    stat.close();
                    con.close();
                } catch (SQLException ex) {
                    LOGGER.error("SQLException", ex);
                } catch (IOException ex) {
                    LOGGER.error("IOException", ex);
                }
            } else {
                LOGGER.debug("Not found sql file.");
            }
        } else {
            LOGGER.debug("Missing parameter.");
        }
    }

}
