package demo;

import demo.test1.Test1;
import demo.test1.Test2;
import demo.test2.Test3;
import demo.test2.Test4;

public class Main {

    public static void main(String[] args) {
        ComponentManager componentManager = ComponentManager.getInstance(Main.class.getPackage().getName(), Thread.currentThread().getContextClassLoader(), true);
        // Test1 is a Component with strategy SINGLETON
        Test1 test1 = componentManager.getInstance(Test1.class);
        // Test2 is a regular class
        Test2 test2 = componentManager.getInstance(Test2.class);
        // Test3 is another Component with strategy PROTOTYPE
        Test3 test3 = componentManager.getInstance(Test3.class);
        // Test4 is a Service with strategy SINGLETON
        Test4 test4 = componentManager.getInstance(Test4.class);

        Test1 _test1 = componentManager.getInstance(Test1.class);
        Test2 _test2 = componentManager.getInstance(Test2.class);
        Test3 _test3 = componentManager.getInstance(Test3.class);
        Test4 _test4 = componentManager.getInstance(Test4.class);
    }

}
