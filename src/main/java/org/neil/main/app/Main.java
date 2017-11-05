package org.neil.main.app;

public class Main {

    // TODO add argument for timeout
    public static void main(String... args) {

        if (executeUrlTester(args)) {
            System.exit(0);
        } else {
            System.exit(1);
        }
    }

    static boolean executeUrlTester(String[] args) {

        return new UrlTesterApplication().testUrls(new SimpleArgumentProcessor().process(args));
    }

}
