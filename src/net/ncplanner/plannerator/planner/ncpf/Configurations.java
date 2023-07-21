package net.ncplanner.plannerator.planner.ncpf;
import java.util.ArrayList;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.file.FileReader;
public class Configurations{
    public static final ArrayList<NCPFConfigurationContainer> configurations = new ArrayList<>();
    public static final ArrayList<Addon> internalAddons = new ArrayList<>();
    public static NCPFConfigurationContainer NUCLEARCRAFT;
    public static void addInternalAddon(Addon internalAddon, String link){
        internalAddons.add(internalAddon);
    }
    public static void initNuclearcraftConfiguration(){
        if(NUCLEARCRAFT!=null)return;//already done m8
        NUCLEARCRAFT = FileReader.read(() -> {
            return Core.getInputStream("configurations/nuclearcraft.ncpf");
        }).configuration;
        configurations.add(NUCLEARCRAFT);
    }
    public static void clearConfigurations(){
        configurations.clear();
        if(NUCLEARCRAFT!=null)configurations.add(NUCLEARCRAFT);
        internalAddons.clear();
    }
}