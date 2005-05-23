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
package org.apache.derby.util;

import java.io.File;
import java.io.PrintWriter;
import java.sql.SQLException;

import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.apache.derby.api.DerbyDataSource;
import org.apache.derby.drda.NetworkServerControl;
import org.apache.derby.impl.EmbeddedConnectionFactory;

/**
 * @version $Rev$ $Date$
 */
public class ClientTestSuite extends TestSuite {
    private NetworkServerControl nsc;
    private String tmpDir;

    public ClientTestSuite() {
    }

    public ClientTestSuite(Class aClass, String s) {
        super(aClass, s);
    }

    public ClientTestSuite(Class aClass) {
        super(aClass);
    }

    public ClientTestSuite(String s) {
        super(s);
    }

    public void run(TestResult testResult) {
        createServer();
        try {
            super.run(testResult);
        } finally {
            shutdownServer();
            delete(new File(tmpDir));
        }
    }

    protected void createServer() {
        tmpDir = System.getProperty("java.io.tmpdir") + "/netTest";
        System.setProperty("derby.system.home", tmpDir);
        try {
            nsc = new NetworkServerControl();
            nsc.start(new PrintWriter(System.out));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void shutdownServer() {
        try {
            nsc.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
        DerbyDataSource ds = new DerbyDataSource();
        ds.setShutdown(true);
        try {
            new EmbeddedConnectionFactory().getConnection(ds);
        } catch (SQLException e) {
            // ok
        }
    }

    private void delete(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                delete(file);
            }
        }
        dir.delete();
    }
}
