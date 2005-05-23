/**
 *
 * Copyright 2005 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.derby.impl;

import java.io.File;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import junit.framework.TestCase;

import org.apache.derby.api.DerbyDriver;

/**
 * @version $Rev$ $Date$
 */
public class EmbeddedDriverTest extends TestCase {
    private String tmpDir;

    public void testConnect() throws SQLException {
        Connection c = DriverManager.getConnection("jdbc:derby:testdb;create=true", null, null);
        c.close();
    }

    protected void setUp() throws Exception {
        super.setUp();
        new DerbyDriver();
        tmpDir = System.getProperty("java.io.tmpdir") + "/embedTest";
        System.setProperty("derby.system.home", tmpDir);
    }

    protected void tearDown() throws Exception {
        // shutdown server
        try {
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (SQLException e) {
            if (!"XJ015".equals(e.getSQLState())) {
                throw e;
            }
        }
        delete(new File(tmpDir));
        Driver driver = DriverManager.getDriver("jdbc:derby:");
        DriverManager.deregisterDriver(driver);

        super.tearDown();
    }

    private void delete(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                delete(file);
            }
        }
        dir.delete();
    }
}
