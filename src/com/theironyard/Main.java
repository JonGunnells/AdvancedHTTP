package com.theironyard;

import com.github.mustachejava.Mustache;
import spark.ModelAndView;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        String fileName = "people.csv";
        ArrayList<Person> people = new ArrayList<>();
        File f = new File(fileName);
        Scanner fileScanner = new Scanner(f);
        fileScanner.nextLine();
        while (fileScanner.hasNext()) {
            String line = fileScanner.nextLine();
            String[] columns = line.split(",");
            Person p = new Person(Integer.valueOf(columns[0]), columns[1], columns[2], columns[3], columns[4], columns[5]);
            people.add(p);

        }
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

                    Person per = people.get(id - 1);

                    return new ModelAndView(per, "person.html");

                },
                new MustacheTemplateEngine()


        );
    }
}