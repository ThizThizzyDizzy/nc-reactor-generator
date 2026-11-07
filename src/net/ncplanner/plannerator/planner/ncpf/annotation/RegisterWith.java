package net.ncplanner.plannerator.planner.ncpf.annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import net.ncplanner.plannerator.planner.module.Module;
@Retention(RetentionPolicy.RUNTIME)
public @interface RegisterWith{
    Class<? extends Module> module();
}
