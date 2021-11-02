package net.ncplanner.plannerator.planner.module;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.configuration.AddonConfiguration;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.OverhaulMSR;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.OverhaulSFR;
import net.ncplanner.plannerator.multiblock.overhaul.turbine.OverhaulTurbine;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.file.FileReader;
import net.ncplanner.plannerator.planner.tutorial.Tutorial;
import net.ncplanner.plannerator.planner.tutorial.TutorialFileReader;
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
        Configuration.configurations.add(FileReader.read(() -> {
            return Core.getInputStream("configurations/aapn.ncpf");
        }).configuration.addAlternative("AAPN"));
        Configuration.addInternalAddon(() -> {
            return AddonConfiguration.convert(FileReader.read(() -> {
                return Core.getInputStream("configurations/addons/extreme_reactors.ncpf");
            }).configuration);
        }, "https://www.curseforge.com/minecraft/mc-mods/extreme-reactors");
        Configuration.addInternalAddon(() -> {
            return AddonConfiguration.convert(FileReader.read(() -> {
                return Core.getInputStream("configurations/addons/ic2.ncpf");
            }).configuration);
        }, "https://www.curseforge.com/minecraft/mc-mods/ic2-classic");
        Configuration.addInternalAddon(() -> {
            return AddonConfiguration.convert(FileReader.read(() -> {
                return Core.getInputStream("configurations/addons/qmd.ncpf");
            }).configuration);
        }, "https://www.curseforge.com/minecraft/mc-mods/qmd");
        Configuration.addInternalAddon(() -> {
            return AddonConfiguration.convert(FileReader.read(() -> {
                return Core.getInputStream("configurations/addons/trinity.ncpf");
            }).configuration);
        }, "https://www.curseforge.com/minecraft/mc-mods/trinity");
        Configuration.addInternalAddon(() -> {
            return AddonConfiguration.convert(FileReader.read(() -> {
                return Core.getInputStream("configurations/addons/ncouto.ncpf");
            }).configuration);
        }, "https://www.curseforge.com/minecraft/customization/nuclearcraft-overhauled-unrealistic-turbine");
        Configuration.addInternalAddon(() -> {
            return AddonConfiguration.convert(FileReader.read(() -> {
                return Core.getInputStream("configurations/addons/moar_heat_sinks.ncpf");
            }).configuration);
        }, "https://www.curseforge.com/minecraft/customization/moar-heat-sinks");
        Configuration.addInternalAddon(() -> {
            return AddonConfiguration.convert(FileReader.read(() -> {
                return Core.getInputStream("configurations/addons/moar_fuels.ncpf");
            }).configuration);
        }, "https://www.curseforge.com/minecraft/customization/moarfuels");
        Configuration.addInternalAddon(() -> {
            return AddonConfiguration.convert(FileReader.read(() -> {
                return Core.getInputStream("configurations/addons/moar_fuels_lite.ncpf");
            }).configuration);
        }, "https://www.curseforge.com/minecraft/customization/moarfuels");
        Configuration.addInternalAddon(() -> {
            return AddonConfiguration.convert(FileReader.read(() -> {
                return Core.getInputStream("configurations/addons/moar_fuels_ultra_lite.ncpf");
            }).configuration);
        }, "https://www.curseforge.com/minecraft/customization/moarfuels");
        Configuration.addInternalAddon(() -> {
            return AddonConfiguration.convert(FileReader.read(() -> {
                return Core.getInputStream("configurations/addons/moar_reactor_functionality.ncpf");
            }).configuration);
        }, "https://www.curseforge.com/minecraft/customization/moar-reactor-functionality");
        Configuration.addInternalAddon(() -> {
            return AddonConfiguration.convert(FileReader.read(() -> {
                return Core.getInputStream("configurations/addons/nuclear_oil_refining.ncpf");
            }).configuration);
        }, "https://www.curseforge.com/minecraft/customization/nuclear-oil-refining");
        Configuration.addInternalAddon(() -> {
            return AddonConfiguration.convert(FileReader.read(() -> {
                return Core.getInputStream("configurations/addons/nuclear_tree_factory.ncpf");
            }).configuration);
        }, "https://www.curseforge.com/minecraft/customization/nuclear-tree-factory");
        Configuration.addInternalAddon(() -> {
            return AddonConfiguration.convert(FileReader.read(() -> {
                return Core.getInputStream("configurations/addons/bes.ncpf");
            }).configuration);
        }, "https://www.curseforge.com/minecraft/customization/binarys-extra-stuff-bes");
        Configuration.addInternalAddon(() -> {
            return AddonConfiguration.convert(FileReader.read(() -> {
                return Core.getInputStream("configurations/addons/aop.ncpf");
            }).configuration);
        }, "https://www.curseforge.com/minecraft/customization/aop");
        Configuration.addInternalAddon(() -> {
            return AddonConfiguration.convert(FileReader.read(() -> {
                return Core.getInputStream("configurations/addons/nco_confectionery.ncpf");
            }).configuration);
        }, "https://www.curseforge.com/minecraft/customization/nco-confectionery");
        Configuration.addInternalAddon(() -> {
            return AddonConfiguration.convert(FileReader.read(() -> {
                return Core.getInputStream("configurations/addons/thorium_mixed_fuels.ncpf");
            }).configuration);
        }, "https://www.curseforge.com/minecraft/customization/thorium-mixed-fuels");
        Configuration.addInternalAddon(() -> {
            return AddonConfiguration.convert(FileReader.read(() -> {
                return Core.getInputStream("configurations/addons/inert_matrix_fuels.ncpf");
            }).configuration);
        }, "https://www.curseforge.com/minecraft/customization/inert-matrix-fuels");
        Configuration.addInternalAddon(() -> {
            return AddonConfiguration.convert(FileReader.read(() -> {
                return Core.getInputStream("configurations/addons/alloy_heat_sinks.ncpf");
            }).configuration);
        }, "https://www.curseforge.com/minecraft/customization/alloy-heat-sinks");
        Configuration.addInternalAddon(() -> {
            return AddonConfiguration.convert(FileReader.read(() -> {
                return Core.getInputStream("configurations/addons/spicy_heat_sinks_stable.ncpf");
            }).configuration);
        }, "https://www.curseforge.com/minecraft/customization/spicy-heat-sinks");
        Configuration.addInternalAddon(() -> {
            return AddonConfiguration.convert(FileReader.read(() -> {
                return Core.getInputStream("configurations/addons/spicy_heat_sinks_unstable.ncpf");
            }).configuration);
        }, "https://www.curseforge.com/minecraft/customization/spicy-heat-sinks");
    }
}