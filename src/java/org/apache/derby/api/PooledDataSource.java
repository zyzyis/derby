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
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;

/**
 * Implementation of a DataSource supporting pooled connections. This is intended for use by connection pooling
 * middleware rather than end user applications.
 *
 * @version $Rev$ $Date$
 */
public class PooledDataSource extends BasicDataSource implements ConnectionPoolDataSource {
    public PooledConnection getPooledConnection() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public PooledConnection getPooledConnection(String user, String password) throws SQLException {
        throw new UnsupportedOperationException();
    }
}
