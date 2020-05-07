package demo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Seyed Sahil
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {

    Strategy strategy() default Strategy.SINGLETON;

}
