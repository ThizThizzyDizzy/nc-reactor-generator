package net.ncplanner.plannerator.planner.ncpf;
import java.util.ArrayList;
import java.util.HashMap;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.file.FileReader;
public class Configurations{
    public static final ArrayList<NCPFConfigurationContainer> configurations = new ArrayList<>();
    public static final ArrayList<Addon> internalAddons = new ArrayList<>();
    public static final HashMap<Addon, String> internalAddonLinks = new HashMap<>();
    public static NCPFConfigurationContainer NUCLEARCRAFT;
    public static void addInternalAddon(Addon addon, String link){
        internalAddons.add(addon);
        internalAddonLinks.put(addon, link);
    }
    public static void initNuclearcraftConfiguration(){
        if(NUCLEARCRAFT!=null)return;//already done m8
        NUCLEARCRAFT = FileReader.read(() -> {
            return Core.getInputStream("configurations/nuclearcraft.ncpf");
        }).configuration;//TODO alternatives (for discord bot): "" and "SF4"
        //TODO path = "default"
        configurations.add(NUCLEARCRAFT);
    }
    public static void clearConfigurations(){
        configurations.clear();
        if(NUCLEARCRAFT!=null)configurations.add(NUCLEARCRAFT);
        internalAddons.clear();
    }
}