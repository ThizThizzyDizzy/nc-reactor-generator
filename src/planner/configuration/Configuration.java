package planner.configuration;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Objects;
import planner.Core;
import planner.configuration.underhaul.UnderhaulConfiguration;
import planner.configuration.overhaul.OverhaulConfiguration;
import simplelibrary.config2.Config;
public class Configuration{
    public String name;
    public String version;
    public static ArrayList<Configuration> configurations = new ArrayList<>();
    static{
        Configuration nuclear = new Configuration("nah", "nope");
        configurations.add(nuclear);
//        Configuration e2e = new Configuration("E2E", version);
//        configurations.add(e2e);
//        Configuration po3 = new Configuration("PO3", version);
//        configurations.add(po3);
        Core.configuration = nuclear;
    }
    public Configuration(String name, String version){
        this.name = name;
        this.version = version;
    }
    public UnderhaulConfiguration underhaul;
    public OverhaulConfiguration overhaul;
    public void save(FileOutputStream stream){
        Config config = Config.newConfig();
        config.set("partial", isPartial());
        if(underhaul!=null)config.set("underhaul", underhaul.save());
        if(overhaul!=null)config.set("overhaul", overhaul.save());
        config.save(stream);
    }
    protected boolean isPartial(){
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
}