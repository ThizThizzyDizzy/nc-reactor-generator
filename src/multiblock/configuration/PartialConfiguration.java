package multiblock.configuration;
import java.util.ArrayList;
import multiblock.Multiblock;
public class PartialConfiguration extends Configuration{
    public static PartialConfiguration generate(Configuration configuration, ArrayList<Multiblock> multiblocks){
        PartialConfiguration partial = new PartialConfiguration(configuration.name, configuration.overhaulVersion, configuration.underhaulVersion);
        configuration.apply(partial, multiblocks, partial);
        return partial;
    }
    public PartialConfiguration(String name, String version, String underhaulVersion){
        super(name, version, underhaulVersion);
    }
    @Override
    public boolean isPartial(){
        return true;
    }
}