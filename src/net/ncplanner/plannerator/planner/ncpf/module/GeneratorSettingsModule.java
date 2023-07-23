package net.ncplanner.plannerator.planner.ncpf.module;
import net.ncplanner.plannerator.multiblock.generator.lite.LiteGenerator;
import net.ncplanner.plannerator.multiblock.generator.lite.LiteMultiblock;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
public class GeneratorSettingsModule<T extends LiteMultiblock> extends NCPFModule{
    public LiteGenerator<T> generator;
    public GeneratorSettingsModule(){
        super("plannerator:generator_settings");
    }
    @Override
    public void conglomerate(NCPFModule addon){
        throw new UnsupportedOperationException("Generator settings can not be combined!");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        generator = ncpf.getDefinedNCPFObject("settings", LiteGenerator::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setDefinedNCPFObject("settings", generator);
    }
}