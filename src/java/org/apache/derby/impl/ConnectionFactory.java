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

import org.apache.derby.api.BasicDataSource;

/**
 * Base class for factories that create Connections. Different factory implementations support different JDBC
 * specification versions as determined by the JVM in use. Initial support is provided for the JDBC 2.0, JDBC 3.0 and
 * JSR-169 APIs.
 * <p/>
 * Each subclass is responsible for creating connections as specified by the properties supplied in the DataSource.
 * These may be direct connections to an embedded engine, client connections to a remote server, or other transports as
 * they become supported.
 *
 * @version $Rev$ $Date$
 */
public abstract class ConnectionFactory {
    public static ConnectionFactory getInstance() {
        // return the appropriate factory for JDBC 2.0, JDBC 3.0 or JSR-169 implementations of Connection
        throw new UnsupportedOperationException();
    }

    /**
     * Return a physical connection to the database specified by the DataSource.
     *
     * @param ds the DataSource to connect to
     *
     * @return a physical connection to the database
     *
     * @throws SQLException if there was a problem establishing the connection
     */
    public abstract Connection getConnection(BasicDataSource ds) throws SQLException;

    /**
     * Return a physical connection to the database specified by the DataSource.
     *
     * @param ds       the DataSource to connect to
     * @param user     the username to connect with
     * @param password the password to connect with
     *
     * @return a physical connection to the database
     *
     * @throws SQLException if there was a problem establishing the connection
     */
    public abstract Connection getConnection(BasicDataSource ds, String user, String password) throws SQLException;
}
