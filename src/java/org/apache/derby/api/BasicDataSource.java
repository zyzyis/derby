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
import java.sql.SQLException;
import java.util.Properties;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.derby.impl.DefaultConnectionFactory;

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
    private String user;
    private String password;
    private String serverName;
    private int portNumber = 1527;

    // transient properties defined by the DataSource interface
    private transient int loginTimeout;
    private transient PrintWriter logWriter;

    // Derby specific properties
    private String connectionFactoryClass;
    private transient ConnectionFactory connectionFactory;
    private boolean create;
    private boolean upgrade;
    private boolean shutdown;

    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * Set the name of the database. This is the name of the database inside the server and corresponds to the path of
     * the root directory; relative paths are resolved against the path specified in the derby.system.home system
     * property set on the server.
     *
     * @param databaseName the name of the database
     */
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    /**
     * Set the name of this DataSource. This is typically used by a connection manager to identify connections from the
     * same DataSource.
     *
     * @param dataSourceName the name of this DataSource
     */
    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Set the description for this DataSource.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public String getUser() {
        return user;
    }

    /**
     * Set the identity used to connect to the server. This may be null if connecting to an embedded server.
     *
     * @param user the identity (username) used to connector to the server
     */
    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    /**
     * Set a password credential for authenticating to the server.
     *
     * @param password the password credential used to authenticate to the server
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public String getConnectionFactoryClass() {
        return connectionFactoryClass;
    }

    /**
     * Set the name of the class to be used to create connections. If this property is not set then an internal default
     * implementation is used which will determined the type of connection based on the value of the serverName
     * property:
     * <p/>
     * <ul>
     * <li>if the property is set, attempt to connect to a server on the specified host using the Derby Network Client
     * protocol</li>
     * <li>if the property is null, connect to an embedded server</li> </ul>
     * <p/>
     * Users can supply their own implementation of {@link org.apache.derby.api.ConnectionFactory} to support custom
     * protocols. Such an implementation must have a public no-arg constructor and will be loaded using first the Thread
     * Context ClassLoader and then ClassLoader used to load this class.
     *
     * @param connectionFactoryClass name of a custom {@link org.apache.derby.api.ConnectionFactory} implementation;
     *                               may be null
     */
    public void setConnectionFactoryClass(String connectionFactoryClass) {
        this.connectionFactoryClass = connectionFactoryClass;
    }

    public String getServerName() {
        return serverName;
    }

    /**
     * Helper for instantating ConnectionFactory instances.
     *
     * @return a ConnectionFactory as defined by the connectionFactoryClass property
     * @throws SQLException if there was a problem instantiating the factory
     */
    protected synchronized ConnectionFactory newConnectionFactory() throws SQLException {
        if (connectionFactory == null) {
            String name = connectionFactoryClass;
            if (name == null) {
                connectionFactory = new DefaultConnectionFactory();
            } else {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                if (cl == null) {
                    cl = this.getClass().getClassLoader();
                }
                try {
                    Class clazz = cl.loadClass(name);
                    connectionFactory = (ConnectionFactory) clazz.newInstance();
                } catch (ClassNotFoundException e) {
                    throw new SQLException(e.getMessage());
                } catch (InstantiationException e) {
                    throw new SQLException(e.getMessage());
                } catch (IllegalAccessException e) {
                    throw new SQLException(e.getMessage());
                }
            }
        }
        return connectionFactory;
    }

    /**
     * Set the host name of the server to connect to.
     * If null, the default ConnectionFactory will connect to an embedded server.
     *
     * @param serverName the host name of the server to connect to; null means an embedded server
     */
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public int getPortNumber() {
        return portNumber;
    }

    /**
     * Set the TCP/IP port for a network connection.
     * Defaults to 1527 and is ignored if the server is embedded.
     *
     * @param portNumber the port number to connect to
     */
    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public int getLoginTimeout() {
        return loginTimeout;
    }

    /**
     * Set the number of seconds to wait whilst attempted to establish a connection.
     * The default value of zero implies no limit.
     *
     * @param loginTimeout number of seconds to wait when establishing a connection; zero means no timeout
     * @see javax.sql.DataSource#setLoginTimeout(int)
     */
    public void setLoginTimeout(int loginTimeout) {
        this.loginTimeout = loginTimeout;
    }

    public PrintWriter getLogWriter() {
        return logWriter;
    }

    /**
     * Set the PrintWriter to be used for all messages associated with this DataSource.
     * The default is null indicating logging is disabled.
     *
     * @param logWriter the PrintWriter to be used for logging; null disables logging
     * @see javax.sql.DataSource#setLogWriter(java.io.PrintWriter)
     */
    public void setLogWriter(PrintWriter logWriter) {
        this.logWriter = logWriter;
    }

    public boolean getCreate() {
        return create;
    }

    public void setCreate(boolean create) {
        this.create = create;
    }

    public boolean getUpgrade() {
        return upgrade;
    }

    public void setUpgrade(boolean upgrade) {
        this.upgrade = upgrade;
    }

    public boolean getShutdown() {
        return shutdown;
    }

    public void setShutdown(boolean shutdown) {
        this.shutdown = shutdown;
    }

    /**
     * Calculate hashCode based on the following properties: <ul> <li>dataSourceName</li> </ul>
     *
     * @return hashCode for this DataSource
     */
    public int hashCode() {
        return dataSourceName == null ? 0 : dataSourceName.hashCode();
    }

    /**
     * Calculate equals based on the following properties: <ul> <li>dataSourceName</li> </ul>
     *
     * @param obj object to compare to
     *
     * @return true if both objects refer to the same DataSource
     */
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof BasicDataSource == false) return false;
        final BasicDataSource other = (BasicDataSource) obj;
        if (dataSourceName == null) {
            return other.dataSourceName == null;
        } else {
            return dataSourceName.equals(other.dataSourceName);
        }
    }

    /**
     * Package protected method for initializing from a set of Properties.
     *
     * @param props properties to load
     */
    void loadProperties(Properties props) {
        for (Iterator i = props.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            if ("serverName".equals(key)) {
                setServerName(value);
            } else if ("portNumber".equals(key)) {
                setPortNumber(Integer.valueOf(value).intValue());
            } else if ("databaseName".equals(key)) {
                setDatabaseName(value);
            } else if ("user".equals(key)) {
                setUser(value);
            } else if ("password".equals(key)) {
                setPassword(value);
            } else if ("connectionFactoryClass".equals(key)) {
                setConnectionFactoryClass(value);
            } else if ("create".equals(key)) {
                setCreate(Boolean.valueOf(value).booleanValue());
            } else if ("upgrade".equals(key)) {
                setUpgrade(Boolean.valueOf(value).booleanValue());
            } else if ("shutdown".equals(key)) {
                setShutdown(Boolean.valueOf(value).booleanValue());
            }
        }
    }

    /**
     * Package protected method for initializing from a JDBC URL.
     *
     * @param url the URL to load from
     */
    void loadURL(String url) {
        // remove jdbc:derby: prefix
        url = url.substring(11);
        if (url.startsWith("//")) {
            // serverName specified in url
            int index = url.indexOf(';');
            String serverName;
            if (index == -1) {
                serverName = url.substring(2);
                url = "";
            } else {
                serverName = url.substring(2, index);
                url = url.substring(index);
            }

            // extract databaseName
            index = serverName.indexOf('/');
            if (index != -1) {
                setDatabaseName(serverName.substring(index+1).trim());
                serverName = serverName.substring(0, index);
            }

            // extract portNumber
            index = serverName.indexOf(':');
            if (index != -1) {
                setPortNumber(Integer.valueOf(serverName.substring(index+1)).intValue());
                serverName = serverName.substring(0, index);
            }
            setServerName(serverName.trim());
        } else {
            String name;
            int index = url.indexOf(';');
            if (index == -1) {
                name = url;
            } else {
                name = url.substring(0, index);
                url = url.substring(index);
            }
            name = name.trim();
            if (name.length() > 0) {
                setDatabaseName(name);
            }
        }

        // extract properties from URL
        Properties props = new Properties();
        StringTokenizer tok = new StringTokenizer(url, ";");
        while (tok.hasMoreTokens()) {
            String pair = tok.nextToken();
            int index = pair.indexOf('=');
            if (index == -1) {
                continue;
            }
            String key = pair.substring(0, index);
            String value = pair.substring(index+1);
            props.setProperty(key.trim(), value.trim());
        }
        loadProperties(props);
    }

    /**
     * Package protected method for converting a DataSource to a Derby JDBC URL.
     *
     * @return a URL enconded form of this DataSource
     */
    String toURL() {
        StringBuffer buf = new StringBuffer(256);
        buf.append("jdbc:derby:");
        if (serverName != null) {
            append(buf, "serverName", serverName);
            append(buf, "portNumber", portNumber, 1527);
        }
        append(buf, "databaseName", databaseName);
        append(buf, "description", description);
        append(buf, "user", user);
        append(buf, "password", password);
        append(buf, "dataSourceName", dataSourceName);
        append(buf, "connectionFactoryClass", connectionFactoryClass);
        append(buf, "create", create);
        append(buf, "upgrade", upgrade);
        append(buf, "shutdown", shutdown);
        return buf.toString();
    }

    private void append(StringBuffer buf, String key, Object value) {
        if (value != null) {
            buf.append(';').append(key).append('=').append(value);
        }
    }

    private void append(StringBuffer buf, String key, int value, int def) {
        if (value != def) {
            buf.append(';').append(key).append('=').append(value);
        }
    }

    private void append(StringBuffer buf, String key, boolean value) {
        if (value) {
            buf.append(';').append(key).append("=true");
        }
    }
}
