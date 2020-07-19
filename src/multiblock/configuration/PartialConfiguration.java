package multiblock.configuration;
import java.util.ArrayList;
import multiblock.Multiblock;
public class PartialConfiguration extends Configuration{
    public static PartialConfiguration generate(Configuration configuration, ArrayList<Multiblock> multiblocks){
        PartialConfiguration partial = new PartialConfiguration(configuration.name, configuration.version);
        configuration.applyPartial(partial, multiblocks);
        return partial;
    }
    public PartialConfiguration(String name, String version){
        super(name, version);
    }
    @Override
    public boolean isPartial(){
        return true;
    }
}