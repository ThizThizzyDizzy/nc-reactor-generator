package net.ncplanner.plannerator.planner.ncpf.module;
import net.ncplanner.plannerator.ncpf.ConglomerationError;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
public class NuclearCraftGeneratedModule extends NCPFModule{
    public String ncVersion;
    public NuclearCraftGeneratedModule(){
        super("nuclearcraft:generated");
    }
    @Override
    public void conglomerate(NCPFModule addon){
        throw new ConglomerationError("Cannot conglomerate NuclearCraft generated module! (What did you *do*?)");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        ncVersion = ncpf.getString("nuclearcraft_version");
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setString("nuclearcraft_version", ncVersion);
    }
}
