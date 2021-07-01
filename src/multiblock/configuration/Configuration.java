package multiblock.configuration;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import multiblock.Multiblock;
import multiblock.configuration.overhaul.OverhaulConfiguration;
import multiblock.configuration.underhaul.UnderhaulConfiguration;
import planner.Main;
import planner.exception.MissingConfigurationEntryException;
import planner.file.FileReader;
import planner.file.FileWriter;
import planner.file.NCPFFile;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
public class Configuration{
    public String name;
    public String overhaulVersion;
    public String underhaulVersion;
    public boolean addon;
    public ArrayList<Configuration> addons = new ArrayList<>();
    public static final ArrayList<Configuration> configurations = new ArrayList<>();
    public static final ArrayList<Supplier<AddonConfiguration>> internalAddons = new ArrayList<>();
    public static final HashMap<Supplier<AddonConfiguration>, AddonConfiguration> internalAddonCache = new HashMap<>();
    private static final Configuration NUCLEARCRAFT = FileReader.read(() -> {
            return getInputStream("configurations/nuclearcraft.ncpf");
        }).configuration.addAlternative("").addAlternative("SF4");
    static{
        configurations.add(NUCLEARCRAFT);
    }
    public static void clearConfigurations(){
        configurations.clear();
        configurations.add(NUCLEARCRAFT);
        internalAddons.clear();
        internalAddonCache.clear();
    }
    public ArrayList<String> alternatives = new ArrayList<>();
    public Configuration(String name, String version, String underhaulVersion){
        this.name = name;
        this.overhaulVersion = version;
        this.underhaulVersion = underhaulVersion;
    }
    public UnderhaulConfiguration underhaul;
    public OverhaulConfiguration overhaul;
    public Config save(Configuration parent, Config config){
        config.set("partial", isPartial());
        config.set("addon", addon);
        if(underhaul!=null)config.set("underhaul", underhaul.save(parent, isPartial()));
        if(underhaulVersion!=null)config.set("underhaulVersion", underhaulVersion);
        if(overhaul!=null)config.set("overhaul", overhaul.save(parent, isPartial()));
        if(overhaulVersion!=null)config.set("version", overhaulVersion);
        config.set("name", name);
        if(!addons.isEmpty()){
            ConfigList addns = new ConfigList();
            for(Configuration cnfg : addons){
                addns.add(cnfg.save(this, Config.newConfig()));
            }
            config.set("addons", addns);
        }
        return config;
    }
    public boolean isPartial(){
        return false;
    }
    public void impose(Configuration configuration){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        NCPFFile saver = new NCPFFile();
        saver.configuration = this;
        FileWriter.write(saver, out, FileWriter.NCPF);
        try{
            out.close();
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }
        Configuration fresh = FileReader.read(() -> {
            return new ByteArrayInputStream(out.toByteArray());
        }).configuration;
        if(fresh.overhaul!=null&&fresh.overhaul.fissionMSR!=null){
            for(int i = 0; i<fresh.overhaul.fissionMSR.allBlocks.size(); i++){
                fresh.overhaul.fissionMSR.allBlocks.get(i).displayTexture = overhaul.fissionMSR.allBlocks.get(i).displayTexture;
            }
        }
        impose(fresh, configuration);
    }
    public static void impose(Configuration toImpose, Configuration configuration){
        configuration.addons = toImpose.addons;
        configuration.alternatives = toImpose.alternatives;
        configuration.name = toImpose.name;
        if(configuration.overhaul==null&&configuration.underhaul!=null&&toImpose.underhaul==null&&toImpose.overhaul!=null){
            //imposing overhaul onto underhaul
            configuration.overhaul = toImpose.overhaul;
            configuration.overhaulVersion = toImpose.overhaulVersion;
        }else if(configuration.underhaul==null&&configuration.overhaul!=null&&toImpose.overhaul==null&&toImpose.underhaul!=null){
            //imposing underhaul onto overhaul
            configuration.underhaul = toImpose.underhaul;
            configuration.underhaulVersion = toImpose.underhaulVersion;
        }else{
            configuration.overhaul = toImpose.overhaul;
            configuration.overhaulVersion = toImpose.overhaulVersion;
            configuration.underhaul = toImpose.underhaul;
            configuration.underhaulVersion = toImpose.underhaulVersion;
        }
    }
    public void apply(PartialConfiguration partial, ArrayList<Multiblock> multiblocks, PartialConfiguration parent){
        if(underhaul!=null){
            partial.underhaul = new UnderhaulConfiguration();
            underhaul.apply(partial.underhaul, multiblocks, parent);
        }
        if(overhaul!=null){
            partial.overhaul = new OverhaulConfiguration();
            overhaul.apply(partial.overhaul, multiblocks, parent);
        }
        for(Configuration addon : addons){
            PartialConfiguration adn = new PartialConfiguration(addon.name, addon.overhaulVersion, addon.underhaulVersion);
            partial.addons.add(adn);
            adn.addon = true;
            addon.apply(adn, multiblocks, partial);
        }
    }
    public void apply(AddonConfiguration addon, Configuration parent){
        if(underhaul!=null){
            addon.underhaul = new UnderhaulConfiguration();
            addon.self.underhaul = new UnderhaulConfiguration();
            underhaul.apply(addon, parent);
        }
        if(overhaul!=null){
            addon.overhaul = new OverhaulConfiguration();
            addon.self.overhaul = new OverhaulConfiguration();
            overhaul.apply(addon, parent);
        }
    }
    public Configuration addAlternative(String s){
        alternatives.add(s);
        return this;
    }
    public String getShortName(){
        if(alternatives.isEmpty())return null;
        return alternatives.get(0).trim().isEmpty()?null:alternatives.get(0);
    }
    public static InputStream getInputStream(String path){
        try{
            if(new File("nbproject").exists()){
                return new FileInputStream(new File("src/"+path.replace("/", "/")));
            }else{
                JarFile jar = new JarFile(new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath().replace("%20", " ")));
                Enumeration enumEntries = jar.entries();
                while(enumEntries.hasMoreElements()){
                    JarEntry file = (JarEntry)enumEntries.nextElement();
                    if(file.getName().equals(path.replace("/", "/"))){
                        return jar.getInputStream(file);
                    }
                }
            }
            throw new IllegalArgumentException("Cannot find file: "+path);
        }catch(IOException ex){
            System.err.println("Couldn't read file: "+path);
            return null;
        }
    }
    @Override
    public String toString(){
        return name+" ("+(overhaulVersion==null?underhaulVersion:overhaulVersion)+")";
    }
    public boolean isConfigurationEqual(Configuration c){
        if(c==null)return false;
        return Objects.equals(c.overhaul, overhaul)&&Objects.equals(c.underhaul, underhaul);
    }
    public boolean isOverhaulConfigurationEqual(Configuration c){
        if(c==null)return false;
        return Objects.equals(c.overhaul, overhaul);
    }
    public boolean isUnderhaulConfigurationEqual(Configuration c){
        if(c==null)return false;
        return Objects.equals(c.underhaul, underhaul);
    }
    public String getFullName(){
        String full = name;
        for(Configuration addon : addons){
            full+="+"+addon.name;
        }
        return full;
    }
    public boolean nameMatches(Configuration other){
        return Objects.equals(name, other.name);
    }
    public boolean nameAndVersionMatches(Configuration other){
        return Objects.equals(name, other.name)&&Objects.equals(underhaulVersion, other.underhaulVersion)&&Objects.equals(overhaulVersion, other.overhaulVersion);
    }
    public boolean underhaulNameMatches(Configuration other){
        return nameMatches(other)&&Objects.equals(underhaulVersion, other.underhaulVersion);
    }
    public boolean overhaulNameMatches(Configuration other){
        return nameMatches(other)&&Objects.equals(overhaulVersion, other.overhaulVersion);
    }
    public void addAndConvertAddon(AddonConfiguration addon) throws MissingConfigurationEntryException{
        Configuration addn = addon.self;
        addn.convertAddon(addon, this);
        if(addn.underhaul!=null&&addn.underhaul.fissionSFR!=null&&underhaul!=null&&underhaul.fissionSFR!=null){
            underhaul.fissionSFR.allBlocks.addAll(addn.underhaul.fissionSFR.blocks);
            underhaul.fissionSFR.allFuels.addAll(addn.underhaul.fissionSFR.fuels);
        }
        if(addn.overhaul!=null&&addn.overhaul.fissionSFR!=null&&overhaul!=null&&overhaul.fissionSFR!=null){
            overhaul.fissionSFR.allBlocks.addAll(addn.overhaul.fissionSFR.blocks);
            overhaul.fissionSFR.allCoolantRecipes.addAll(addn.overhaul.fissionSFR.coolantRecipes);
            FOR:for(multiblock.configuration.overhaul.fissionsfr.Block block : addn.overhaul.fissionSFR.allBlocks){
                for(multiblock.configuration.overhaul.fissionsfr.Block b : overhaul.fissionSFR.allBlocks){
                    for(String nam : b.getLegacyNames()){
                        if(nam.equals(block.name)){
                            block.name = b.name;
                            b.allRecipes.addAll(block.recipes);
                            continue FOR;
                        }
                    }
                }
                throw new IllegalArgumentException("Unable to find block "+block.name+" for recipes!");
            }
        }
        if(addn.overhaul!=null&&addn.overhaul.fissionMSR!=null&&overhaul!=null&&overhaul.fissionMSR!=null){
            overhaul.fissionMSR.allBlocks.addAll(addn.overhaul.fissionMSR.blocks);
            FOR:for(multiblock.configuration.overhaul.fissionmsr.Block block : addn.overhaul.fissionMSR.allBlocks){
                for(multiblock.configuration.overhaul.fissionmsr.Block b : overhaul.fissionMSR.allBlocks){
                    for(String nam : b.getLegacyNames()){
                        if(nam.equals(block.name)){
                            block.name = b.name;
                            b.allRecipes.addAll(block.recipes);
                            continue FOR;
                        }
                    }
                }
                throw new IllegalArgumentException("Unable to find block "+block.name+" for recipes!");
            }
        }
        if(addn.overhaul!=null&&addn.overhaul.turbine!=null&&overhaul!=null&&overhaul.turbine!=null){
            overhaul.turbine.allBlocks.addAll(addn.overhaul.turbine.blocks);
            overhaul.turbine.allRecipes.addAll(addn.overhaul.turbine.recipes);
        }
        if(addn.overhaul!=null&&addn.overhaul.fusion!=null&&overhaul!=null&&overhaul.fusion!=null){
            overhaul.fusion.allBlocks.addAll(addn.overhaul.fusion.blocks);
            overhaul.fusion.allRecipes.addAll(addn.overhaul.fusion.recipes);
            overhaul.fusion.allCoolantRecipes.addAll(addn.overhaul.fusion.coolantRecipes);
        }
        addons.add(addn);
    }
    private void convertAddon(AddonConfiguration parent, Configuration convertTo) throws MissingConfigurationEntryException{
        if(underhaul!=null){
            underhaul.convertAddon(parent, convertTo);
        }
        if(overhaul!=null){
            overhaul.convertAddon(parent, convertTo);
        }
    }
    public Configuration findMatchingAddon(Configuration addon){
        for(Configuration addn : addons){
            if(addn.nameAndVersionMatches(addon))return addn;
        }
        throw new NullPointerException("No matching addons found for "+addon.toString()+"!");
    }
    public void removeAddon(Configuration addon){
        if(addon.underhaul!=null&&addon.underhaul.fissionSFR!=null&&underhaul!=null&&underhaul.fissionSFR!=null){
            underhaul.fissionSFR.allBlocks.removeAll(addon.underhaul.fissionSFR.blocks);
            underhaul.fissionSFR.allFuels.removeAll(addon.underhaul.fissionSFR.fuels);
        }
        if(addon.overhaul!=null&&addon.overhaul.fissionSFR!=null&&overhaul!=null&&overhaul.fissionSFR!=null){
            overhaul.fissionSFR.allBlocks.removeAll(addon.overhaul.fissionSFR.blocks);
            overhaul.fissionSFR.allCoolantRecipes.removeAll(addon.overhaul.fissionSFR.coolantRecipes);
            FOR:for(multiblock.configuration.overhaul.fissionsfr.Block block : addon.overhaul.fissionSFR.allBlocks){
                for(multiblock.configuration.overhaul.fissionsfr.Block b : overhaul.fissionSFR.allBlocks){
                    for(String nam : b.getLegacyNames()){
                        if(nam.equals(block.name)){
                            b.allRecipes.removeAll(block.recipes);
                            continue FOR;
                        }
                    }
                }
                throw new IllegalArgumentException("Unable to find block "+block.name+" to remove recipes!");
            }
        }
        if(addon.overhaul!=null&&addon.overhaul.fissionMSR!=null&&overhaul!=null&&overhaul.fissionMSR!=null){
            overhaul.fissionMSR.allBlocks.removeAll(addon.overhaul.fissionMSR.blocks);
            FOR:for(multiblock.configuration.overhaul.fissionmsr.Block block : addon.overhaul.fissionMSR.allBlocks){
                for(multiblock.configuration.overhaul.fissionmsr.Block b : overhaul.fissionMSR.allBlocks){
                    for(String nam : b.getLegacyNames()){
                        if(nam.equals(block.name)){
                            b.allRecipes.removeAll(block.recipes);
                            continue FOR;
                        }
                    }
                }
                throw new IllegalArgumentException("Unable to find block "+block.name+" to remove recipes!");
            }
        }
        if(addon.overhaul!=null&&addon.overhaul.turbine!=null&&overhaul!=null&&overhaul.turbine!=null){
            overhaul.turbine.allBlocks.removeAll(addon.overhaul.turbine.blocks);
            overhaul.turbine.allRecipes.removeAll(addon.overhaul.turbine.recipes);
        }
        if(addon.overhaul!=null&&addon.overhaul.fusion!=null&&overhaul!=null&&overhaul.fusion!=null){
            overhaul.fusion.allBlocks.removeAll(addon.overhaul.fusion.blocks);
            overhaul.fusion.allRecipes.removeAll(addon.overhaul.fusion.recipes);
            overhaul.fusion.allCoolantRecipes.removeAll(addon.overhaul.fusion.coolantRecipes);
        }
        addons.remove(addon);
    }
    public String getSaveName(boolean overhaul){
        String nam = name+" "+(overhaul?overhaulVersion:underhaulVersion);
        for(Configuration c : addons){
            if(overhaul&&c.overhaulVersion==null)continue;
            if(!overhaul&&c.underhaulVersion==null)continue;
            nam+="\n+ "+c.getSaveName(overhaul);
        }
        return nam;
    }
    public Configuration copy(){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        NCPFFile saver = new NCPFFile();
        saver.configuration = this;
        FileWriter.write(saver, out, FileWriter.NCPF);
        try{
            out.close();
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }
        Configuration copy = FileReader.read(() -> {
            return new ByteArrayInputStream(out.toByteArray());
        }).configuration;
        if(copy.overhaul!=null&&copy.overhaul.fissionMSR!=null){
            for(int i = 0; i<copy.overhaul.fissionMSR.allBlocks.size(); i++){
                copy.overhaul.fissionMSR.allBlocks.get(i).displayTexture = overhaul.fissionMSR.allBlocks.get(i).displayTexture;
            }
        }
        return copy;
    }
    public Configuration makeAddon(Configuration parent){
        Configuration addon = new Configuration("Nuclearcraft CT Additions", "Unknown", null);
        addon.addon = true;
        if(overhaul!=null){
            addon.overhaul = new OverhaulConfiguration();
            overhaul.makeAddon(parent.overhaul, addon.overhaul);
        }
        if(underhaul!=null){
            addon.underhaul = new UnderhaulConfiguration();
            underhaul.makeAddon(parent.underhaul, addon.underhaul);
        }
        return addon;
    }
}