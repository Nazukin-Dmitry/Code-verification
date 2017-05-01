package server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by 1 on 01.05.2017.
 */
public class RMIServer {
    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("rmiServerAppContext.xml");
        new ClassPathXmlApplicationContext("rmiServerAppContext2.xml");
    }
}
