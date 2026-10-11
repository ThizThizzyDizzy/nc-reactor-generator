package net.ncplanner.plannerator.planner.configuration;
import com.thizthizzydizzy.dizzyengine.ResourceManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashMap;
import net.ncplanner.ncpf.io.NcpfJsonConverter;
import net.ncplanner.ncpf.structure.NcpfAddon;
import net.ncplanner.plannerator.planner.ncpf.Configuration;
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
        try(InputStream stream = ResourceManager.getInternalResource("configurations/nuclearcraft.ncpf.json")){
            NUCLEARCRAFT = new CannedConfiguration(NcpfJsonConverter.parseJson(stream), "default").addAlias("").addAlias("SF4");
        }catch(IOException ex){throw new UncheckedIOException("Failed to load default NuclearCraft configuration", ex);}
        configurations.add(0, NUCLEARCRAFT);
        Configuration.NUCLEARCRAFT=NUCLEARCRAFT;
        Configuration.configurations.add(0,NUCLEARCRAFT);
    }
    public static void clearConfigurations(){
        configurations.clear();
        if(NUCLEARCRAFT!=null)configurations.add(NUCLEARCRAFT);
        internalConfigurations.clear();
        internalAddons.clear();
    }
}
