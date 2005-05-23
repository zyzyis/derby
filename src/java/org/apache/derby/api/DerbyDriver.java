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
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Implementation of legacy JDBC Driver.
 *
 * @version $Rev$ $Date$
 */
public class DerbyDriver implements Driver {
    static {
        DerbyDriver driver = new DerbyDriver();
        try {
            DriverManager.registerDriver(driver);
        } catch (SQLException e) {
            // ah well
        }
    }

    public boolean acceptsURL(String url) throws SQLException {
        return url.startsWith("jdbc:derby:") || url.equals("jdbc:default:connection");
    }

    public Connection connect(String url, Properties info) throws SQLException {
        DriverDataSource ds = new DriverDataSource();
        // decompose url and properties to initialize the DriverDataSource
        return ds.getConnection();
    }

    public int getMajorVersion() {
        throw new UnsupportedOperationException();
    }

    public int getMinorVersion() {
        throw new UnsupportedOperationException();
    }

    public boolean jdbcCompliant() {
        return true;
    }

    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /**
     * Internal implementation of a DataSource that, for compatibility with base JDBC 2.0, does not implement the actual
     * interface.
     */
    private static class DriverDataSource extends BasicDataSource {
        private Connection getConnection() throws SQLException {
            return newConnectionFactory().getConnection(this);
        }
    }
}
