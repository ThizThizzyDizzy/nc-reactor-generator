package net.ncplanner.plannerator.multiblock.generator.lite.overhaulDistiller.mutators;
import java.util.Random;
import net.ncplanner.plannerator.multiblock.generator.lite.mutator.Mutator;
import net.ncplanner.plannerator.multiblock.generator.lite.overhaulDistiller.LiteOverhaulDistiller;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.Setting;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingIndicies;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = OverhaulModule.class)
public class RandomRecipeMutator extends Mutator<LiteOverhaulDistiller>{
    public SettingIndicies indicies = new SettingIndicies("Recipes");
    public RandomRecipeMutator(){
        super("nuclearcraft:overhaul_distiller:random_recipe");
    }
    @Override
    public String getTitle(){
        return "Random Recipe Mutator";
    }
    @Override
    public String getTooltip(){
        return "Changes the distiller's recipe to a random recipe from the list of allowed recipes";
    }
    @Override
    public void run(LiteOverhaulDistiller multiblock, Random rand){
        if(indicies.get().length==0)return;
        multiblock.recipe = indicies.get()[rand.nextInt(indicies.get().length)];
    }
    @Override
    public int getSettingCount(){
        return 1;
    }
    @Override
    public Setting getSetting(int i){
        return indicies;
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        indicies.set(ncpf.getIntArray("indicies"));
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setIntArray("indicies", indicies.get());
    }
    @Override
    public void setIndicies(LiteOverhaulDistiller multiblock){
        indicies.init(multiblock.configuration.recipeDisplayName, multiblock.configuration.recipeDisplayTexture);
    }
    @Override
    public void init(LiteOverhaulDistiller multiblock){
    }
}
