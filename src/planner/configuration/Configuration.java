package planner.configuration;
import java.io.FileOutputStream;
import planner.configuration.underhaul.UnderhaulConfiguration;
import planner.configuration.overhaul.OverhaulConfiguration;
import simplelibrary.config2.Config;
public class Configuration{
    public String name;
    public String version;
    public Configuration(String name, String version){
        this.name = name;
        this.version = version;
    }
    public UnderhaulConfiguration underhaul = new UnderhaulConfiguration();
    public OverhaulConfiguration overhaul = new OverhaulConfiguration();
    public void save(FileOutputStream stream){
        Config config = Config.newConfig();
        if(underhaul!=null)config.set("underhaul", underhaul.save());
        if(overhaul!=null)config.set("overhaul", overhaul.save());
        config.save(stream);
    }
}