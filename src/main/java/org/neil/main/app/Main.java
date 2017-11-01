package org.neil.main.app;

import java.util.Arrays;

public class Main {

    public static void main(String... args) {

        System.out.println("Hello World!!!");
        Arrays.stream(args).forEach(System.out::println);
    }

}
