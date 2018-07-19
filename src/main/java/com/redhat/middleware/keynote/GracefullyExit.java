package com.redhat.middleware.keynote;

import io.vertx.core.Vertx;

import java.util.Scanner;

public class GracefullyExit extends Thread {
    private Vertx vertx;

    public GracefullyExit() {

    }

    public GracefullyExit(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        String exit = scanner.next();
        if ("1".equals(exit))
            vertx.close();
        scanner.close();
        System.out.println("==========>>>>>>>>>>>>>>>>> my game-server application stop !");
        System.exit(0);
    }
}
