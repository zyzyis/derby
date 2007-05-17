/*

   Derby - Class org.apache.derby.impl.sql.execute.GenericExecutionContext

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to you under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.execute.ExecutionContext;
import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.derby.iapi.sql.execute.ResultSetFactory;
import org.apache.derby.iapi.sql.execute.ResultSetStatisticsFactory;

import org.apache.derby.iapi.sql.ResultSet;

import org.apache.derby.iapi.services.sanity.SanityManager;

import org.apache.derby.iapi.services.context.ContextImpl;
import org.apache.derby.iapi.services.context.ContextManager;

import org.apache.derby.iapi.services.monitor.Monitor;

import org.apache.derby.iapi.error.StandardException;

import java.util.Properties;
import org.apache.derby.iapi.error.ExceptionSeverity;
/**
 * ExecutionContext stores the result set factory to be used by
 * the current connection, and manages execution-level connection
 * activities.
 * <p>
 * An execution context is expected to be on the stack for the
 * duration of the connection.
 *
 */
class GenericExecutionContext
	extends ContextImpl 
	implements ExecutionContext {

	//
	// class implementation
	//
	private ResultSetFactory rsFactory;
	private ResultSetStatisticsFactory rssFactory;
	private ExecutionFactory execFactory;

	//
	// ExecutionContext interface
	//
	/**
	 * Get the ResultSetFactory from this ExecutionContext.
	 *
	 * @return	The result set factory associated with this
	 *		ExecutionContext
	 */
	public ResultSetFactory getResultSetFactory() 
	{
		/* null rsFactory may have been passed to
		 * constructor in order to speed up boot time.
		 */
		if (rsFactory == null)
		{
			rsFactory = execFactory.getResultSetFactory();
		}
		return rsFactory;
	}

	/**
	 * Get the ResultSetStatisticsFactory from this ExecutionContext.
	 *
	 * @return	The result set statistics factory associated with this
	 *		ExecutionContext
	 *
	 * @exception StandardException		Thrown on error
	 */
	public ResultSetStatisticsFactory getResultSetStatisticsFactory()
					throws StandardException {
		if (rssFactory == null) {
			rssFactory = (ResultSetStatisticsFactory)
				Monitor.bootServiceModule(
									false,
									execFactory,
									ResultSetStatisticsFactory.MODULE,
									(Properties) null);
		}

		return rssFactory;
	}

	public ExecutionFactory getExecutionFactory() {
		return execFactory;
	}

	//
	// Context interface
	//

	/**
	 * @exception StandardException Thrown on error
	 */
	public void cleanupOnError(Throwable error) throws StandardException {
		if (error instanceof StandardException) {

			StandardException se = (StandardException) error;
            int severity = se.getSeverity();
            if (severity >= ExceptionSeverity.SESSION_SEVERITY)
            {
               popMe();
               return;
            }
			if (severity > ExceptionSeverity.STATEMENT_SEVERITY)
            {
 				return;
            }

			return;
		}
	}

	//
	// class interface
	//
	GenericExecutionContext(
		    ResultSetFactory rsf,
			ContextManager cm,
			ExecutionFactory ef)
	{

		super(cm, ExecutionContext.CONTEXT_ID);
		rsFactory = rsf;
		execFactory = ef;
	}

}
