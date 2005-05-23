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

import org.apache.derby.api.ConnectionFactory;
import org.apache.derby.api.BasicDataSource;

/**
 * @version $Rev$ $Date$
 */
public class DefaultConnectionFactory implements ConnectionFactory {
    public Connection getConnection(BasicDataSource ds) throws SQLException {
        ConnectionFactory cf;
        if (ds.getServerName() == null) {
            cf = new ClientConnectionFactory();
        } else {
            cf = new EmbeddedConnectionFactory();
        }
        return cf.getConnection(ds);
    }

    public Connection getConnection(BasicDataSource ds, String user, String password) throws SQLException {
        ConnectionFactory cf;
        if (ds.getServerName() == null) {
            cf = new ClientConnectionFactory();
        } else {
            cf = new EmbeddedConnectionFactory();
        }
        return cf.getConnection(ds, user, password);
    }
}
