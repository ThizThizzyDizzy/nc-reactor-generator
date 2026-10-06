package net.ncplanner.plannerator.planner.ncpf.module;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.NCPFElement;
@Deprecated
public interface RecipesBlockModule{
    public Supplier<NCPFElement> getRecipeElement();
}