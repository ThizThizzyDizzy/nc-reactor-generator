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
public class RandomLayerMutator extends Mutator<LiteOverhaulDistiller>{
    public SettingIndicies indicies = new SettingIndicies("Layers");
    public RandomLayerMutator(){super("nuclearcraft:overhaul_distiller:random_layer");}
    @Override
    public String getTitle(){return "Random Layer Mutator";}
    @Override
    public String getTooltip(){return "Changes a complete interior layer; sieve trays receive a sieve assembly layer directly above them";}
    @Override
    public void run(LiteOverhaulDistiller d, Random rand){
        ArrayList<Integer> allowed = new ArrayList<>(), sieves = new ArrayList<>();
        for(int index : indicies.get()){
            int b = index-1;
            if(b<0||d.configuration.blockSieve[b]||d.configuration.blockTray[b])allowed.add(b);
            if(b>=0&&d.configuration.blockSieve[b])sieves.add(b);
        }
        if(allowed.isEmpty())return;
        int b = allowed.get(rand.nextInt(allowed.size()));
        if(b>=0&&d.configuration.blockTray[b]){
            if(d.dims[1]<2||sieves.isEmpty())return;
            int y = 1+rand.nextInt(d.dims[1]-1);
            d.setLayer(y, b);
            d.setLayer(y+1, sieves.get(rand.nextInt(sieves.size())));
        }else d.setLayer(1+rand.nextInt(d.dims[1]), b);
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
    public void setIndicies(LiteOverhaulDistiller d){indicies.init(d.configuration.blockDisplayName, d.configuration.blockDisplayTexture, "Air");}
    @Override
    public void init(LiteOverhaulDistiller d){}
    @Override
    public void importFrom(LiteOverhaulDistiller d, NCPFConfigurationContainer container){
        setIndicies(d);
        ArrayList<Integer> allowed = new ArrayList<>();
        allowed.add(0);
        for(int i = 0; i<d.configuration.blockDefinition.length; i++)if(d.configuration.blockTray[i]||d.configuration.blockSieve[i])allowed.add(i+1);
        indicies.set(allowed.stream().mapToInt(Integer::intValue).toArray());
    }
}
