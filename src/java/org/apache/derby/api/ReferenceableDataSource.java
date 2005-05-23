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

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.RefAddr;
import javax.naming.StringRefAddr;

/**
 * Specialization of DerbyDataSource that can be bound to JNDI as a Reference. This is intended for use by application
 * server implementations that actually perform the bind into the directory rather than end user applications that would
 * just retrieve a DataSource.
 * <p/>
 * This functionality should not be added to the base class as the javax.naming package may not be available on all
 * platforms specifically J2ME CDC/FP.
 *
 * @version $Rev$ $Date$
 */
public class ReferenceableDataSource extends DerbyDataSource implements Referenceable {
    public Reference getReference() {
        RefAddr addr = new StringRefAddr("Derby URL", toURL());
        return new Reference(getClass().getName(), addr);
    }
}
