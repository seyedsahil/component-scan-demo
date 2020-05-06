package demo;

import demo.test1.Test1;
import demo.test1.Test2;
import demo.test2.Test4;

public class Main {

    public static void main(String[] args) throws IllegalAccessException, InstantiationException {
        ComponentManager componentManager = ComponentManager.getInstance(Main.class.getPackage().getName(), Thread.currentThread().getContextClassLoader(), true);
        Test1 test1 = componentManager.getComponent(Test1.class.getName());
        test1.onStartup();
        Test4 test4 = componentManager.getService(Test4.class.getName());
        test4.onStartup();
    }

}
