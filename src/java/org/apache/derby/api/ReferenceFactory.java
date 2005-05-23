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

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.naming.spi.ObjectFactory;

/**
 * Factory for converting DataSource implementations to and from JNDI Reference implementations.
 *
 * @version $Rev$ $Date$
 */
public final class ReferenceFactory implements ObjectFactory {
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable environment) throws Exception {
        // extract the URL from the reference
        Reference ref = (Reference) obj;
        StringRefAddr addr = (StringRefAddr) ref.get("Derby URL");
        String url = (String) addr.getContent();

        // instantiate the DataSource
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = getClass().getClassLoader();
        }
        Class dsClass = cl.loadClass(ref.getClassName());

        // load the properties into the DataSource
        BasicDataSource ds = (BasicDataSource) dsClass.newInstance();
        ds.loadURL(url);
        return ds;
    }
}
