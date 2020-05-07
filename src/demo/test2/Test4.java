package demo.test2;

import demo.Service;
import demo.Strategy;

@Service(strategy = Strategy.SINGLETON)
public class Test4 {

    public void onStartup() {
        System.out.println("Service started...");
    }

}
