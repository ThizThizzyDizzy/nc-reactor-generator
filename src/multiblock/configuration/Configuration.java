package multiblock.configuration;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import multiblock.configuration.underhaul.UnderhaulConfiguration;
import multiblock.configuration.overhaul.OverhaulConfiguration;
import planner.file.FileReader;
import multiblock.Multiblock;
import planner.Main;
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
    public static ArrayList<Configuration> configurations = new ArrayList<>();
    static{
        configurations.add(FileReader.read(() -> {
            return getInputStream("configurations/nuclearcraft.ncpf");
        }).configuration.addAlternative("").addAlternative("SF4"));
        configurations.add(FileReader.read(() -> {
            return getInputStream("configurations/po3.ncpf");
        }).configuration.addAlternative("PO3"));
        configurations.add(FileReader.read(() -> {
            return getInputStream("configurations/e2e.ncpf");
        }).configuration.addAlternative("E2E"));
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
        configuration.addons = fresh.addons;
        configuration.alternatives = fresh.alternatives;
        configuration.name = fresh.name;
        configuration.overhaul = fresh.overhaul;
        configuration.overhaulVersion = fresh.overhaulVersion;
        configuration.underhaul = fresh.underhaul;
        configuration.underhaulVersion = fresh.underhaulVersion;
    }
    public void apply(PartialConfiguration partial, ArrayList<Multiblock> multiblocks, PartialConfiguration parent){
        //TODO fix; this ignores addons
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
    public boolean underhaulNameMatches(Configuration other){
        return nameMatches(other)&&Objects.equals(underhaulVersion, other.underhaulVersion);
    }
    public boolean overhaulNameMatches(Configuration other){
        return nameMatches(other)&&Objects.equals(overhaulVersion, other.overhaulVersion);
    }
}