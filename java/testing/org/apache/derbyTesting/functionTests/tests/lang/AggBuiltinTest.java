/**
 *  Derby - Class org.apache.derbyTesting.functionTests.tests.lang.AggBuiltinTest
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.derbyTesting.functionTests.tests.lang;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import junit.framework.Test;
import org.apache.derbyTesting.junit.BaseJDBCTestCase;
import org.apache.derbyTesting.junit.JDBC;
import org.apache.derbyTesting.junit.TestConfiguration;

/**
 * Test aggregate built-ins
 */

public final class AggBuiltinTest extends BaseJDBCTestCase {

    private Statement st;
    private ResultSet rs;
    private String [] expColNames;
    private String [][] expRS;
    private final String NULLS_ELIMINATED = "01003";
    private SQLWarning sqlWarn = null;
    private final String[][] SINGLE_NULL_ROW = new String[][]{{null}};
    /**
     * Public constructor required for running test as standalone JUnit.
     * @param name test name
     */
    public AggBuiltinTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return TestConfiguration.defaultSuite(AggBuiltinTest.class);
    }

    public void testBuiltinAggregates() throws Exception
    {
        st = createStatement();

        try {

            avg();
            count();
            countStar();
            sum();
            max();
            min();

        } finally {
            try {
                st.close();
            } catch (SQLException e) {}

            st = null;
            rs = null;
            expColNames = null;
            expRS = null;
            sqlWarn = null;
        }
    }


    private void avg() throws SQLException {
        x("create table t (i int, s smallint, l bigint,"
            + "             c char(10), v varchar(50), lvc long varchar,"
            + "             d double precision, r real, "
            + "             dt date, t time, ts timestamp,"
            + "             b char(2) for bit data, bv varchar(8) for bit "
            + "data, lbv long varchar for bit data,"
            + "             dc decimal(5,2))");

        // empty table

        x("create table empty (i int, s smallint, l bigint,"
            + "             c char(10), v varchar(50), lvc long varchar,"
            + "             d double precision, r real, "
            + "             dt date, t time, ts timestamp,"
            + "             b char(2) for bit data, bv varchar(8) for bit "
            + "data, lbv long varchar for bit data,"
            + "             dc decimal(5,2))");

        // populate tables

        x("insert into t (i) values (null)");

        x("insert into t (i) values (null)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', 'also duplicated',"
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', x'0000111100001111', X'1234', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', 'also duplicated',"
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', X'1234', 111.11)");

        x("insert into t values (1, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', 'also duplicated',"
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', X'1234', 111.11)");

        x("insert into t values (0, 200, 1000000,"
            + "                   'duplicate', 'this is duplicated', 'also duplicated',"
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', X'1234', 222.22)");

        e("42802",
            "insert into t values (0, 100, 2000000,"
            + "                   'duplicate', 'this is duplicated', 'also duplicated',"
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', X'1234', 222.22)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'goodbye', 'this is duplicated', 'also duplicated',"
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', X'1234', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'noone is here', 'jimmie noone "
            + "was here',"
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', X'1234', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', 'also duplicated',"
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', X'1234', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', 'also duplicated',"
            + "                   100.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', X'1234', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', 'also duplicated',"
            + "                   200.0e0, 100.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', X'1234', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', 'also duplicated',"
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-09-09'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', X'1234', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', 'also duplicated',"
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:55:55'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', X'1234', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', 'also duplicated',"
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:55:55'),"
            + "                   X'12af', X'0000111100001111', X'1234', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', 'also duplicated',"
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'ffff', X'0000111100001111', X'1234', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', 'also duplicated',"
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'1111111111111111', X'1234', 111.11)");

        //------------------------------------
        // NEGATIVE TESTS
        //------------------------------------
        // Cannot aggregate datatypes that don't support NumberDataValue

        e("42Y22", "select avg(c) from t");

        e("42Y22", "select avg(v) from t");

        e("42Y22", "select avg(lvc) from t");

        e("42Y22", "select avg(dt) from t");

        e("42Y22", "select avg(t) from t");

        e("42Y22", "select avg(ts) from t");

        e("42Y22", "select avg(b) from t");

        e("42Y22", "select avg(bv) from t");

        e("42Y22", "select avg(lbv) from t");

        e("42Y22", "select avg(c) from t group by c");

        e("42Y22", "select avg(v) from t group by c");

        e("42Y22", "select avg(lvc) from t group by c");

        e("42Y22", "select avg(dt) from t group by c");

        e("42Y22", "select avg(t) from t group by c");

        e("42Y22", "select avg(ts) from t group by c");

        e("42Y22", "select avg(b) from t group by c");

        e("42Y22", "select avg(bv) from t group by c");

        e("42Y22", "select avg(lbv) from t group by c");

        // long varchar datatypes too

        x("create table t1 (c1 long varchar)");

        e("42Y22", "select avg(c1) from t1");

        x("drop table t1");

        // constants

        e("42Y22", "select avg('hello') from t");

        e("42Y22", "select avg(X'11') from t");

        e("42Y22", "select avg(date('1999-06-06')) from t");

        e("42Y22", "select avg(time('12:30:30')) from t");

        e("42Y22", "select avg(timestamp('1999-06-06 12:30:30')) from t");

        //-------------------------
        // NULL AGGREGATION
        //-------------------------
        // scalar

        q("select avg(i) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        q("select avg(s) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        q("select avg(d) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        q("select avg(l) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        q("select avg(r) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        q("select avg(dc) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        // variations

        q("select avg(i), avg(s), avg(r), avg(l) from empty");

        expColNames = new String [] {"1", "2", "3", "4"};
        JDBC.assertColumnNames(rs, expColNames);

        expRS = new String [][]{{null, null, null, null}};
        ok();

        q("select avg(i+1) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        // vector

        q("select avg(i) from empty group by i");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select avg(s) from empty group by s");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select avg(d) from empty group by d");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select avg(l) from empty group by l");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select avg(r) from empty group by r");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select avg(dc) from empty group by dc");
        c1();
        JDBC.assertDrainResults(rs, 0);

        //------------------------------
        // BASIC ACCEPTANCE TESTS
        //------------------------------

        q("select avg(i) from t");
        c1();

        expRS = new String [][]
        {
            {"0"}
        };

        ok(NULLS_ELIMINATED);

        q("select avg(s) from t");
        c1();

        expRS = new String [][]{{"107"}};
        ok(NULLS_ELIMINATED);

        q("select avg(d) from t");
        c1();

        expRS = new String [][]{{"192.85714285714286"}};
        ok(NULLS_ELIMINATED);

        q("select avg(l) from t");
        c1();

        expRS = new String [][]{{"1000000"}};
        ok(NULLS_ELIMINATED);

        q("select avg(r) from t");
        c1();

        expRS = new String [][]{{"192.85715"}};
        ok(NULLS_ELIMINATED);

        q("select avg(dc) from t");
        c1();

        expRS = new String [][]{{"119.0464"}};
        ok(NULLS_ELIMINATED);

        q("select avg(i) from t group by i");
        c1();

        expRS = new String [][]
        {
            {"0"},
            {"1"},
            {null}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});

        q("select avg(s) from t group by s");
        c1();

        expRS = new String [][]
        {
            {"100"},
            {"200"},
            {null}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});

        q("select avg(d) from t group by d");
        c1();

        expRS = new String [][]
        {
            {"100.0"},
            {"200.0"},
            {null}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});

        q("select avg(l) from t group by l");
        c1();

        expRS = new String [][]
        {
            {"1000000"},
            {null}
        };

        ok(new String[]{null, NULLS_ELIMINATED});

        q("select avg(r) from t group by r");
        c1();

        expRS = new String [][]
        {
            {"100.0"},
            {"200.0"},
            {null}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select avg(dc), sum(dc), count(dc) from t group by dc");

        expColNames = new String [] {"1", "2", "3"};
        JDBC.assertColumnNames(rs, expColNames);

        expRS = new String [][]
        {
            {"111.1100", "1444.43", "13"},
            {"222.2200", "222.22", "1"},
            {null, null, "0"}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        // constants

        q("select avg(1) from t");
        c1();

        expRS = new String [][]{{"1"}};
        ok();

        q("select avg(1.1) from t");
        c1();

        expRS = new String [][]{{"1.1000"}};
        ok();

        q("select avg(1e1) from t");
        c1();

        expRS = new String [][]{{"10.0"}};
        ok();

        q("select avg(1) from t group by i");
        c1();

        expRS = new String [][]
        {
            {"1"},
            {"1"},
            {"1"}
        };

        ok();

        q("select avg(1.1) from t group by r");
        c1();

        expRS = new String [][]
        {
            {"1.1000"},
            {"1.1000"},
            {"1.1000"}
        };

        ok();

        q("select avg(1e1) from t group by r");
        c1();

        expRS = new String [][]
        {
            {"10.0"},
            {"10.0"},
            {"10.0"}
        };

        ok();

        // multicolumn grouping

        q("select avg(i), avg(l), avg(r) from t group by i, dt, b");

        expColNames = new String [] {"1", "2", "3"};
        JDBC.assertColumnNames(rs, expColNames);

        expRS = new String [][]
        {
            {"0", "1000000", "190.90909"},
            {"0", "1000000", "200.0"},
            {"0", "1000000", "200.0"},
            {"1", "1000000", "200.0"},
            {null, null, null}
        };

        ok(new String[]{null, null, null, null, NULLS_ELIMINATED});


        q("select i, dt, avg(i), avg(r), avg(l), l from t "
            + "group by i, dt, b, l");

        expColNames = new String [] {"I", "DT", "3", "4", "5", "L"};
        JDBC.assertColumnNames(rs, expColNames);

        expRS = new String [][]
        {
            {"0", "1992-01-01", "0", "190.90909", "1000000", "1000000"},
            {"0", "1992-01-01", "0", "200.0", "1000000", "1000000"},
            {"0", "1992-09-09", "0", "200.0", "1000000", "1000000"},
            {"1", "1992-01-01", "1", "200.0", "1000000", "1000000"},
            {null, null, null, null, null, null}
        };

        ok(new String[]{null, null, null, null, NULLS_ELIMINATED});


        // group by expression

        q("select avg(expr1), avg(expr2)"
            + "from (select i * s, r * 2 from t) t (expr1, expr2) "
            + "group by expr2, expr1");

        c2();

        expRS = new String [][]
        {
            {"0", "200.0"},
            {"0", "400.0"},
            {"100", "400.0"},
            {null, null}
        };

        ok(new String[]{null, null, null, NULLS_ELIMINATED});


        // distinct and group by

        q("select distinct avg(i) from t group by i, dt");
        c1();

        expRS = new String [][]
        {
            {"0"},
            {"1"},
            {null}
        };

        ok(new String[]{NULLS_ELIMINATED, NULLS_ELIMINATED, NULLS_ELIMINATED});



        // insert select

        x("create table tmp (x int, y smallint)");

        x("insert into tmp (x, y) select avg(i), avg(s) from t");

        if (usingEmbedded())
        {
            if (sqlWarn == null)
                sqlWarn = st.getWarnings();
            if (sqlWarn == null)
                sqlWarn = getConnection().getWarnings();
            assertNotNull("Expected warning but found none", sqlWarn);
            assertSQLState(NULLS_ELIMINATED, sqlWarn);
            sqlWarn = null;
        }

        q("select * from tmp");

        expColNames = new String [] {"X", "Y"};
        JDBC.assertColumnNames(rs, expColNames);

        expRS = new String [][]{{"0", "107"}};
        ok();

        x("insert into tmp (x, y) select avg(i), avg(s) from t "
            + "group by b");

        if (usingEmbedded())
        {
            if (sqlWarn == null)
                sqlWarn = st.getWarnings();
            if (sqlWarn == null)
                sqlWarn = getConnection().getWarnings();
            assertNotNull("Expected warning but found none", sqlWarn);
            assertSQLState(NULLS_ELIMINATED, sqlWarn);
            sqlWarn = null;
        }

        q("select * from tmp");

        expColNames = new String [] {"X", "Y"};
        JDBC.assertColumnNames(rs, expColNames);

        expRS = new String [][]
        {
            {"0", "107"},
            {"0", "107"},
            {"0", "100"},
            {null, null}
        };

        ok();

        x("drop table tmp");

        // some accuracy tests

        x("create table tmp (x int)");

        x("insert into tmp values (2147483647),"
            + "                     (2147483647),"
            + "                     (2147483647),"
            + "                     (2147483647),"
            + "                     (2147483647),"
            + "                     (2147483647),"
            + "                     (2147483647),"
            + "                     (2147483647),"
            + "                     (2147483647),"
            + "                     (2147483647),"
            + "                     (2147483647),"
            + "                     (2147483647),"
            + "                     (2147483647),"
            + "                     (2147483647),"
            + "                     (2147483647),"
            + "                     (2147483647),"
            + "                     (2147483647),"
            + "                     (2147483647),"
            + "                     (2147483647),"
            + "                     (2147483647),"
            + "                     (2147483647),"
            + "                     (2147483647)");

        q("values(2147483647)");
        c1();

        expRS = new String [][]{{"2147483647"}};
        ok();

        q("select avg(x) from tmp");
        c1();

        expRS = new String [][]{{"2147483647"}};
        ok();

        q("select avg(-(x - 1)) from tmp");
        c1();

        expRS = new String [][]{{"-2147483646"}};
        ok();

        q("select avg(x) from tmp group by x");
        c1();

        expRS = new String [][]{{"2147483647"}};
        ok();

        q("select avg(-(x - 1)) from tmp group by x");
        c1();

        expRS = new String [][]{{"-2147483646"}};
        ok();

        x("drop table tmp");

        // Now lets try some simple averages to see what type of
        // accuracy we get

        x("create table tmp(x double precision, y int)");

        PreparedStatement scalar = prepareStatement(
            "select avg(x) from tmp");

        PreparedStatement vector = prepareStatement(
            "select avg(x) from tmp group by y");

        x("insert into tmp values (1,1)");

        rs = scalar.executeQuery();
        c1();

        expRS = new String [][]{{"1.0"}};
        ok();

        rs = vector.executeQuery();
        c1();

        expRS = new String [][]{{"1.0"}};
        ok();

        x("insert into tmp values (2,1)");

        rs = scalar.executeQuery();
        c1();

        expRS = new String [][]{{"1.5"}};
        ok();

        rs = vector.executeQuery();
        c1();

        expRS = new String [][]{{"1.5"}};
        ok();

        x("insert into tmp values (3,1)");

        rs = scalar.executeQuery();
        c1();

        expRS = new String [][]{{"2.0"}};
        ok();

        rs = vector.executeQuery();
        c1();

        expRS = new String [][]{{"2.0"}};
        ok();

        x("insert into tmp values (4,1)");

        rs = scalar.executeQuery();
        c1();

        expRS = new String [][]{{"2.5"}};
        ok();

        rs = vector.executeQuery();
        c1();

        expRS = new String [][]{{"2.5"}};
        ok();

        x("insert into tmp values (5,1)");

        rs = scalar.executeQuery();
        c1();

        expRS = new String [][]{{"3.0"}};
        ok();

        rs = vector.executeQuery();
        c1();

        expRS = new String [][]{{"3.0"}};
        ok();

        x("insert into tmp values (6,1)");

        rs = scalar.executeQuery();
        c1();

        expRS = new String [][]{{"3.5"}};
        ok();

        rs = vector.executeQuery();
        c1();

        expRS = new String [][]{{"3.5"}};
        ok();

        x("insert into tmp values (7,1)");

        rs = scalar.executeQuery();
        c1();

        expRS = new String [][]{{"4.0"}};
        ok();

        rs = vector.executeQuery();
        c1();

        expRS = new String [][]{{"4.0"}};
        ok();

        x("insert into tmp values (10000,1)");

        rs = scalar.executeQuery();
        c1();

        expRS = new String [][]{{"1253.5"}};
        ok();

        rs = vector.executeQuery();
        c1();

        expRS = new String [][]{{"1253.5"}};
        ok();

        scalar.close();
        vector.close();

        // drop tables
        x("drop table tmp");
        x("drop table t");
        x("drop table empty");
    }

    private void count() throws SQLException {
        // ** insert count.sql create an all types tables

        x("create table t (i int, s smallint, l bigint,"
            + "             c char(10), v varchar(50), lvc long varchar,"
            + "             d double precision, r real, "
            + "             dt date, t time, ts timestamp,"
            + "             b char(2) for bit data, bv varchar(8) for bit "
            + "data, lbv long varchar for bit data,"
            + "             dc decimal(5,2))");

        // empty table

        x("create table empty (i int, s smallint, l bigint,"
            + "             c char(10), v varchar(50), lvc long varchar,"
            + "             d double precision, r real, "
            + "             dt date, t time, ts timestamp,"
            + "             b char(2) for bit data, bv varchar(8) for bit "
            + "data, lbv long varchar for bit data,"
            + "             dc decimal(5,2))");

        // bit maps to Byte[], so can't test for now populate tables

        x("insert into t (i) values (null)");

        x("insert into t (i) values (null)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', 'also duplicated',"
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', X'1234', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', 'also duplicated',"
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', X'1234', 111.11)");

        x("insert into t values (1, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', 'also duplicated',"
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', X'1234', 111.11)");

        x("insert into t values (0, 200, 1000000,"
            + "                   'duplicate', 'this is duplicated', 'also duplicated',"
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', X'1234', 222.22)");

        x("insert into t values (0, 100, 2000000,"
            + "                   'duplicate', 'this is duplicated', 'also duplicated',"
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', X'1234', 222.22)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'goodbye', 'this is duplicated', 'also duplicated',"
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', X'1234', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'noone is here', 'jimmie noone "
            + "was here',"
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', X'1234', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', 'also duplicated',"
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', X'1234', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', 'also duplicated',"
            + "                   100.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', X'1234', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', 'also duplicated',"
            + "                   200.0e0, 100.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', X'1234', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', 'also duplicated',"
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-09-09'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', X'1234', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', 'also duplicated',"
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:55:55'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', X'1234', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', 'also duplicated',"
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:55:55'),"
            + "                   X'12af', X'0000111100001111', X'1234', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', 'also duplicated',"
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'ffff', X'0000111100001111', X'1234', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', 'also duplicated',"
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'1111111111111111', X'1234', 111.11)");

        //-------------------------
        // NULL AGGREGATION
        //-------------------------

        //scalar

        q("select count(i) from empty");
        c1();

        expRS = new String [][]{{"0"}};
        ok();

        q("select count(s) from empty");
        c1();

        expRS = new String [][]{{"0"}};
        ok();

        q("select count(l) from empty");
        c1();

        expRS = new String [][]{{"0"}};
        ok();

        q("select count(c) from empty");
        c1();

        expRS = new String [][]{{"0"}};
        ok();

        q("select count(v) from empty");
        c1();

        expRS = new String [][]{{"0"}};
        ok();

        q("select count(lvc) from empty");
        c1();

        expRS = new String [][]{{"0"}};
        ok();

        q("select count(d) from empty");
        c1();

        expRS = new String [][]{{"0"}};
        ok();

        q("select count(r) from empty");
        c1();

        expRS = new String [][]{{"0"}};
        ok();

        q("select count(dt) from empty");
        c1();

        expRS = new String [][]{{"0"}};
        ok();

        q("select count(t) from empty");
        c1();

        expRS = new String [][]{{"0"}};
        ok();

        q("select count(ts) from empty");
        c1();

        expRS = new String [][]{{"0"}};
        ok();

        q("select count(b) from empty");
        c1();

        expRS = new String [][]{{"0"}};
        ok();

        q("select count(bv) from empty");
        c1();

        expRS = new String [][]{{"0"}};
        ok();

        // bug: should fail in db2 mode after for bit data is
        // completely implemented

        q("select count(lbv) from empty");
        c1();

        expRS = new String [][]{{"0"}};
        ok();

        q("select count(dc) from empty");
        c1();

        expRS = new String [][]{{"0"}};
        ok();

        // variations

        q("select count(i), count(b), count(i), count(s) from empty");

        expColNames = new String [] {"1", "2", "3", "4"};
        JDBC.assertColumnNames(rs, expColNames);

        expRS = new String [][]{{"0", "0", "0", "0"}};
        ok();

        q("select count(i+1) from empty");
        c1();

        expRS = new String [][]{{"0"}};
        ok();

        // vector

        q("select count(i) from empty group by i");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select count(s) from empty group by s");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select count(l) from empty group by l");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select count(c) from empty group by c");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select count(v) from empty group by v");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select count(d) from empty group by d");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select count(r) from empty group by r");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select count(dt) from empty group by dt");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select count(t) from empty group by t");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select count(ts) from empty group by ts");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select count(b) from empty group by b");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select count(bv) from empty group by bv");
        c1();
        JDBC.assertDrainResults(rs, 0);

        e("X0X67", "select count(lbv) from empty group by lbv");

        q("select count(dc) from empty group by dc");
        c1();
        JDBC.assertDrainResults(rs, 0);

        //------------------------------
        // BASIC ACCEPTANCE TESTS
        //------------------------------

        q("select count(i) from t");
        c1();

        expRS = new String [][]{{"15"}};
        ok(NULLS_ELIMINATED);


        q("select count(s) from t");
        c1();

        expRS = new String [][]{{"15"}};
        ok(NULLS_ELIMINATED);


        q("select count(l) from t");
        c1();

        expRS = new String [][]{{"15"}};
        ok(NULLS_ELIMINATED);


        q("select count(c) from t");
        c1();

        expRS = new String [][]{{"15"}};
        ok(NULLS_ELIMINATED);


        q("select count(v) from t");
        c1();

        expRS = new String [][]{{"15"}};
        ok(NULLS_ELIMINATED);


        q("select count(lvc) from t");
        c1();

        expRS = new String [][]{{"15"}};
        ok(NULLS_ELIMINATED);


        q("select count(d) from t");
        c1();

        expRS = new String [][]{{"15"}};
        ok(NULLS_ELIMINATED);


        q("select count(r) from t");
        c1();

        expRS = new String [][]{{"15"}};
        ok(NULLS_ELIMINATED);


        q("select count(dt) from t");
        c1();

        expRS = new String [][]{{"15"}};
        ok(NULLS_ELIMINATED);


        q("select count(t) from t");
        c1();

        expRS = new String [][]{{"15"}};
        ok(NULLS_ELIMINATED);


        q("select count(ts) from t");
        c1();

        expRS = new String [][]{{"15"}};
        ok(NULLS_ELIMINATED);


        q("select count(b) from t");
        c1();

        expRS = new String [][]{{"15"}};
        ok(NULLS_ELIMINATED);


        q("select count(bv) from t");
        c1();

        expRS = new String [][]{{"15"}};
        ok(NULLS_ELIMINATED);


        q("select count(lbv) from t");
        c1();

        expRS = new String [][]{{"15"}};
        ok(NULLS_ELIMINATED);


        q("select count(dc) from t");
        c1();

        expRS = new String [][]{{"15"}};
        ok(NULLS_ELIMINATED);


        q("select count(i) from t group by i");
        c1();

        expRS = new String [][]
        {
            {"14"},
            {"1"},
            {"0"}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select count(s) from t group by s");
        c1();

        expRS = new String [][]
        {
            {"14"},
            {"1"},
            {"0"}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select count(l) from t group by l");
        c1();

        expRS = new String [][]
        {
            {"14"},
            {"1"},
            {"0"}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select count(c) from t group by c");
        c1();

        expRS = new String [][]
        {
            {"14"},
            {"1"},
            {"0"}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select count(v) from t group by v");
        c1();

        expRS = new String [][]
        {
            {"1"},
            {"14"},
            {"0"}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select count(d) from t group by d");
        c1();

        expRS = new String [][]
        {
            {"1"},
            {"14"},
            {"0"}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select count(r) from t group by r");
        c1();

        expRS = new String [][]
        {
            {"1"},
            {"14"},
            {"0"}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select count(dt) from t group by dt");
        c1();

        expRS = new String [][]
        {
            {"14"},
            {"1"},
            {"0"}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select count(t) from t group by t");
        c1();

        expRS = new String [][]
        {
            {"14"},
            {"1"},
            {"0"}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select count(ts) from t group by ts");
        c1();

        expRS = new String [][]
        {
            {"14"},
            {"1"},
            {"0"}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select count(b) from t group by b");
        c1();

        expRS = new String [][]
        {
            {"14"},
            {"1"},
            {"0"}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select count(bv) from t group by bv");
        c1();

        expRS = new String [][]
        {
            {"14"},
            {"1"},
            {"0"}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        e("X0X67", "select count(lbv) from t group by lbv");

        q("select count(dc) from t group by dc");
        c1();

        expRS = new String [][]
        {
            {"13"},
            {"2"},
            {"0"}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        // constants

        q("select count(1) from t");
        c1();

        expRS = new String [][]{{"17"}};
        ok();

        q("select count('hello') from t");
        c1();

        expRS = new String [][]{{"17"}};
        ok();

        q("select count(1.1) from t");
        c1();

        expRS = new String [][]{{"17"}};
        ok();

        q("select count(1e1) from t");
        c1();

        expRS = new String [][]{{"17"}};
        ok();

        q("select count(X'11') from t");
        c1();

        expRS = new String [][]{{"17"}};
        ok();

        q("select count(date('1999-06-06')) from t");
        c1();

        expRS = new String [][]{{"17"}};
        ok();

        q("select count(time('12:30:30')) from t");
        c1();

        expRS = new String [][]{{"17"}};
        ok();

        q("select count(timestamp('1999-06-06 12:30:30')) from t");
        c1();

        expRS = new String [][]{{"17"}};
        ok();

        q("select count(1) from t group by i");
        c1();

        expRS = new String [][]
        {
            {"14"},
            {"1"},
            {"2"}
        };

        ok();

        q("select count('hello') from t group by c");
        c1();

        expRS = new String [][]
        {
            {"14"},
            {"1"},
            {"2"}
        };

        ok();

        q("select count(1.1) from t group by dc");
        c1();

        expRS = new String [][]
        {
            {"13"},
            {"2"},
            {"2"}
        };

        ok();

        q("select count(1e1) from t group by r");
        c1();

        expRS = new String [][]
        {
            {"1"},
            {"14"},
            {"2"}
        };

        ok();

        q("select count(X'11') from t group by b");
        c1();

        expRS = new String [][]
        {
            {"14"},
            {"1"},
            {"2"}
        };

        ok();

        q("select count(date('1999-06-06')) from t group by dt");
        c1();

        expRS = new String [][]
        {
            {"14"},
            {"1"},
            {"2"}
        };

        ok();

        q("select count(time('12:30:30')) from t group by t");
        c1();

        expRS = new String [][]
        {
            {"14"},
            {"1"},
            {"2"}
        };

        ok();

        q("select count(timestamp('1999-06-06 12:30:30')) from "
            + "t group by ts");
        c1();

        expRS = new String [][]
        {
            {"14"},
            {"1"},
            {"2"}
        };

        ok();

        // multicolumn grouping

        q("select count(i), count(dt), count(b) from t group "
            + "by i, dt, b");

        expColNames = new String [] {"1", "2", "3"};
        JDBC.assertColumnNames(rs, expColNames);

        expRS = new String [][]
        {
            {"12", "12", "12"},
            {"1", "1", "1"},
            {"1", "1", "1"},
            {"1", "1", "1"},
            {"0", "0", "0"}
        };

        ok(new String[]{null, null, null, null, NULLS_ELIMINATED});


        q("select l, dt, count(i), count(dt), count(b), i from "
            + "t group by i, dt, b, l");

        expColNames = new String [] {"L", "DT", "3", "4", "5", "I"};
        JDBC.assertColumnNames(rs, expColNames);

        expRS = new String [][]
        {
            {"1000000", "1992-01-01", "11", "11", "11", "0"},
            {"2000000", "1992-01-01", "1", "1", "1", "0"},
            {"1000000", "1992-01-01", "1", "1", "1", "0"},
            {"1000000", "1992-09-09", "1", "1", "1", "0"},
            {"1000000", "1992-01-01", "1", "1", "1", "1"},
            {null, null, "0", "0", "0", null}
        };

        ok(new String[]{null, null, null, null, null, NULLS_ELIMINATED});


        // group by expression

        q("select count(expr1), count(expr2)"
            + "from (select i * s, c || v from t) t (expr1, expr2) "
            + "group by expr2, expr1");
        c2();

        expRS = new String [][]
        {
            {"1", "1"},
            {"12", "12"},
            {"1", "1"},
            {"1", "1"},
            {"0", "0"}
        };

        ok(new String[]{null, null, null, null, NULLS_ELIMINATED});


        // distinct and group by

        q("select distinct count(i) from t group by i, dt");
        c1();

        expRS = new String [][]
        {
            {"0"},
            {"1"},
            {"13"}
        };

        ok(new String[]{NULLS_ELIMINATED, NULLS_ELIMINATED, NULLS_ELIMINATED});



        // insert select

        x("create table tmp (x int, y smallint)");

        x("insert into tmp (x, y) select count(i), count(c) from t");

        if (usingEmbedded())
        {
            if (sqlWarn == null)
                sqlWarn = st.getWarnings();
            if (sqlWarn == null)
                sqlWarn = getConnection().getWarnings();
            assertNotNull("Expected warning but found none", sqlWarn);
            assertSQLState(NULLS_ELIMINATED, sqlWarn);
            sqlWarn = null;
        }

        q("select * from tmp");

        expColNames = new String [] {"X", "Y"};
        JDBC.assertColumnNames(rs, expColNames);

        expRS = new String [][]{{"15", "15"}};
        ok();

        x("insert into tmp (x, y) select count(i), count(c) "
            + "from t group by b");

        if (usingEmbedded())
        {
            if (sqlWarn == null)
                sqlWarn = st.getWarnings();
            if (sqlWarn == null)
                sqlWarn = getConnection().getWarnings();
            assertNotNull("Expected warning but found none", sqlWarn);
            assertSQLState(NULLS_ELIMINATED, sqlWarn);
            sqlWarn = null;
        }

        q("select * from tmp");

        expColNames = new String [] {"X", "Y"};
        JDBC.assertColumnNames(rs, expColNames);

        expRS = new String [][]
        {
            {"15", "15"},
            {"14", "14"},
            {"1", "1"},
            {"0", "0"}
        };

        ok();

        x("drop table tmp");

        // drop tables

        x("drop table t");

        x("drop table empty");


    }

    private void countStar() throws SQLException {
        // ** insert countStar.sql Test the COUNT() aggregate
        // create an all types tables

        x("create table t (i int, s smallint, l bigint,"
            + "             c char(10), v varchar(50), lvc long varchar,"
            + "             d double precision, r real, "
            + "             dt date, t time, ts timestamp,"
            + "             b char(2) for bit data, bv varchar(8) for bit "
            + "data, lbv long varchar for bit data)");

        // empty table

        x("create table empty (i int, s smallint, l bigint,"
            + "             c char(10), v varchar(50), lvc long varchar,"
            + "             d double precision, r real, "
            + "             dt date, t time, ts timestamp,"
            + "             b char(2) for bit data, bv varchar(8) for bit "
            + "data, lbv long varchar for bit data)");

        // populate tables

        x("insert into t (i) values (null)");

        x("insert into t (i) values (null)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', 'also duplicated',"
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', X'ABCD')");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', 'also duplicated',"
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', X'ABCD')");

        x("insert into t values (1, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', 'also duplicated',"
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', X'ABCD')");

        x("insert into t values (0, 200, 1000000,"
            + "                   'duplicate', 'this is duplicated', 'also duplicated',"
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', X'ABCD')");

        x("insert into t values (0, 100, 2000000,"
            + "                   'duplicate', 'this is duplicated', 'also duplicated',"
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', X'ABCD')");

        x("insert into t values (0, 100, 1000000,"
            + "                   'goodbye', 'this is duplicated', 'also duplicated',"
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', X'ABCD')");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'noone is here', 'jimmie noone "
            + "was here',"
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', X'ABCD')");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', 'also duplicated',"
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', X'ABCD')");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', 'also duplicated',"
            + "                   100.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', X'ABCD')");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', 'also duplicated',"
            + "                   200.0e0, 100.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', X'ABCD')");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', 'also duplicated',"
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-09-09'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', X'ABCD')");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', 'also duplicated',"
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:55:55'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', X'ABCD')");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', 'also duplicated',"
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:55:55'),"
            + "                   X'12af', X'0000111100001111', X'ABCD')");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', 'also duplicated',"
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'ffff', X'0000111100001111', X'1234')");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', 'also duplicated',"
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'1111111111111111', X'ABCD')");

        //-------------------------
        // NULL AGGREGATION
        //-------------------------

        // scalar

        q("select count(*) from empty");
        c1();

        expRS = new String [][]{{"0"}};
        ok();

        // variations

        q("select count(*), count(*) from empty");
        c2();

        expRS = new String [][]{{"0", "0"}};
        ok();

        // vector

        q("select count(*) from empty group by i");
        c1();
        JDBC.assertDrainResults(rs, 0);

        //------------------------------
        // BASIC ACCEPTANCE TESTS
        //------------------------------

        q("select count(*) from t");
        c1();

        expRS = new String [][]{{"17"}};
        ok();

        q("select count(*) from t group by i");
        c1();

        expRS = new String [][]
        {
            {"14"},
            {"1"},
            {"2"}
        };

        ok();

        // multicolumn grouping

        q("select count(*), count(*), count(*) from t group by i, dt, b");

        expColNames = new String [] {"1", "2", "3"};
        JDBC.assertColumnNames(rs, expColNames);

        expRS = new String [][]
        {
            {"12", "12", "12"},
            {"1", "1", "1"},
            {"1", "1", "1"},
            {"1", "1", "1"},
            {"2", "2", "2"}
        };

        ok();

        // group by expression

        q("select count(*), count(*)"
            + "from (select i * s, c || v from t) t (expr1, expr2) "
            + "group by expr2, expr1");
        c2();

        expRS = new String [][]
        {
            {"1", "1"},
            {"12", "12"},
            {"1", "1"},
            {"1", "1"},
            {"2", "2"}
        };

        ok();

        // distinct and group by

        q("select distinct count(*) from t group by i, dt");
        c1();

        expRS = new String [][]
        {
            {"1"},
            {"2"},
            {"13"}
        };

        ok();

        // view

        x("create view v1 as select * from t");

        q("select count(*) from v1");
        c1();

        expRS = new String [][]{{"17"}};
        ok();

        q("select count(*)+count(*) from v1");
        c1();

        expRS = new String [][]{{"34"}};
        ok();

        x("drop view v1");

        // insert select

        x("create table tmp (x int, y smallint)");

        x("insert into tmp (x, y) select count(*), count(*) from t");

        q("select * from tmp");

        expColNames = new String [] {"X", "Y"};
        JDBC.assertColumnNames(rs, expColNames);

        expRS = new String [][]{{"17", "17"}};
        ok();

        x("insert into tmp (x, y) select count(*), count(*) "
            + "from t group by b");

        q("select * from tmp");

        expColNames = new String [] {"X", "Y"};
        JDBC.assertColumnNames(rs, expColNames);

        expRS = new String [][]
        {
            {"17", "17"},
            {"14", "14"},
            {"1", "1"},
            {"2", "2"}
        };

        ok();



        // drop tables
        x("drop table tmp");
        x("drop table t");
        x("drop table empty");
    }

    private void sum() throws SQLException {
        // ** insert sum.sqlBUGS: sum() on decimal may overflow
        // the decimal,w/o the type system knowing.  so, given
        // dec(1,0),result might be dec(2,0), but return length
        // passedto connectivity is 1 which is wrong.  if we
        // allowthe decimal to grow beyond the preset type, we
        // needto all the type system to get it.  alternatively,
        // need to cast/normalize/setWidth() the result to ensure
        // right type. create an all types tables

        x("create table t (i int, s smallint, l bigint,"
            + "             c char(10), v varchar(50), "
            + "             d double precision, r real, "
            + "             dt date, t time, ts timestamp,"
            + "             b char(2) for bit data, bv varchar(8) for bit "
            + "data, dc decimal(5,2))");

        // empty table

        x("create table empty (i int, s smallint, l bigint,"
            + "             c char(10), v varchar(50), "
            + "             d double precision, r real, "
            + "             dt date, t time, ts timestamp,"
            + "             b char(2) for bit data, bv varchar(8) for bit "
            + "data, dc decimal(5,2))");

        // bit maps to Byte[], so can't test for now populate tables

        x("insert into t (i) values (null)");

        x("insert into t (i) values (null)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', "
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', "
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', 111.11)");

        x("insert into t values (1, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', "
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', 111.11)");

        x("insert into t values (0, 200, 1000000,"
            + "                   'duplicate', 'this is duplicated', "
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', 222.22)");

        x("insert into t values (0, 100, 2000000,"
            + "                   'duplicate', 'this is duplicated', "
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', 222.22)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'goodbye', 'this is duplicated', "
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'noone is here', "
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', "
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', "
            + "                   100.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', "
            + "                   200.0e0, 100.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', "
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-09-09'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', "
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:55:55'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', "
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:55:55'),"
            + "                   X'12af', X'0000111100001111', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', "
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'ffff', X'0000111100001111', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', "
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'1111111111111111', 111.11)");

        // Bit maps to Byte[], so can't test for now

        //------------------------------------
        // NEGATIVE TESTS
        //------------------------------------

        // Cannot aggregate datatypes that don't support NumberDataValue

        e("42Y22", "select sum(c) from t");

        e("42Y22", "select sum(v) from t");

        e("42Y22", "select sum(dt) from t");

        e("42Y22", "select sum(t) from t");

        e("42Y22", "select sum(ts) from t");

        e("42Y22", "select sum(b) from t");

        e("42Y22", "select sum(bv) from t");

        e("42Y22", "select sum(c) from t group by c");

        e("42Y22", "select sum(v) from t group by c");

        e("42Y22", "select sum(dt) from t group by c");

        e("42Y22", "select sum(t) from t group by c");

        e("42Y22", "select sum(ts) from t group by c");

        e("42Y22", "select sum(b) from t group by c");

        e("42Y22", "select sum(bv) from t group by c");

        // long varchar datatypes too

        x("create table t1 (c1 long varchar)");

        e("42Y22", "select sum(c1) from t1");

        x("drop table t1");

        // constants

        e("42Y22", "select sum('hello') from t");

        e("42Y22", "select sum(X'11') from t");

        e("42Y22", "select sum(date('1999-06-06')) from t");

        e("42Y22", "select sum(time('12:30:30')) from t");

        e("42Y22", "select sum(timestamp('1999-06-06 12:30:30')) from t");

        //-------------------------
        // NULL AGGREGATION
        //-------------------------

        // scalar

        q("select sum(i) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        q("select sum(s) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        q("select sum(d) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        q("select sum(l) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        q("select sum(r) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        q("select sum(dc) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        // variations

        q("select sum(i), sum(s), sum(r), sum(l) from empty");

        expColNames = new String [] {"1", "2", "3", "4"};
        JDBC.assertColumnNames(rs, expColNames);

        expRS = new String [][]{{null, null, null, null}};
        ok();

        q("select sum(i+1) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        // vector

        q("select sum(i) from empty group by i");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select sum(s) from empty group by s");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select sum(d) from empty group by d");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select sum(l) from empty group by l");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select sum(r) from empty group by r");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select sum(dc) from empty group by dc");
        c1();
        JDBC.assertDrainResults(rs, 0);

        //------------------------------
        // BASIC ACCEPTANCE TESTS
        //------------------------------

        q("select sum(i) from t");
        c1();

        expRS = new String [][]{{"1"}};
        ok(NULLS_ELIMINATED);


        q("select sum(s) from t");
        c1();

        expRS = new String [][]{{"1600"}};
        ok(NULLS_ELIMINATED);


        q("select sum(d) from t");
        c1();

        expRS = new String [][]{{"2900.0"}};
        ok(NULLS_ELIMINATED);


        q("select sum(l) from t");
        c1();

        expRS = new String [][]{{"16000000"}};
        ok(NULLS_ELIMINATED);


        q("select sum(r) from t");
        c1();

        expRS = new String [][]{{"2900.0"}};
        ok(NULLS_ELIMINATED);


        q("select sum(dc) from t");
        c1();

        expRS = new String [][]{{"1888.87"}};
        ok(NULLS_ELIMINATED);


        q("select sum(i) from t group by i");
        c1();

        expRS = new String [][]
        {
            {"0"},
            {"1"},
            {null}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select sum(s) from t group by s");
        c1();

        expRS = new String [][]
        {
            {"1400"},
            {"200"},
            {null}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select sum(d) from t group by d");
        c1();

        expRS = new String [][]
        {
            {"100.0"},
            {"2800.0"},
            {null}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select sum(l) from t group by l");
        c1();

        expRS = new String [][]
        {
            {"14000000"},
            {"2000000"},
            {null}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select sum(r) from t group by r");
        c1();

        expRS = new String [][]
        {
            {"100.0"},
            {"2800.0"},
            {null}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select sum(dc) from t group by dc");
        c1();

        expRS = new String [][]
        {
            {"1444.43"},
            {"444.44"},
            {null}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        // constants

        q("select sum(1) from t");
        c1();

        expRS = new String [][]{{"17"}};
        ok();

        q("select sum(1.1) from t");
        c1();

        expRS = new String [][]{{"18.7"}};
        ok();

        q("select sum(1e1) from t");
        c1();

        expRS = new String [][]{{"170.0"}};
        ok();

        q("select sum(1) from t group by i");
        c1();

        expRS = new String [][]
        {
            {"14"},
            {"1"},
            {"2"}
        };

        ok();

        q("select sum(1.1) from t group by r");
        c1();

        expRS = new String [][]
        {
            {"1.1"},
            {"15.4"},
            {"2.2"}
        };

        ok();

        q("select sum(1e1) from t group by r");
        c1();

        expRS = new String [][]
        {
            {"10.0"},
            {"140.0"},
            {"20.0"}
        };

        ok();

        // multicolumn grouping

        q("select sum(i), sum(l), sum(r) from t group by i, dt, b");

        expColNames = new String [] {"1", "2", "3"};
        JDBC.assertColumnNames(rs, expColNames);

        expRS = new String [][]
        {
            {"0", "13000000", "2300.0"},
            {"0", "1000000", "200.0"},
            {"0", "1000000", "200.0"},
            {"1", "1000000", "200.0"},
            {null, null, null}
        };

        ok(new String[]{null, null, null, null, NULLS_ELIMINATED});

        q("select i, dt, sum(i), sum(r), sum(l), l from t "
            + "group by i, dt, b, l");

        expColNames = new String [] {"I", "DT", "3", "4", "5", "L"};
        JDBC.assertColumnNames(rs, expColNames);

        expRS = new String [][]
        {
            {"0", "1992-01-01", "0", "2100.0", "11000000", "1000000"},
            {"0", "1992-01-01", "0", "200.0", "2000000", "2000000"},
            {"0", "1992-01-01", "0", "200.0", "1000000", "1000000"},
            {"0", "1992-09-09", "0", "200.0", "1000000", "1000000"},
            {"1", "1992-01-01", "1", "200.0", "1000000", "1000000"},
            {null, null, null, null, null, null}
        };

        ok(new String[]{null, null, null, null, null, NULLS_ELIMINATED});


        // group by expression

        q("select sum(expr1), sum(expr2)"
            + "from (select i * s, r * 2 from t) t (expr1, expr2) "
            + "group by expr2, expr1");
        c2();

        expRS = new String [][]
        {
            {"0", "200.0"},
            {"0", "5200.0"},
            {"100", "400.0"},
            {null, null}
        };

        ok(new String[]{null, null, null, NULLS_ELIMINATED});


        // distinct and group by

        q("select distinct sum(i) from t group by i, dt");
        c1();

        expRS = new String [][]
        {
            {"0"},
            {"1"},
            {null}
        };

        ok(new String[]{NULLS_ELIMINATED, NULLS_ELIMINATED, NULLS_ELIMINATED});



        // insert select

        x("create table tmp (x int, y smallint)");

        x("insert into tmp (x, y) select sum(i), sum(s) from t");

        if (usingEmbedded())
        {
            if (sqlWarn == null)
                sqlWarn = st.getWarnings();
            if (sqlWarn == null)
                sqlWarn = getConnection().getWarnings();
            assertNotNull("Expected warning but found none", sqlWarn);
            assertSQLState(NULLS_ELIMINATED, sqlWarn);
            sqlWarn = null;
        }

        q("select * from tmp");

        expColNames = new String [] {"X", "Y"};
        JDBC.assertColumnNames(rs, expColNames);

        expRS = new String [][]{{"1", "1600"}};
        ok();

        x("insert into tmp (x, y) select sum(i), sum(s) from t "
            + "group by b");

        if (usingEmbedded())
        {
            if (sqlWarn == null)
                sqlWarn = st.getWarnings();
            if (sqlWarn == null)
                sqlWarn = getConnection().getWarnings();
            assertNotNull("Expected warning but found none", sqlWarn);
            assertSQLState(NULLS_ELIMINATED, sqlWarn);
            sqlWarn = null;
        }

        q("select * from tmp");

        expColNames = new String [] {"X", "Y"};
        JDBC.assertColumnNames(rs, expColNames);

        expRS = new String [][]
        {
            {"1", "1600"},
            {"1", "1500"},
            {"0", "100"},
            {null, null}
        };

        ok();

        x("drop table tmp");

        // overflow

        x("create table tmp (x int)");

        x("insert into tmp values (2147483647),"
            + "                     (2147483647)");

        e("22003", "select sum(x) from tmp");

        x("drop table tmp");

        x("create table tmp (x double precision)");

        x("insert into tmp values (2147483647),"
            + "                     (2147483647),"
            + "                     (2147483647),"
            + "                     (2147483647),"
            + "                     (2147483647),"
            + "                     (2147483647),"
            + "                     (2147483647),"
            + "                     (2147483647),"
            + "                     (2147483647)");

        q("select sum(x) from tmp");
        c1();

        expRS = new String [][]{{"1.9327352823E10"}};
        ok();

        // drop tables
        x("drop table tmp");
        x("drop table t");
        x("drop table empty");

    }

    private void max() throws SQLException {
        // ** insert max.sql create an all types tables

        x("create table t (i int, s smallint, l bigint,"
            + "             c char(10), v varchar(50), "
            + "             d double precision, r real, "
            + "             dt date, t time, ts timestamp,"
            + "             b char(2) for bit data, bv varchar(8) for bit "
            + "data, dc decimal(5,2))");

        // empty table

        x("create table empty (i int, s smallint, l bigint,"
            + "             c char(10), v varchar(50), "
            + "             d double precision, r real, "
            + "             dt date, t time, ts timestamp,"
            + "             b char(2) for bit data, bv varchar(8) for bit "
            + "data, dc decimal(5,2))");

        // bit maps to Byte[], so can't test for now populate tables

        x("insert into t (i) values (null)");

        x("insert into t (i) values (null)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', "
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', "
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', 111.11)");

        x("insert into t values (1, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', "
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', 111.11)");

        x("insert into t values (0, 200, 1000000,"
            + "                   'duplicate', 'this is duplicated', "
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', 222.22)");

        x("insert into t values (0, 100, 2000000,"
            + "                   'duplicate', 'this is duplicated', "
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', 222.22)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'goodbye', 'this is duplicated', "
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'noone is here', "
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', "
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', "
            + "                   100.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', "
            + "                   200.0e0, 100.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', "
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-09-09'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', "
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:55:55'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', "
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:55:55'),"
            + "                   X'12af', X'0000111100001111', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', "
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'ffff', X'0000111100001111', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', "
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'1111111111111111', 111.11)");

        //------------------------------------
        // NEGATIVE TESTS
        //------------------------------------

        // long varchar datatypes too

        x("create table t1 (c1 long varchar)");

        e("42Y22", "select max(c1) from t1");

        x("drop table t1");

        //-------------------------
        // NULL AGGREGATION
        //-------------------------

        // scalar

        q("select max(i) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        q("select max(s) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        q("select max(l) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        q("select max(c) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        q("select max(v) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        q("select max(d) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        q("select max(r) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        q("select max(dt) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        q("select max(t) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        q("select max(ts) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        q("select max(b) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        q("select max(bv) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        q("select max(dc) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        // variations

        q("select max(i), max(b), max(i), max(s) from empty");

        expColNames = new String [] {"1", "2", "3", "4"};
        JDBC.assertColumnNames(rs, expColNames);

        expRS = new String [][]{{null, null, null, null}};
        ok();

        q("select max(i+1) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        // vector

        q("select max(i) from empty group by i");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select max(s) from empty group by s");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select max(l) from empty group by l");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select max(c) from empty group by c");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select max(v) from empty group by v");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select max(d) from empty group by d");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select max(r) from empty group by r");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select max(dt) from empty group by dt");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select max(t) from empty group by t");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select max(ts) from empty group by ts");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select max(b) from empty group by b");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select max(bv) from empty group by bv");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select max(dc) from empty group by dc");
        c1();
        JDBC.assertDrainResults(rs, 0);

        //------------------------------
        // BASIC ACCEPTANCE TESTS
        //------------------------------

        q("select max(i) from t");
        c1();

        expRS = new String [][]{{"1"}};
        ok(NULLS_ELIMINATED);


        q("select max(s) from t");
        c1();

        expRS = new String [][]{{"200"}};
        ok(NULLS_ELIMINATED);


        q("select max(l) from t");
        c1();

        expRS = new String [][]{{"2000000"}};
        ok(NULLS_ELIMINATED);


        q("select max(c) from t");
        c1();

        expRS = new String [][]{{"goodbye"}};
        ok(NULLS_ELIMINATED);


        q("select max(v) from t");
        c1();

        expRS = new String [][]{{"this is duplicated"}};
        ok(NULLS_ELIMINATED);


        q("select max(d) from t");
        c1();

        expRS = new String [][]{{"200.0"}};
        ok(NULLS_ELIMINATED);


        q("select max(r) from t");
        c1();

        expRS = new String [][]{{"200.0"}};
        ok(NULLS_ELIMINATED);


        q("select max(dt) from t");
        c1();

        expRS = new String [][]{{"1992-09-09"}};
        ok(NULLS_ELIMINATED);


        q("select max(t) from t");
        c1();

        expRS = new String [][]{{"12:55:55"}};
        ok(NULLS_ELIMINATED);


        q("select max(ts) from t");
        c1();

        expRS = new String [][]{{"1992-01-01 12:55:55.0"}};
        ok(NULLS_ELIMINATED);


        q("select max(b) from t");
        c1();

        expRS = new String [][]{{"ffff"}};
        ok(NULLS_ELIMINATED);


        q("select max(bv) from t");
        c1();

        expRS = new String [][]{{"1111111111111111"}};
        ok(NULLS_ELIMINATED);


        q("select max(dc) from t");
        c1();

        expRS = new String [][]{{"222.22"}};
        ok(NULLS_ELIMINATED);


        q("select max(i) from t group by i");
        c1();

        expRS = new String [][]
        {
            {"0"},
            {"1"},
            {null}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select max(s) from t group by s");
        c1();

        expRS = new String [][]
        {
            {"100"},
            {"200"},
            {null}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select max(l) from t group by l");
        c1();

        expRS = new String [][]
        {
            {"1000000"},
            {"2000000"},
            {null}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select max(c) from t group by c");
        c1();

        expRS = new String [][]
        {
            {"duplicate"},
            {"goodbye"},
            {null}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select max(v) from t group by v");
        c1();

        expRS = new String [][]
        {
            {"noone is here"},
            {"this is duplicated"},
            {null}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select max(d) from t group by d");
        c1();

        expRS = new String [][]
        {
            {"100.0"},
            {"200.0"},
            {null}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select max(r) from t group by r");
        c1();

        expRS = new String [][]
        {
            {"100.0"},
            {"200.0"},
            {null}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select max(dt) from t group by dt");
        c1();

        expRS = new String [][]
        {
            {"1992-01-01"},
            {"1992-09-09"},
            {null}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select max(t) from t group by t");
        c1();

        expRS = new String [][]
        {
            {"12:30:30"},
            {"12:55:55"},
            {null}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select max(ts) from t group by ts");
        c1();

        expRS = new String [][]
        {
            {"1992-01-01 12:30:30.0"},
            {"1992-01-01 12:55:55.0"},
            {null}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select max(b) from t group by b");
        c1();

        expRS = new String [][]
        {
            {"12af"},
            {"ffff"},
            {null}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select max(bv) from t group by bv");
        c1();

        expRS = new String [][]
        {
            {"0000111100001111"},
            {"1111111111111111"},
            {null}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select max(dc) from t group by dc");
        c1();

        expRS = new String [][]
        {
            {"111.11"},
            {"222.22"},
            {null}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        // constants

        q("select max(1) from t");
        c1();

        expRS = new String [][]{{"1"}};
        ok();

        q("select max('hello') from t");
        c1();

        expRS = new String [][]{{"hello"}};
        ok();

        q("select max(1.1) from t");
        c1();

        expRS = new String [][]{{"1.1"}};
        ok();

        q("select max(1e1) from t");
        c1();

        expRS = new String [][]{{"10.0"}};
        ok();

        q("select max(X'11') from t");
        c1();

        expRS = new String [][]{{"11"}};
        ok();

        q("select max(date('1999-06-06')) from t");
        c1();

        expRS = new String [][]{{"1999-06-06"}};
        ok();

        q("select max(time('12:30:30')) from t");
        c1();

        expRS = new String [][]{{"12:30:30"}};
        ok();

        q("select max(timestamp('1999-06-06 12:30:30')) from t");
        c1();

        expRS = new String [][]{{"1999-06-06 12:30:30.0"}};
        ok();

        q("select max(1) from t group by i");
        c1();

        expRS = new String [][]
        {
            {"1"},
            {"1"},
            {"1"}
        };

        ok();

        q("select max('hello') from t group by c");
        c1();

        expRS = new String [][]
        {
            {"hello"},
            {"hello"},
            {"hello"}
        };

        ok();

        q("select max(1.1) from t group by dc");
        c1();

        expRS = new String [][]
        {
            {"1.1"},
            {"1.1"},
            {"1.1"}
        };

        ok();

        q("select max(1e1) from t group by d");
        c1();

        expRS = new String [][]
        {
            {"10.0"},
            {"10.0"},
            {"10.0"}
        };

        ok();

        q("select max(X'11') from t group by b");
        c1();

        expRS = new String [][]
        {
            {"11"},
            {"11"},
            {"11"}
        };

        ok();

        q("select max(date('1999-06-06')) from t group by dt");
        c1();

        expRS = new String [][]
        {
            {"1999-06-06"},
            {"1999-06-06"},
            {"1999-06-06"}
        };

        ok();

        q("select max(time('12:30:30')) from t group by t");
        c1();

        expRS = new String [][]
        {
            {"12:30:30"},
            {"12:30:30"},
            {"12:30:30"}
        };

        ok();

        q("select max(timestamp('1999-06-06 12:30:30')) from t "
            + "group by ts");
        c1();

        expRS = new String [][]
        {
            {"1999-06-06 12:30:30.0"},
            {"1999-06-06 12:30:30.0"},
            {"1999-06-06 12:30:30.0"}
        };

        ok();

        // multicolumn grouping

        q("select max(i), max(dt), max(b) from t group by i, dt, b");

        expColNames = new String [] {"1", "2", "3"};
        JDBC.assertColumnNames(rs, expColNames);

        expRS = new String [][]
        {
            {"0", "1992-01-01", "12af"},
            {"0", "1992-01-01", "ffff"},
            {"0", "1992-09-09", "12af"},
            {"1", "1992-01-01", "12af"},
            {null, null, null}
        };

        ok(new String[]{null, null, null, null, NULLS_ELIMINATED});


        q("select l, dt, max(i), max(dt), max(b), i from t "
            + "group by i, dt, b, l");

        expColNames = new String [] {"L", "DT", "3", "4", "5", "I"};
        JDBC.assertColumnNames(rs, expColNames);

        expRS = new String [][]
        {
            {"1000000", "1992-01-01", "0", "1992-01-01", "12af", "0"},
            {"2000000", "1992-01-01", "0", "1992-01-01", "12af", "0"},
            {"1000000", "1992-01-01", "0", "1992-01-01", "ffff", "0"},
            {"1000000", "1992-09-09", "0", "1992-09-09", "12af", "0"},
            {"1000000", "1992-01-01", "1", "1992-01-01", "12af", "1"},
            {null, null, null, null, null, null}
        };

        ok(new String[]{null, null, null, null, null, NULLS_ELIMINATED});


        // group by expression

        q("select max(expr1), max(expr2)"
            + "from (select i * s, c || v from t) t (expr1, expr2) "
            + "group by expr2, expr1");
        c2();

        expRS = new String [][]
        {
            {"0", "duplicate noone is here"},
            {"0", "duplicate this is duplicated"},
            {"100", "duplicate this is duplicated"},
            {"0", "goodbye   this is duplicated"},
            {null, null}
        };

        ok(new String[]{null, null, null, null, NULLS_ELIMINATED});


        // distinct and group by

        q("select distinct max(i) from t group by i, dt");
        c1();

        expRS = new String [][]
        {
            {"0"},
            {"1"},
            {null}
        };

        ok(new String[]{NULLS_ELIMINATED, NULLS_ELIMINATED, NULLS_ELIMINATED});



        // insert select

        x("create table tmp (x int, y char(20))");

        x("insert into tmp (x, y) select max(i), max(c) from t");

        if (usingEmbedded())
        {
            if (sqlWarn == null)
                sqlWarn = st.getWarnings();
            if (sqlWarn == null)
                sqlWarn = getConnection().getWarnings();
            assertNotNull("Expected warning but found none", sqlWarn);
            assertSQLState(NULLS_ELIMINATED, sqlWarn);
            sqlWarn = null;
        }

        q("select * from tmp");

        expColNames = new String [] {"X", "Y"};
        JDBC.assertColumnNames(rs, expColNames);

        expRS = new String [][]{{"1", "goodbye"}};
        ok();

        x("insert into tmp (x, y) select max(i), max(c) from t "
            + "group by b");

        if (usingEmbedded())
        {
            if (sqlWarn == null)
                sqlWarn = st.getWarnings();
            if (sqlWarn == null)
                sqlWarn = getConnection().getWarnings();
            assertNotNull("Expected warning but found none", sqlWarn);
            assertSQLState(NULLS_ELIMINATED, sqlWarn);
            sqlWarn = null;
        }

        q("select * from tmp");

        expColNames = new String [] {"X", "Y"};
        JDBC.assertColumnNames(rs, expColNames);

        expRS = new String [][]
        {
            {"1", "goodbye"},
            {"1", "goodbye"},
            {"0", "duplicate"},
            {null, null}
        };

        ok();

        // drop tables
        x("drop table tmp");
        x("drop table t");
        x("drop table empty");

    }

    private void min() throws SQLException {
            // ** insert min.sql create an all types tables

        x("create table t (i int, s smallint, l bigint,"
            + "             c char(10), v varchar(50), "
            + "             d double precision, r real, "
            + "             dt date, t time, ts timestamp,"
            + "             b char(2) for bit data, bv varchar(8) for bit "
            + "data, dc decimal(5,2))");

        // empty table

        x("create table empty (i int, s smallint, l bigint,"
            + "             c char(10), v varchar(50), "
            + "             d double precision, r real, "
            + "             dt date, t time, ts timestamp,"
            + "             b char(2) for bit data, bv varchar(8) for bit "
            + "data, dc decimal(5,2))");

        // populate tables

        x("insert into t (i) values (null)");

        x("insert into t (i) values (null)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', "
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', "
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', 111.11)");

        x("insert into t values (1, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', "
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', 111.11)");

        x("insert into t values (0, 200, 1000000,"
            + "                   'duplicate', 'this is duplicated', "
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', 222.22)");

        x("insert into t values (0, 100, 2000000,"
            + "                   'duplicate', 'this is duplicated', "
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', 222.22)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'goodbye', 'this is duplicated', "
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'noone is here', "
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', "
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', "
            + "                   100.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', "
            + "                   200.0e0, 100.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', "
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-09-09'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', "
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:55:55'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'0000111100001111', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', "
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:55:55'),"
            + "                   X'12af', X'0000111100001111', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', "
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'ffff', X'0000111100001111', 111.11)");

        x("insert into t values (0, 100, 1000000,"
            + "                   'duplicate', 'this is duplicated', "
            + "                   200.0e0, 200.0e0, "
            + "                   date('1992-01-01'), time('12:30:30'), "
            + "timestamp('1992-01-01 12:30:30'),"
            + "                   X'12af', X'1111111111111111', 111.11)");

        //------------------------------------
        // NEGATIVE TESTS
        //------------------------------------

        // long varchar datatypes too

        x("create table t1 (c1 long varchar)");

        e("42Y22", "select min(c1) from t1");

        x("drop table t1");

        //-------------------------
        // NULL AGGREGATION
        //-------------------------

        // scalar

        q("select min(i) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        q("select min(s) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        q("select min(l) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        q("select min(c) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        q("select min(v) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        q("select min(d) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        q("select min(r) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        q("select min(dt) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        q("select min(t) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        q("select min(ts) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        q("select min(b) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        q("select min(bv) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        q("select min(dc) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        // variations

        q("select min(i), min(b), min(i), min(s) from empty");

        expColNames = new String [] {"1", "2", "3", "4"};
        JDBC.assertColumnNames(rs, expColNames);

        expRS = new String [][]{{null, null, null, null}};
        ok();

        q("select min(i+1) from empty");
        c1();

        expRS = SINGLE_NULL_ROW;
        ok();

        // vector

        q("select min(i) from empty group by i");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select min(s) from empty group by s");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select min(l) from empty group by l");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select min(c) from empty group by c");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select min(v) from empty group by v");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select min(d) from empty group by d");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select min(r) from empty group by r");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select min(dt) from empty group by dt");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select min(t) from empty group by t");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select min(ts) from empty group by ts");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select min(b) from empty group by b");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select min(bv) from empty group by bv");
        c1();
        JDBC.assertDrainResults(rs, 0);

        q("select min(dc) from empty group by dc");
        c1();
        JDBC.assertDrainResults(rs, 0);

        //------------------------------
        // BASIC ACCEPTANCE TESTS
        //------------------------------

        q("select min(i) from t");
        c1();

        expRS = new String [][]{{"0"}};
        ok(NULLS_ELIMINATED);


        q("select min(s) from t");
        c1();

        expRS = new String [][]{{"100"}};
        ok(NULLS_ELIMINATED);


        q("select min(l) from t");
        c1();

        expRS = new String [][]{{"1000000"}};
        ok(NULLS_ELIMINATED);


        q("select min(c) from t");
        c1();

        expRS = new String [][]{{"duplicate"}};
        ok(NULLS_ELIMINATED);


        q("select min(v) from t");
        c1();

        expRS = new String [][]{{"noone is here"}};
        ok(NULLS_ELIMINATED);


        q("select min(d) from t");
        c1();

        expRS = new String [][]{{"100.0"}};
        ok(NULLS_ELIMINATED);


        q("select min(r) from t");
        c1();

        expRS = new String [][]{{"100.0"}};
        ok(NULLS_ELIMINATED);


        q("select min(dt) from t");
        c1();

        expRS = new String [][]{{"1992-01-01"}};
        ok(NULLS_ELIMINATED);


        q("select min(t) from t");
        c1();

        expRS = new String [][]{{"12:30:30"}};
        ok(NULLS_ELIMINATED);


        q("select min(ts) from t");
        c1();

        expRS = new String [][]{{"1992-01-01 12:30:30.0"}};
        ok(NULLS_ELIMINATED);


        q("select min(b) from t");
        c1();

        expRS = new String [][]{{"12af"}};
        ok(NULLS_ELIMINATED);


        q("select min(bv) from t");
        c1();

        expRS = new String [][]{{"0000111100001111"}};
        ok(NULLS_ELIMINATED);


        q("select min(dc) from t");
        c1();

        expRS = new String [][]{{"111.11"}};
        ok(NULLS_ELIMINATED);


        q("select min(i) from t group by i");
        c1();

        expRS = new String [][]
        {
            {"0"},
            {"1"},
            {null}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select min(s) from t group by s");
        c1();

        expRS = new String [][]
        {
            {"100"},
            {"200"},
            {null}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select min(l) from t group by l");
        c1();

        expRS = new String [][]
        {
            {"1000000"},
            {"2000000"},
            {null}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select min(c) from t group by c");
        c1();

        expRS = new String [][]
        {
            {"duplicate"},
            {"goodbye"},
            {null}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select min(v) from t group by v");
        c1();

        expRS = new String [][]
        {
            {"noone is here"},
            {"this is duplicated"},
            {null}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select min(d) from t group by d");
        c1();

        expRS = new String [][]
        {
            {"100.0"},
            {"200.0"},
            {null}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select min(r) from t group by r");
        c1();

        expRS = new String [][]
        {
            {"100.0"},
            {"200.0"},
            {null}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select min(dt) from t group by dt");
        c1();

        expRS = new String [][]
        {
            {"1992-01-01"},
            {"1992-09-09"},
            {null}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select min(t) from t group by t");
        c1();

        expRS = new String [][]
        {
            {"12:30:30"},
            {"12:55:55"},
            {null}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select min(ts) from t group by ts");
        c1();

        expRS = new String [][]
        {
            {"1992-01-01 12:30:30.0"},
            {"1992-01-01 12:55:55.0"},
            {null}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select min(b) from t group by b");
        c1();

        expRS = new String [][]
        {
            {"12af"},
            {"ffff"},
            {null}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select min(bv) from t group by bv");
        c1();

        expRS = new String [][]
        {
            {"0000111100001111"},
            {"1111111111111111"},
            {null}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        q("select min(dc) from t group by dc");
        c1();

        expRS = new String [][]
        {
            {"111.11"},
            {"222.22"},
            {null}
        };

        ok(new String[]{null, null, NULLS_ELIMINATED});


        // constants

        q("select min(1) from t");
        c1();

        expRS = new String [][]{{"1"}};
        ok();

        q("select min('hello') from t");
        c1();

        expRS = new String [][]{{"hello"}};
        ok();

        q("select min(1.1) from t");
        c1();

        expRS = new String [][]{{"1.1"}};
        ok();

        q("select min(1e1) from t");
        c1();

        expRS = new String [][]{{"10.0"}};
        ok();

        q("select min(X'11') from t");
        c1();

        expRS = new String [][]{{"11"}};
        ok();

        q("select min(date('1999-06-06')) from t");
        c1();

        expRS = new String [][]{{"1999-06-06"}};
        ok();

        q("select min(time('12:30:30')) from t");
        c1();

        expRS = new String [][]{{"12:30:30"}};
        ok();

        q("select min(timestamp('1999-06-06 12:30:30')) from t");
        c1();

        expRS = new String [][]{{"1999-06-06 12:30:30.0"}};
        ok();

        q("select min(1) from t group by i");
        c1();

        expRS = new String [][]
        {
            {"1"},
            {"1"},
            {"1"}
        };

        ok();

        q("select min('hello') from t group by c");
        c1();

        expRS = new String [][]
        {
            {"hello"},
            {"hello"},
            {"hello"}
        };

        ok();

        q("select min(1.1) from t group by dc");
        c1();

        expRS = new String [][]
        {
            {"1.1"},
            {"1.1"},
            {"1.1"}
        };

        ok();

        q("select min(1e1) from t group by d");
        c1();

        expRS = new String [][]
        {
            {"10.0"},
            {"10.0"},
            {"10.0"}
        };

        ok();

        q("select min(X'11') from t group by b");
        c1();

        expRS = new String [][]
        {
            {"11"},
            {"11"},
            {"11"}
        };

        ok();

        q("select min(date('1999-06-06')) from t group by dt");
        c1();

        expRS = new String [][]
        {
            {"1999-06-06"},
            {"1999-06-06"},
            {"1999-06-06"}
        };

        ok();

        q("select min(time('12:30:30')) from t group by t");
        c1();

        expRS = new String [][]
        {
            {"12:30:30"},
            {"12:30:30"},
            {"12:30:30"}
        };

        ok();

        q("select min(timestamp('1999-06-06 12:30:30')) from t "
            + "group by ts");
        c1();

        expRS = new String [][]
        {
            {"1999-06-06 12:30:30.0"},
            {"1999-06-06 12:30:30.0"},
            {"1999-06-06 12:30:30.0"}
        };

        ok();

        // multicolumn grouping

        q("select min(i), min(dt), min(b) from t group by i, dt, b");

        expColNames = new String [] {"1", "2", "3"};
        JDBC.assertColumnNames(rs, expColNames);

        expRS = new String [][]
        {
            {"0", "1992-01-01", "12af"},
            {"0", "1992-01-01", "ffff"},
            {"0", "1992-09-09", "12af"},
            {"1", "1992-01-01", "12af"},
            {null, null, null}
        };

        ok(new String[]{null, null, null, null, NULLS_ELIMINATED});


        q("select l, dt, min(i), min(dt), min(b), i from t "
            + "group by i, dt, b, l");

        expColNames = new String [] {"L", "DT", "3", "4", "5", "I"};
        JDBC.assertColumnNames(rs, expColNames);

        expRS = new String [][]
        {
            {"1000000", "1992-01-01", "0", "1992-01-01", "12af", "0"},
            {"2000000", "1992-01-01", "0", "1992-01-01", "12af", "0"},
            {"1000000", "1992-01-01", "0", "1992-01-01", "ffff", "0"},
            {"1000000", "1992-09-09", "0", "1992-09-09", "12af", "0"},
            {"1000000", "1992-01-01", "1", "1992-01-01", "12af", "1"},
            {null, null, null, null, null, null}
        };

        ok(new String[]{null, null, null, null, null, NULLS_ELIMINATED});


        // group by expression

        q("select min(expr1), min(expr2)"
            + "from (select i * s, c || v from t) t (expr1, expr2) "
            + "group by expr2, expr1");
        c2();

        expRS = new String [][]
        {
            {"0", "duplicate noone is here"},
            {"0", "duplicate this is duplicated"},
            {"100", "duplicate this is duplicated"},
            {"0", "goodbye   this is duplicated"},
            {null, null}
        };

        ok(new String[]{null, null, null, null, NULLS_ELIMINATED});


        // distinct and group by

        q("select distinct min(i) from t group by i, dt");
        c1();

        expRS = new String [][]
        {
            {"0"},
            {"1"},
            {null}
        };

        ok(new String[]{NULLS_ELIMINATED, NULLS_ELIMINATED, NULLS_ELIMINATED});



        // insert select

        x("create table tmp (x int, y char(20))");

        x("insert into tmp (x, y) select min(i), min(c) from t");

        if (usingEmbedded())
        {
            if (sqlWarn == null)
                sqlWarn = st.getWarnings();
            if (sqlWarn == null)
                sqlWarn = getConnection().getWarnings();
            assertNotNull("Expected warning but found none", sqlWarn);
            assertSQLState(NULLS_ELIMINATED, sqlWarn);
            sqlWarn = null;
        }

        q("select * from tmp");

        expColNames = new String [] {"X", "Y"};
        JDBC.assertColumnNames(rs, expColNames);

        expRS = new String [][]{{"0", "duplicate"}};
        ok();

        x("insert into tmp (x, y) select min(i), min(c) from t "
            + "group by b");

        if (usingEmbedded())
        {
            if (sqlWarn == null)
                sqlWarn = st.getWarnings();
            if (sqlWarn == null)
                sqlWarn = getConnection().getWarnings();
            assertNotNull("Expected warning but found none", sqlWarn);
            assertSQLState(NULLS_ELIMINATED, sqlWarn);
            // sqlWarn = null; comment out to silence IDE "value not used"
        }

        q("select * from tmp");

        expColNames = new String [] {"X", "Y"};
        JDBC.assertColumnNames(rs, expColNames);

        expRS = new String [][]
        {
            {"0", "duplicate"},
            {"0", "duplicate"},
            {"0", "duplicate"},
            {null, null}
        };

        ok();

        // drop tables
        x("drop table tmp");
        x("drop table t");
        x("drop table empty");
    }

    private void x(String stmt) throws SQLException {

        st.executeUpdate(stmt);
    }

    private void q(String query) throws SQLException {
        rs = st.executeQuery(query);
    }

    private void c1() throws SQLException {
        expColNames = new String [] {"1"};
        JDBC.assertColumnNames(rs, expColNames);
    }

    private void c2() throws SQLException {
        expColNames = new String [] {"1", "2"};
        JDBC.assertColumnNames(rs, expColNames);
    }

    private void e(String expectedState, String stmt) {
        assertStatementError(expectedState, st, stmt);
    }

    private void ok() throws SQLException {
        JDBC.assertFullResultSet(rs, expRS, true);
    }

    private void ok(String[] warnings) throws SQLException {
        JDBC.assertFullResultSet(rs, expRS, warnings);
    }

    private void ok(String warning) throws SQLException {
        ok(new String[]{warning});
    }
}
