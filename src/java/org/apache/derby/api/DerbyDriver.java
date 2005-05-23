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
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.derby.jdbc.InternalDriver;

/**
 * Implementation of legacy JDBC Driver.
 *
 * @version $Rev$ $Date$
 */
public class DerbyDriver implements Driver {
    private static final String SQLJ_INTERNAL = "jdbc:default:connection";

    static {
        DerbyDriver driver = new DerbyDriver();
        try {
            DriverManager.registerDriver(driver);
        } catch (SQLException e) {
            // ah well
        }
    }

    public boolean acceptsURL(String url) throws SQLException {
        if (url == null) {
            return false;
        }
        return url.startsWith("jdbc:derby:") || url.equals(SQLJ_INTERNAL);
    }

    public Connection connect(String url, Properties info) throws SQLException {
        if (SQLJ_INTERNAL.equals(url)) {
            InternalDriver driver = InternalDriver.activeDriver();
            if (driver == null) {
                // database is not booted yet
                return null;
            }
            return driver.connect(url, info);
        }

        DriverDataSource ds = new DriverDataSource();
        ds.loadProperties(info);
        ds.loadURL(url);
        return ds.getConnection();
    }

    public int getMajorVersion() {
        return 10; // todo get from properties file
    }

    public int getMinorVersion() {
        return 1; // todo get from properties file
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
