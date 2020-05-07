package demo.test1;

import demo.Component;
import demo.Strategy;

@Component(strategy = Strategy.SINGLETON)
public class Test1 {

    public void onStartup() {
        System.out.println("Component initialized...");
    }

}
