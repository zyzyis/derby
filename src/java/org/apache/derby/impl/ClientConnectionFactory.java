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

import javax.sql.DataSource;

import org.apache.derby.api.ConnectionFactory;
import org.apache.derby.api.BasicDataSource;
import org.apache.derby.jdbc.ClientBaseDataSource;
import org.apache.derby.jdbc.ClientDataSource;

/**
 * @version $Rev$ $Date$
 */
public class ClientConnectionFactory implements ConnectionFactory {
    public Connection getConnection(BasicDataSource ds) throws SQLException {
        DataSource cds = getClientDataSource(ds);
        return cds.getConnection();
    }

    public Connection getConnection(BasicDataSource ds, String user, String password) throws SQLException {
        DataSource cds = getClientDataSource(ds);
        return cds.getConnection(user, password);
    }

    private DataSource getClientDataSource(BasicDataSource ds) {
        ClientDataSource cds = new ClientDataSource();
        cds.setServerName(ds.getServerName());
        cds.setPortNumber(ds.getPortNumber());
        cds.setDatabaseName(ds.getDatabaseName());
        cds.setUser(ds.getUser());
        cds.setPassword(ds.getPassword());
        StringBuffer attrs = new StringBuffer();
        if (ds.getCreate()) {
            attrs.append(";create=true");
        }
        if (ds.getShutdown()) {
            attrs.append(";shutdown=true");
        }
        cds.setConnectionAttributes(attrs.toString());

        cds.setLoginTimeout(ds.getLoginTimeout());
        cds.setLogWriter(ds.getLogWriter());
        return cds;
    }
}
