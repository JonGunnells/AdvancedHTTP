package com.theironyard;

import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static junit.framework.TestCase.assertTrue;


public class MainTest {

    public Connection startConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:test");
        Main.createTables(conn);
        return conn;

    }
    @Test
    public void testPerson() throws SQLException {
        Connection conn = startConnection();
        Main.insertPerson(conn, " ", "firstName", "lastName", "email", "country");
        Person p = Main.selectPerson(conn, 1);
        conn.close();
        assertTrue(p != null);


    }

    @Test
    public void testPeople() throws SQLException {
        Connection conn = startConnection();
        Main.selectPeople(conn);
        conn.close();
        assertTrue();

    }


}