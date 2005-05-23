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

import java.sql.Connection;
import java.sql.SQLException;

import junit.framework.TestSuite;
import junit.framework.TestCase;

import org.apache.derby.util.ClientTestSuite;
import org.apache.derby.api.BasicDataSource;
import org.apache.derby.api.DerbyDataSource;

/**
 * @version $Rev$ $Date$
 */
public class ClientConnectionFactoryTest extends TestCase {
    public static TestSuite suite() {
        return new ClientTestSuite(ClientConnectionFactoryTest.class);
    }

    private BasicDataSource ds;
    private ClientConnectionFactory cf;

    public void testConnect() throws SQLException {
        Connection c = cf.getConnection(ds);
        try {
            assertEquals("testuser", c.getMetaData().getUserName());
        } finally {
            c.close();
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        ds = new DerbyDataSource();
        ds.setServerName("localhost");
        ds.setDatabaseName("testdb");
        ds.setCreate(true);
        ds.setUser("testuser");
        ds.setPassword("testpassword");
        cf = new ClientConnectionFactory();
    }
}
