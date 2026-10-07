package net.ncplanner.plannerator.planner.configuration;
import com.thizthizzydizzy.dizzyengine.ResourceManager;
import java.util.ArrayList;
import java.util.HashMap;
import net.ncplanner.ncpf.io.NcpfJsonConverter;
import net.ncplanner.ncpf.structure.NcpfAddon;
public class ConfigurationManager{
    public static final ArrayList<CannedConfiguration> configurations = new ArrayList<>();
    public static final ArrayList<CannedConfiguration> internalConfigurations = new ArrayList<>();
    public static final HashMap<CannedConfiguration, String> internalConfigurationLinks = new HashMap<>();
    public static final HashMap<CannedConfiguration, String> internalConfigurationAuthors = new HashMap<>();
    public static final ArrayList<NcpfAddon> internalAddons = new ArrayList<>();
    public static final HashMap<NcpfAddon, String> internalAddonLinks = new HashMap<>();
    public static final HashMap<NcpfAddon, String> internalAddonAuthors = new HashMap<>();
    public static CannedConfiguration NUCLEARCRAFT;
    public static void addInternalConfiguration(CannedConfiguration c, String link, String author){
        configurations.add(c);
        internalConfigurations.add(c);
        internalConfigurationLinks.put(c, link);
        internalConfigurationAuthors.put(c, author);
    }
    public static void addInternalAddon(NcpfAddon addon, String link, String author){
        internalAddons.add(addon);
        internalAddonLinks.put(addon, link);
        internalAddonAuthors.put(addon, author);
    }
    public static void initNuclearcraftConfiguration(){
        if(NUCLEARCRAFT!=null)return;//already done m8
        NUCLEARCRAFT = new CannedConfiguration(NcpfJsonConverter.parseJson(ResourceManager.getInternalResource("configurations/nuclearcraft.ncpf.json")), "default").addAlias("").addAlias("SF4");
        configurations.add(0, NUCLEARCRAFT);
        net.ncplanner.plannerator.planner.ncpf.Configuration.NUCLEARCRAFT=NUCLEARCRAFT;
        net.ncplanner.plannerator.planner.ncpf.Configuration.configurations.add(0,NUCLEARCRAFT);
    }
    public static void clearConfigurations(){
        configurations.clear();
        if(NUCLEARCRAFT!=null)configurations.add(NUCLEARCRAFT);
        internalConfigurations.clear();
        internalAddons.clear();
    }
}
