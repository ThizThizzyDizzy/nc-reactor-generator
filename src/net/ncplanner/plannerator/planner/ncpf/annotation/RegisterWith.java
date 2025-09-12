package net.ncplanner.plannerator.planner.ncpf.annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@Retention(RetentionPolicy.RUNTIME)
public @interface RegisterWith{
    Class<? extends net.ncplanner.plannerator.planner.module.Module> module();
}
