package com.theironyard;

import com.github.mustachejava.Mustache;
import org.h2.tools.Server;
import spark.ModelAndView;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("DROP TABLE IF EXISTS people");
        stmt.execute("CREATE TABLE people (id IDENTITY, first_name VARCHAR, last_name VARCHAR, email VARCHAR, country VARCHAR, ip VARCHAR)");


    }

    public static void insertPerson(Connection conn, String firstName, String lastName, String email, String country, String ip) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO people VALUES (NULL, ?, ?, ?, ?, ?)");
        stmt.setString(1, firstName);
        stmt.setString(2, lastName);
        stmt.setString(3, email);
        stmt.setString(4, country);
        stmt.setString(5, ip);
        stmt.execute();

    }

    public static Person selectPerson(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM people WHERE id = ?");
        stmt.setInt(1, id);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            String firstName = results.getString("first_name");
            String lastName = results.getString("last_name");
            String email = results.getString("email");
            String country = results.getString("country");
            String ip = results.getString("ip");
            Person p = new Person(id, firstName, lastName, email, country, ip);
            return p;
        }
        return null;

    }

    public static void populateDatabase (Connection conn) throws SQLException, FileNotFoundException {
        File f = new File("people.csv");
        Scanner fileScanner = new Scanner(f);
        fileScanner.nextLine();
        while (fileScanner.hasNext()) {
            String line = fileScanner.nextLine();
            String[] columns = line.split(",");
            Person p = new Person(Integer.valueOf(columns[0]), columns[1], columns[2], columns[3], columns[4], columns[5]);
            insertPerson(conn, p.firstName, p.lastName, p.email, p.country, p.ipAddress);

        }

    }

    public static ArrayList<Person> selectPeople (Connection conn) throws SQLException{
        ArrayList<Person> people = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM people");
        ResultSet results = stmt.executeQuery();
        while (results.next()) {
            int id = results.getInt("id");
            String firstName = results.getString("firstName");
            String lastName = results.getString("lastName");
            String email = results.getString("email");
            String country = results.getString("country");
            String ipAddress = results.getString("ipAddress");
            people.add(new Person(id, firstName, lastName, email, country, ipAddress));
        }
        return people;

    }

    public static void main(String[] args) throws FileNotFoundException, SQLException {
        Server.createWebServer().start();
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn);
        populateDatabase(conn);
        ArrayList<Person> people = new ArrayList<>();
        Spark.init();
        Spark.get(
                "/",
                (request, response) -> {
                    HashMap m = new HashMap<>();
                    String idStr = request.queryParams("offset");
                    int offset = 0;
                    if (idStr != null) {
                        offset = Integer.valueOf(idStr);
                    }


                    ArrayList<Person> namesArr = new ArrayList<Person>(people.subList(offset, offset + 20));

                    m.put("people", namesArr);
                    m.put("previous", offset - 20);
                    m.put("next", offset + 20);


                    return new ModelAndView(m, "index.html");


                },
                new MustacheTemplateEngine()

        );
        Spark.get(
                "/person",
                (request, response) -> {
                     int  id = Integer.valueOf(request.queryParams("id"));

                    Person p = people.get(id - 1);

                    return new ModelAndView(p, "person.html");

                },
                new MustacheTemplateEngine()


        );
    }
}