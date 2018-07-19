package com.redhat.middleware.keynote;

import java.util.Scanner;

public class GracefullyExit extends Thread {
    public GracefullyExit() {

    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        String exit = scanner.next();
        if ("1".equals(exit)) {
            System.out.println("==========>>>>>>>>>>>>>>>>> my game-server application stop !");
            scanner.close();
            System.exit(0);
        }

    }
}
