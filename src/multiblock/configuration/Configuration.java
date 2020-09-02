package multiblock.configuration;
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
        if(underhaul!=null){
            config.set("underhaul", underhaul.save(parent, isPartial()));
            config.set("underhaulVersion", underhaulVersion);
        }
        if(overhaul!=null){
            config.set("overhaul", overhaul.save(parent, isPartial()));
            config.set("version", overhaulVersion);
        }
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
        if(Objects.equals(configuration.name, name)){
            if(underhaul!=null)configuration.underhaul = underhaul;
            if(overhaul!=null)configuration.overhaul = overhaul;
        }else{
            configuration.underhaul = underhaul;
            configuration.overhaul = overhaul;
        }
        configuration.name = name;
        configuration.overhaulVersion = overhaulVersion;
        configuration.underhaulVersion = underhaulVersion;
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
}