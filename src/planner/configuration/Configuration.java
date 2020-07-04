package planner.configuration;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;
import static planner.Core.getInputStream;
import planner.configuration.underhaul.UnderhaulConfiguration;
import planner.configuration.overhaul.OverhaulConfiguration;
import planner.file.FileReader;
import multiblock.Multiblock;
import simplelibrary.config2.Config;
public class Configuration{
    public String name;
    public String version;
    public static ArrayList<Configuration> configurations = new ArrayList<>();
    static{
        configurations.add(FileReader.read(() -> {
            return getInputStream("configurations/nuclearcraft.ncpf");
        }).configuration);
        configurations.add(FileReader.read(() -> {
            return getInputStream("configurations/po3.ncpf");
        }).configuration);
        configurations.add(FileReader.read(() -> {
            return getInputStream("configurations/e2e.ncpf");
        }).configuration);
    }
    public Configuration(String name, String version){
        this.name = name;
        this.version = version;
    }
    public UnderhaulConfiguration underhaul;
    public OverhaulConfiguration overhaul;
    public void save(OutputStream stream){
        Config config = Config.newConfig();
        config.set("partial", isPartial());
        if(underhaul!=null)config.set("underhaul", underhaul.save(isPartial()));
        if(overhaul!=null)config.set("overhaul", overhaul.save(isPartial()));
        if(name!=null)config.set("name", name);
        if(version!=null)config.set("version", version);
        config.save(stream);
    }
    public boolean isPartial(){
        return false;
    }
    public void impose(Configuration configuration){
        if(Objects.equals(configuration.name, name)){
            if(underhaul!=null)configuration.underhaul = underhaul;
            if(overhaul!=null)configuration.overhaul = overhaul;
        }else{
            configuration.underhaul = underhaul;
            configuration.overhaul = overhaul;
        }
        configuration.name = name;
        configuration.version = version;
    }
    public void applyPartial(PartialConfiguration partial, ArrayList<Multiblock> multiblocks){
        if(underhaul!=null){
            partial.underhaul = new UnderhaulConfiguration();
            underhaul.applyPartial(partial.underhaul, multiblocks);
        }
        if(overhaul!=null){
            partial.overhaul = new OverhaulConfiguration();
            overhaul.applyPartial(partial.overhaul, multiblocks);
        }
    }
}