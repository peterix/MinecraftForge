package net.minecraftforge.common.capabilities;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD}) //Maybe Class to allow for default registration?
public @interface CapabilityInject
{
    Class<?> value();

    String className() default "";
}
