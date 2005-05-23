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
import java.sql.SQLException;

import junit.framework.TestCase;

import org.apache.derby.api.BasicDataSource;
import org.apache.derby.api.DerbyDataSource;
import org.apache.derby.jdbc.EmbeddedSimpleDataSource;

/**
 * @version $Rev$ $Date$
 */
public class EmbeddedConnectionFactoryTest extends TestCase {
    private String tmpDir;
    private EmbeddedConnectionFactory cf;
    private BasicDataSource ds;

    public void testStandardOptions() throws SQLException {
        ds.setDatabaseName("testdb");
        ds.setUser("user");
        ds.setPassword("password");
        EmbeddedSimpleDataSource eds = cf.getDataSource(ds);
        assertEquals("testdb", eds.getDatabaseName());
        assertEquals("user", eds.getUser());
        assertEquals("password", eds.getPassword());
    }

    public void testDerbyOptions() throws SQLException {
        ds.setCreateDatabase(false);
        ds.setShutdownDatabase(false);
        EmbeddedSimpleDataSource eds = cf.getDataSource(ds);
        assertNull(eds.getCreateDatabase());
        assertNull(eds.getShutdownDatabase());

        ds.setCreateDatabase(true);
        ds.setShutdownDatabase(true);
        eds = cf.getDataSource(ds);
        assertEquals("create", eds.getCreateDatabase());
        assertEquals("shutdown", eds.getShutdownDatabase());
    }

    public void testCreateDatabase() throws SQLException {
        ds.setDatabaseName("testdb");
        ds.setCreateDatabase(true);

        // connect first time, should be no warning
        Connection c = cf.getConnection(ds);
        assertNull(c.getWarnings());
        c.close();
        File dbfile = new File(tmpDir, "testdb");
        assertTrue(dbfile.exists());

        // database should now be there so expect a warning
        c = cf.getConnection(ds);
        assertNotNull(c.getWarnings());
        c.close();

        // shut down
        ds.setCreateDatabase(false);
        ds.setShutdownDatabase(true);
        try {
            cf.getConnection(ds);
            fail();
        } catch (SQLException e) {
            // expected exception on shutdown
            assertEquals("08006", e.getSQLState());
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        tmpDir = System.getProperty("java.io.tmpdir") + "/embedTest";
        System.setProperty("derby.system.home", tmpDir);

        cf = new EmbeddedConnectionFactory();
        ds = new DerbyDataSource();
    }

    protected void tearDown() throws Exception {
        // shutdown server
        ds = new DerbyDataSource();
        ds.setShutdownDatabase(true);
        try {
            cf.getConnection(ds);
        } catch (SQLException e) {
            if (!"XJ015".equals(e.getSQLState())) {
                throw e;
            }
        }
        delete(new File(tmpDir));
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
