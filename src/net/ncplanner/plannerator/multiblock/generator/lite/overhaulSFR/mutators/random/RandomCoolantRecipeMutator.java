package net.ncplanner.plannerator.multiblock.generator.lite.overhaulSFR.mutators.random;
import java.util.Random;
import net.ncplanner.plannerator.multiblock.generator.lite.mutator.Mutator;
import net.ncplanner.plannerator.multiblock.generator.lite.overhaulSFR.LiteOverhaulSFR;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.Setting;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingIndicies;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class RandomCoolantRecipeMutator extends Mutator<LiteOverhaulSFR>{
    public SettingIndicies indicies = new SettingIndicies("Coolant Recipes");
    public RandomCoolantRecipeMutator(){
        super("nuclearcraft:overhaul_sfr:random_coolant_recipe");
    }
    @Override
    public String getTitle(){
        return "Random Coolant Recipe Mutator";
    }
    @Override
    public String getTooltip(){
        return "Changes the reactor's coolant recipe to a random one from the list of allowed recipes";
    }
    @Override
    public void run(LiteOverhaulSFR multiblock, Random rand){
        multiblock.coolantRecipe = indicies.get()[rand.nextInt(indicies.get().length)];
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
    public void setIndicies(LiteOverhaulSFR multiblock){
        indicies.init(multiblock.configuration.coolantRecipeDisplayName, multiblock.configuration.coolantRecipeDisplayTexture);
    }
    @Override
    public void init(LiteOverhaulSFR multiblock){}
}