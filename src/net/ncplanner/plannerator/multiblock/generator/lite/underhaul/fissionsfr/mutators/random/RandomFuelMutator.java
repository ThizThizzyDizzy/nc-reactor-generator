package net.ncplanner.plannerator.multiblock.generator.lite.underhaul.fissionsfr.mutators.random;
import java.util.Random;
import net.ncplanner.plannerator.multiblock.generator.lite.mutator.Mutator;
import net.ncplanner.plannerator.multiblock.generator.lite.underhaul.fissionsfr.LiteUnderhaulSFR;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingIndicies;
public class RandomFuelMutator implements Mutator<LiteUnderhaulSFR>{
    public SettingIndicies indicies;
    public RandomFuelMutator(LiteUnderhaulSFR multiblock){
        indicies = new SettingIndicies("Fuels", multiblock.configuration.fuelName);
    }
    @Override
    public void run(LiteUnderhaulSFR multiblock, Random rand){
        multiblock.fuel = indicies.get()[rand.nextInt(indicies.get().length)];
    }
}