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
import javax.sql.XAConnection;

/**
 * Implementation of a DataSource supporting XA connections; this is intended for use by distributed transaction
 * managers rather than end user applications.
 *
 * @version $Rev$ $Date$
 */
public class XADataSource extends BasicDataSource implements javax.sql.XADataSource {
    public XAConnection getXAConnection() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public XAConnection getXAConnection(String user, String password) throws SQLException {
        throw new UnsupportedOperationException();
    }
}
