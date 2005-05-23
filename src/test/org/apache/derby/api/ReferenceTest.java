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

import javax.naming.Reference;
import javax.naming.RefAddr;
import javax.naming.StringRefAddr;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class ReferenceTest extends TestCase {
    public void testCreateReference() {
        ReferenceableDataSource ds = new ReferenceableDataSource();
        ds.setServerName("testhost");
        ds.setDatabaseName("testdb");
        ds.setCreate(true);
        Reference ref = ds.getReference();
        assertEquals(1, ref.size());
        RefAddr addr = (StringRefAddr) ref.get(0);
        assertEquals("Derby URL", addr.getType());
        assertEquals("jdbc:derby:;serverName=testhost;databaseName=testdb;create=true", addr.getContent());
    }

    public void testCreateObject() throws Exception {
        StringRefAddr addr = new StringRefAddr("Derby URL", "jdbc:derby:;serverName=testhost;databaseName=testdb;create=true");
        Reference ref = new Reference(ReferenceableDataSource.class.getName(), addr);
        ReferenceFactory factory = new ReferenceFactory();
        Object o = factory.getObjectInstance(ref, null, null, null);
        assertTrue(o instanceof ReferenceableDataSource);
        ReferenceableDataSource ds = (ReferenceableDataSource) o;
        assertEquals("testhost", ds.getServerName());
        assertEquals("testdb", ds.getDatabaseName());
        assertTrue(ds.getCreate());
        assertFalse(ds.getShutdown());
    }
}
