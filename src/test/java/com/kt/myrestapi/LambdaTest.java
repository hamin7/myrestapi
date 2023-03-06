package com.kt.myrestapi;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

public class LambdaTest {

    @Test
    public void consumer() {
        List<String> list = List.of("aa", "bb", "cc");//Immutable List

    }

    @Test @Disabled
    public void runnable() {
        //1. Anonymous Inner class
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Anonymous Inner class");
            }
        });
        t1.start();
        //2. Lambda Expression
        Thread t2 = new Thread(() -> System.out.println("Lambda Expression"));
        t2.start();
    }


}
