package planner.module;
import java.util.ArrayList;
import multiblock.configuration.AddonConfiguration;
import multiblock.configuration.Configuration;
import static multiblock.configuration.Configuration.getInputStream;
import multiblock.overhaul.fissionmsr.OverhaulMSR;
import multiblock.overhaul.fissionsfr.OverhaulSFR;
import multiblock.overhaul.turbine.OverhaulTurbine;
import planner.file.FileReader;
import planner.tutorial.Tutorial;
import planner.tutorial.TutorialFileReader;
public class OverhaulModule extends Module{
    public OverhaulModule(){
        super("overhaul", true);
    }
    @Override
    public String getDisplayName(){
        return "Overhaul";
    }
    @Override
    public String getDescription(){
        return "All the base NuclearCraft: Overhauled multiblocks";
    }
    @Override
    public void addMultiblockTypes(ArrayList multiblockTypes){
        multiblockTypes.add(new OverhaulSFR());
        multiblockTypes.add(new OverhaulMSR());
        multiblockTypes.add(new OverhaulTurbine());
    }
    @Override
    public void addTutorials(){
        Tutorial.addTutorials("Overhaul",
                TutorialFileReader.read("tutorials/overhaul/sfr.ncpt"),
                TutorialFileReader.read("tutorials/overhaul/msr.ncpt"),
                TutorialFileReader.read("tutorials/overhaul/turbine.ncpt"));
    }
    @Override
    public void addConfigurations(){
        Configuration.internalAddons.add(() -> {
            return AddonConfiguration.convert(FileReader.read(() -> {
                return getInputStream("configurations/addons/extreme_reactors.ncpf");
            }).configuration);
        });
        Configuration.internalAddons.add(() -> {
            return AddonConfiguration.convert(FileReader.read(() -> {
                return getInputStream("configurations/addons/ic2.ncpf");
            }).configuration);
        });
        Configuration.internalAddons.add(() -> {
            return AddonConfiguration.convert(FileReader.read(() -> {
                return getInputStream("configurations/addons/qmd.ncpf");
            }).configuration);
        });
        Configuration.internalAddons.add(() -> {
            return AddonConfiguration.convert(FileReader.read(() -> {
                return getInputStream("configurations/addons/trinity.ncpf");
            }).configuration);
        });
        Configuration.internalAddons.add(() -> {
            return AddonConfiguration.convert(FileReader.read(() -> {
                return getInputStream("configurations/addons/ncouto.ncpf");
            }).configuration);
        });
        Configuration.internalAddons.add(() -> {
            return AddonConfiguration.convert(FileReader.read(() -> {
                return getInputStream("configurations/addons/moar_heat_sinks.ncpf");
            }).configuration);
        });
        Configuration.internalAddons.add(() -> {
            return AddonConfiguration.convert(FileReader.read(() -> {
                return getInputStream("configurations/addons/moar_fuels.ncpf");
            }).configuration);
        });
        Configuration.internalAddons.add(() -> {
            return AddonConfiguration.convert(FileReader.read(() -> {
                return getInputStream("configurations/addons/moar_reactor_functionality.ncpf");
            }).configuration);
        });
        Configuration.internalAddons.add(() -> {
            return AddonConfiguration.convert(FileReader.read(() -> {
                return getInputStream("configurations/addons/nuclear_oil_refining.ncpf");
            }).configuration);
        });
        Configuration.internalAddons.add(() -> {
            return AddonConfiguration.convert(FileReader.read(() -> {
                return getInputStream("configurations/addons/nuclear_tree_factory.ncpf");
            }).configuration);
        });
        Configuration.internalAddons.add(() -> {
            return AddonConfiguration.convert(FileReader.read(() -> {
                return getInputStream("configurations/addons/aop.ncpf");
            }).configuration);
        });
        Configuration.internalAddons.add(() -> {
            return AddonConfiguration.convert(FileReader.read(() -> {
                return getInputStream("configurations/addons/nco_confectionery.ncpf");
            }).configuration);
        });
    }
}