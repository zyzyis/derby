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

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Interface to be implemented by classes that are capable of established a connection to a server
 * based in the properties specified in a Derby DataSource.
 *
 * @version $Rev$ $Date$
 */
public interface ConnectionFactory {
    /**
     * Return a physical connection to the database specified by the DataSource.
     *
     * @param ds the DataSource to connect to
     *
     * @return a physical connection to the database
     *
     * @throws SQLException if there was a problem establishing the connection
     */
    Connection getConnection(BasicDataSource ds) throws SQLException;

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
    Connection getConnection(BasicDataSource ds, String user, String password) throws SQLException;
}
