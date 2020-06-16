package planner.configuration;
import planner.configuration.underhaul.UnderhaulConfiguration;
import planner.configuration.overhaul.OverhaulConfiguration;
public class Configuration{
    public String name;
    public String version;
    public Configuration(String name, String version){
        this.name = name;
        this.version = version;
    }
    public UnderhaulConfiguration underhaul = new UnderhaulConfiguration();
    public OverhaulConfiguration overhaul = new OverhaulConfiguration();
}