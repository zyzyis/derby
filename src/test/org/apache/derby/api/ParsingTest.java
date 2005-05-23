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
package org.apache.derby.api;

import java.sql.SQLException;
import java.util.Properties;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class ParsingTest extends TestCase {
    private BasicDataSource mockds;
    private Properties props;

    public void testServerName() throws SQLException {
        mockds.loadURL("jdbc:derby://testHost");
        assertEquals("testHost", mockds.getServerName());
        assertEquals(1527, mockds.getPortNumber());
        assertNull(mockds.getDatabaseName());
    }

    public void testServerAndDatabaseName() throws SQLException {
        mockds.loadURL("jdbc:derby://testHost/testdb");
        assertEquals("testHost", mockds.getServerName());
        assertEquals(1527, mockds.getPortNumber());
        assertEquals("testdb", mockds.getDatabaseName());
    }

    public void testServerNameAndPort() throws SQLException {
        mockds.loadURL("jdbc:derby://testHost2:1234");
        assertEquals("testHost2",  mockds.getServerName());
        assertEquals(1234, mockds.getPortNumber());
        assertNull(mockds.getDatabaseName());
    }

    public void testServerNameDatabaseNameAndPort() throws SQLException {
        mockds.loadURL("jdbc:derby://testHost2:1234/testdb");
        assertEquals("testHost2", mockds.getServerName());
        assertEquals(1234, mockds.getPortNumber());
        assertEquals("testdb", mockds.getDatabaseName());
    }

    public void testNoServerName() throws SQLException {
        mockds.loadURL("jdbc:derby://");
        assertEquals("", mockds.getServerName());
        assertEquals(1527, mockds.getPortNumber());
        assertNull(mockds.getDatabaseName());
    }

    public void testDatabaseNameInURL() throws SQLException {
        mockds.loadURL("jdbc:derby:");
        assertEquals(null, mockds.getServerName());
        assertEquals(null, mockds.getDatabaseName());

        mockds.loadURL("jdbc:derby:testdb");
        assertEquals(null, mockds.getServerName());
        assertEquals("testdb", mockds.getDatabaseName());

        mockds.loadURL("jdbc:derby:/tmp/testdb");
        assertEquals(null, mockds.getServerName());
        assertEquals("/tmp/testdb", mockds.getDatabaseName());

        mockds.loadURL("jdbc:derby:C:\\Temp\\testdb");
        assertEquals(null, mockds.getServerName());
        assertEquals("C:\\Temp\\testdb", mockds.getDatabaseName());
    }

    public void testDefaultProperties() throws SQLException {
        mockds.loadURL("jdbc:derby:");
        assertNull(mockds.getServerName());
        assertEquals(1527, mockds.getPortNumber());
        assertNull(mockds.getDatabaseName());
        assertNull(mockds.getUser());
        assertNull(mockds.getPassword());
        assertFalse(mockds.getCreate());
        assertFalse(mockds.getUpgrade());
        assertFalse(mockds.getShutdown());
    }

    public void testPropertiesInURL() throws SQLException {
        mockds.loadURL("jdbc:derby:;serverName=testHost;portNumber=1234;databaseName=testdb;user=testuser;password=testpw;create=true;upgrade=true;shutdown=true");
        assertEquals("testHost", mockds.getServerName());
        assertEquals(1234, mockds.getPortNumber());
        assertEquals("testdb", mockds.getDatabaseName());
        assertEquals("testuser", mockds.getUser());
        assertEquals("testpw", mockds.getPassword());
        assertTrue(mockds.getCreate());
        assertTrue(mockds.getUpgrade());
        assertTrue(mockds.getShutdown());
    }

    public void testProperties() throws SQLException {
        props.setProperty("serverName", "testHost");
        props.setProperty("portNumber", "1234");
        props.setProperty("databaseName", "testdb");
        props.setProperty("user", "testuser");
        props.setProperty("password", "testpw");
        props.setProperty("create", "true");
        props.setProperty("upgrade", "true");
        props.setProperty("shutdown", "true");

        mockds.loadProperties(props);
        assertEquals("testHost", mockds.getServerName());
        assertEquals(1234, mockds.getPortNumber());
        assertEquals("testdb", mockds.getDatabaseName());
        assertEquals("testuser", mockds.getUser());
        assertEquals("testpw", mockds.getPassword());
        assertTrue(mockds.getCreate());
        assertTrue(mockds.getUpgrade());
        assertTrue(mockds.getShutdown());
    }

    protected void setUp() throws Exception {
        super.setUp();
        props = new Properties();
        mockds = new BasicDataSource(){
        };
    }
}
