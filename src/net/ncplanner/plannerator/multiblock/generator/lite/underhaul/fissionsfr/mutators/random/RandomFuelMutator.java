package net.ncplanner.plannerator.multiblock.generator.lite.underhaul.fissionsfr.mutators.random;
import java.util.Random;
import net.ncplanner.plannerator.multiblock.generator.lite.mutator.Mutator;
import net.ncplanner.plannerator.multiblock.generator.lite.underhaul.fissionsfr.LiteUnderhaulSFR;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.Setting;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingIndicies;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class RandomFuelMutator extends Mutator<LiteUnderhaulSFR>{
    public SettingIndicies indicies = new SettingIndicies("Fuels");
    public RandomFuelMutator(){
        super("nuclearcraft:underhaul_sfr:random_fuel");
    }
    public RandomFuelMutator(LiteUnderhaulSFR multiblock){
        this();
        setIndicies(multiblock);
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
    @Override
    public void convertFromObject(NCPFObject ncpf){
        indicies.set(ncpf.getIntArray("indicies"));
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setIntArray("indicies", indicies.get());
    }
    @Override
    public void setIndicies(LiteUnderhaulSFR multiblock){
        indicies.init(multiblock.configuration.fuelDisplayName, multiblock.configuration.fuelDisplayTexture);
    }
}