package org.lucidTalents;

import org.lucidTalents.ReaperBot;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeoutException;



public class Main {
    public static void main(String[] args) {
        ReaperBot bot = new ReaperBot();
        try {
            bot.client.login().block(Duration.of(5, ChronoUnit.MINUTES));
        }
        catch(Exception e){
            System.out.println(e);
        }
        System.exit(0);
    }
}
