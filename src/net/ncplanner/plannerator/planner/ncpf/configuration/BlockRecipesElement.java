package net.ncplanner.plannerator.planner.ncpf.configuration;
import java.util.List;
import net.ncplanner.plannerator.ncpf.NCPFElement;
public interface BlockRecipesElement{
    public List<? extends NCPFElement> getBlockRecipes();
    public abstract void clearBlockRecipes();
}