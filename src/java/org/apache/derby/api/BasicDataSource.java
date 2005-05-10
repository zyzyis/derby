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

import java.io.PrintWriter;
import java.io.Serializable;

/**
 * Base class for {@link javax.sql.DataSource} implementations that defines the appropriate properties.
 *
 * @version $Rev$ $Date$
 */
public abstract class BasicDataSource implements Serializable {
    // standard properties per the JDBC 3.0 specification
    private String databaseName;
    private String dataSourceName;
    private String description;
    private String networkProtocol;
    private String password;
    private int portNumber;
    private String roleName;
    private String serverName;
    private String user;

    // transient properties defined by the DataSource interface
    private transient int loginTimeout;
    private transient PrintWriter logWriter;

    // Derby specific properties
    private boolean createDatabase;
    private boolean upgrade;
    private boolean shutdownDatabase;

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNetworkProtocol() {
        return networkProtocol;
    }

    public void setNetworkProtocol(String networkProtocol) {
        this.networkProtocol = networkProtocol;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getLoginTimeout() {
        return loginTimeout;
    }

    public void setLoginTimeout(int loginTimeout) {
        this.loginTimeout = loginTimeout;
    }

    public PrintWriter getLogWriter() {
        return logWriter;
    }

    public void setLogWriter(PrintWriter logWriter) {
        this.logWriter = logWriter;
    }

    public boolean getCreateDatabase() {
        return createDatabase;
    }

    public void setCreateDatabase(boolean createDatabase) {
        this.createDatabase = createDatabase;
    }

    public boolean getUpgrade() {
        return upgrade;
    }

    public void setUpgrade(boolean upgrade) {
        this.upgrade = upgrade;
    }

    public boolean getShutdownDatabase() {
        return shutdownDatabase;
    }

    public void setShutdownDatabase(boolean shutdownDatabase) {
        this.shutdownDatabase = shutdownDatabase;
    }

    /**
     * Calculate hashCode based on the following properties: <ul> <li>TBD</li> </ul>
     *
     * @return hashCode for this DataSource
     */
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * Calculate equals based on the following properties: <ul> <li>TBD</li> </ul>
     *
     * @param obj object to compare to
     *
     * @return true if both objects refer to the same DataSource
     */
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
