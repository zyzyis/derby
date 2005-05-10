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
import javax.sql.DataSource;

import org.apache.derby.impl.ConnectionFactory;

/**
 * Main implementation of DataSource intended for use by end user applications.
 * <p/>
 * This DataSource can be used by both J2SE and CDC/FP/JSR-169 applications to directly establish a physical connection
 * to the database.
 *
 * @version $Rev$ $Date$
 */
public class DerbyDataSource extends BasicDataSource implements DataSource {
    public Connection getConnection() throws SQLException {
        return ConnectionFactory.getInstance().getConnection(this);
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return ConnectionFactory.getInstance().getConnection(this, username, password);
    }
}
