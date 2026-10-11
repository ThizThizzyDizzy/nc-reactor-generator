package net.ncplanner.plannerator.multiblock.generator.lite.overhaulDistiller.mutators;
import java.util.ArrayList;
import java.util.Random;
import net.ncplanner.plannerator.multiblock.generator.lite.mutator.Mutator;
import net.ncplanner.plannerator.multiblock.generator.lite.overhaulDistiller.LiteOverhaulDistiller;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.Setting;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingIndicies;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = OverhaulModule.class)
public class RandomCasingMutator extends Mutator<LiteOverhaulDistiller>{
    public SettingIndicies indicies = new SettingIndicies("Casing Units");
    public RandomCasingMutator(){super("nuclearcraft:overhaul_distiller:random_casing");}
    @Override
    public String getTitle(){return "Random Casing Unit Mutator";}
    @Override
    public String getTooltip(){return "Changes a top or bottom casing unit while preserving controllers and ports";}
    @Override
    public void run(LiteOverhaulDistiller d, Random rand){
        int x = 1+rand.nextInt(d.dims[0]), z = 1+rand.nextInt(d.dims[2]);
        boolean top = rand.nextBoolean();
        int y = top?d.dims[1]+1:0;
        int old = d.blocks[x][y][z];
        if(old>=0&&!d.configuration.blockPlainCasing[old]&&!d.configuration.blockReflux[old]
                &&!d.configuration.blockReboiling[old]&&!d.configuration.blockDistributor[old])return;
        ArrayList<Integer> allowed = new ArrayList<>();
        for(int b : indicies.get()){
            if(d.configuration.blockPlainCasing[b]||(top?(d.configuration.blockReflux[b]||d.configuration.blockDistributor[b]):d.configuration.blockReboiling[b]))allowed.add(b);
        }
        if(!allowed.isEmpty())d.blocks[x][y][z] = allowed.get(rand.nextInt(allowed.size()));
    }
    @Override
    public int getSettingCount(){return 1;}
    @Override
    public Setting getSetting(int i){return indicies;}
    @Override
    public void convertFromObject(NCPFObject ncpf){indicies.set(ncpf.getIntArray("indicies"));}
    @Override
    public void convertToObject(NCPFObject ncpf){ncpf.setIntArray("indicies", indicies.get());}
    @Override
    public void setIndicies(LiteOverhaulDistiller d){indicies.init(d.configuration.blockDisplayName, d.configuration.blockDisplayTexture);}
    @Override
    public void init(LiteOverhaulDistiller d){}
    @Override
    public void importFrom(LiteOverhaulDistiller d, NCPFConfigurationContainer container){
        setIndicies(d);
        ArrayList<Integer> allowed = new ArrayList<>();
        for(int i = 0; i<d.configuration.blockDefinition.length; i++)if(d.configuration.blockPlainCasing[i]
                ||d.configuration.blockReflux[i]||d.configuration.blockReboiling[i]||d.configuration.blockDistributor[i])allowed.add(i);
        indicies.set(allowed.stream().mapToInt(Integer::intValue).toArray());
    }
}
