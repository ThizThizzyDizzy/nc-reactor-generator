package net.ncplanner.plannerator.multiblock.generator.lite.underhaul.fissionsfr.mutators.random;
import java.util.Random;
import net.ncplanner.plannerator.multiblock.generator.lite.mutator.Mutator;
import net.ncplanner.plannerator.multiblock.generator.lite.underhaul.fissionsfr.LiteUnderhaulSFR;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.Setting;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingIndicies;
public class RandomFuelMutator implements Mutator<LiteUnderhaulSFR>{
    public SettingIndicies indicies;
    public RandomFuelMutator(LiteUnderhaulSFR multiblock){
        indicies = new SettingIndicies("Fuels", multiblock.configuration.fuelDisplayName, multiblock.configuration.fuelDisplayTexture);
    }
    @Override
    public String getTitle(){
        return "Random Fuel Mutator";
    }
    @Override
    public String getTooltip(){
        return "Changes the reactor's fuel to a random fuel from the list of allowed fuels";
    }
    @Override
    public void run(LiteUnderhaulSFR multiblock, Random rand){
        multiblock.fuel = indicies.get()[rand.nextInt(indicies.get().length)];
    }
    @Override
    public int getSettingCount(){
        return 1;
    }
    @Override
    public Setting getSetting(int i){
        return indicies;
    }
}