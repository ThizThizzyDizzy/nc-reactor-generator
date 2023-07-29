package net.ncplanner.plannerator.planner.module;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.multiblock.CuboidalMultiblock;
import net.ncplanner.plannerator.multiblock.Direction;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.editor.Decal;
import net.ncplanner.plannerator.multiblock.editor.decal.CellFluxDecal;
import net.ncplanner.plannerator.multiblock.editor.decal.NeutronSourceDecal;
import net.ncplanner.plannerator.multiblock.editor.decal.NeutronSourceLineDecal;
import net.ncplanner.plannerator.multiblock.editor.decal.NeutronSourceTargetDecal;
import net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.OverhaulMSR;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.OverhaulSFR;
import net.ncplanner.plannerator.multiblock.overhaul.turbine.OverhaulTurbine;
import net.ncplanner.plannerator.ncpf.configuration.NCPFOverhaulMSRConfiguration;
import net.ncplanner.plannerator.ncpf.configuration.NCPFOverhaulSFRConfiguration;
import net.ncplanner.plannerator.ncpf.configuration.NCPFOverhaulTurbineConfiguration;
import net.ncplanner.plannerator.ncpf.design.NCPFOverhaulSFRDesign;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.editor.overlay.EditorOverlay;
import net.ncplanner.plannerator.planner.file.FileReader;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulMSRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulSFRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulTurbineConfiguration;
import net.ncplanner.plannerator.planner.ncpf.design.OverhaulSFRDesign;
import net.ncplanner.plannerator.planner.ncpf.module.OverhaulMSRSettingsModule;
import net.ncplanner.plannerator.planner.ncpf.module.OverhaulSFRSettingsModule;
import net.ncplanner.plannerator.planner.ncpf.module.OverhaulTurbineSettingsModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.*;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.*;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.*;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.FuelStatsModule;
import net.ncplanner.plannerator.planner.tutorial.Tutorial;
import net.ncplanner.plannerator.planner.tutorial.TutorialFileReader;
public class OverhaulModule extends Module<Object>{
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
//        multiblockTypes.add(new OverhaulMSR());
//        multiblockTypes.add(new OverhaulTurbine());
    }
    @Override
    public void registerNCPF(){
        registerNCPFConfiguration(OverhaulSFRConfiguration::new);
        registerNCPFConfiguration(OverhaulMSRConfiguration::new);
        registerNCPFConfiguration(OverhaulTurbineConfiguration::new);
        registerNCPFDesign(NCPFOverhaulSFRDesign::new, OverhaulSFRDesign::new);
//        registerNCPFDesign(NCPFOverhaulMSRDesign::new, OverhaulMSRDesign::new);
//        registerNCPFDesign(NCPFOverhaulTurbineDesign::new, OverhaulTurbineDesign::new);
        
        registerNCPFModule(OverhaulSFRSettingsModule::new);
        registerNCPFModule(net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.ConductorModule::new);
        registerNCPFModule(net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.CasingModule::new);
        registerNCPFModule(CoolantVentModule::new);
        registerNCPFModule(net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.ControllerModule::new);
        registerNCPFModule(FuelCellModule::new);
        registerNCPFModule(net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.IrradiatorModule::new);
        registerNCPFModule(net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.ReflectorModule::new);
        registerNCPFModule(net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.ModeratorModule::new);
        registerNCPFModule(net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.NeutronShieldModule::new);
        registerNCPFModule(HeatsinkModule::new);
        registerNCPFModule(net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.NeutronSourceModule::new);
        registerNCPFModule(net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.PortModule::new);
        registerNCPFModule(net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.RecipePortsModule::new);
        registerNCPFModule(FuelStatsModule::new);
        registerNCPFModule(net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.IrradiatorStatsModule::new);
        registerNCPFModule(CoolantRecipeStatsModule::new);
        
        registerNCPFModule(OverhaulMSRSettingsModule::new);
        registerNCPFModule(net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.ConductorModule::new);
        registerNCPFModule(net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.CasingModule::new);
        registerNCPFModule(net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.ControllerModule::new);
        registerNCPFModule(FuelVesselModule::new);
        registerNCPFModule(net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.IrradiatorModule::new);
        registerNCPFModule(net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.ReflectorModule::new);
        registerNCPFModule(net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.ModeratorModule::new);
        registerNCPFModule(net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.NeutronShieldModule::new);
        registerNCPFModule(HeaterModule::new);
        registerNCPFModule(net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.NeutronSourceModule::new);
        registerNCPFModule(net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.PortModule::new);
        registerNCPFModule(net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.RecipePortsModule::new);
        registerNCPFModule(net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.FuelStatsModule::new);
        registerNCPFModule(HeaterStatsModule::new);
        registerNCPFModule(net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.IrradiatorStatsModule::new);
        
        registerNCPFModule(OverhaulTurbineSettingsModule::new);
        registerNCPFModule(BladeModule::new);
        registerNCPFModule(StatorModule::new);
        registerNCPFModule(CoilModule::new);
        registerNCPFModule(BearingModule::new);
        registerNCPFModule(ShaftModule::new);
        registerNCPFModule(ConnectorModule::new);
        registerNCPFModule(net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.ControllerModule::new);
        registerNCPFModule(net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.CasingModule::new);
        registerNCPFModule(InletModule::new);
        registerNCPFModule(OutletModule::new);
        registerNCPFModule(RecipeStatsModule::new);
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
        addConfiguration(FileReader.read(() -> {
            return Core.getInputStream("configurations/aapn.ncpf");
        }).configuration);
        addConfiguration(FileReader.read(() -> {
            return Core.getInputStream("configurations/quanta.ncpf");
        }).configuration);
        addAddon(FileReader.read(() -> {
            return Core.getInputStream("configurations/addons/extreme_reactors.ncpf");
        }).addons.get(0), "https://www.curseforge.com/minecraft/mc-mods/extreme-reactors");
        addAddon(FileReader.read(() -> {
            return Core.getInputStream("configurations/addons/ic2.ncpf");
        }).addons.get(0), "https://www.curseforge.com/minecraft/mc-mods/ic2-classic");
        addAddon(FileReader.read(() -> {
            return Core.getInputStream("configurations/addons/qmd.ncpf");
        }).addons.get(0), "https://www.curseforge.com/minecraft/mc-mods/qmd");
        addAddon(FileReader.read(() -> {
            return Core.getInputStream("configurations/addons/trinity.ncpf");
        }).addons.get(0), "https://www.curseforge.com/minecraft/mc-mods/trinity");
        addAddon(FileReader.read(() -> {
            return Core.getInputStream("configurations/addons/ncouto.ncpf");
        }).addons.get(0), "https://www.curseforge.com/minecraft/customization/nuclearcraft-overhauled-unrealistic-turbine");
        addAddon(FileReader.read(() -> {
            return Core.getInputStream("configurations/addons/moar_heat_sinks.ncpf");
        }).addons.get(0), "https://www.curseforge.com/minecraft/customization/moar-heat-sinks");
        addAddon(FileReader.read(() -> {
            return Core.getInputStream("configurations/addons/moar_fuels.ncpf");
        }).addons.get(0), "https://www.curseforge.com/minecraft/customization/moarfuels");
        addAddon(FileReader.read(() -> {
            return Core.getInputStream("configurations/addons/moar_fuels_lite.ncpf");
        }).addons.get(0), "https://www.curseforge.com/minecraft/customization/moarfuels");
        addAddon(FileReader.read(() -> {
            return Core.getInputStream("configurations/addons/moar_fuels_ultra_lite.ncpf");
        }).addons.get(0), "https://www.curseforge.com/minecraft/customization/moarfuels");
        addAddon(FileReader.read(() -> {
            return Core.getInputStream("configurations/addons/moar_reactor_functionality.ncpf");
        }).addons.get(0), "https://www.curseforge.com/minecraft/customization/moar-reactor-functionality");
        addAddon(FileReader.read(() -> {
            return Core.getInputStream("configurations/addons/nuclear_oil_refining.ncpf");
        }).addons.get(0), "https://www.curseforge.com/minecraft/customization/nuclear-oil-refining");
        addAddon(FileReader.read(() -> {
            return Core.getInputStream("configurations/addons/nuclear_tree_factory.ncpf");
        }).addons.get(0), "https://www.curseforge.com/minecraft/customization/nuclear-tree-factory");
        addAddon(FileReader.read(() -> {
            return Core.getInputStream("configurations/addons/bes.ncpf");
        }).addons.get(0), "https://www.curseforge.com/minecraft/customization/binarys-extra-stuff-bes");
        addAddon(FileReader.read(() -> {
            return Core.getInputStream("configurations/addons/aop.ncpf");
        }).addons.get(0), "https://www.curseforge.com/minecraft/customization/aop");
        addAddon(FileReader.read(() -> {
            return Core.getInputStream("configurations/addons/nco_confectionery.ncpf");
        }).addons.get(0), "https://www.curseforge.com/minecraft/customization/nco-confectionery");
        addAddon(FileReader.read(() -> {
            return Core.getInputStream("configurations/addons/thorium_mixed_fuels.ncpf");
        }).addons.get(0), "https://www.curseforge.com/minecraft/customization/thorium-mixed-fuels");
        addAddon(FileReader.read(() -> {
            return Core.getInputStream("configurations/addons/inert_matrix_fuels.ncpf");
        }).addons.get(0), "https://www.curseforge.com/minecraft/customization/inert-matrix-fuels");
        addAddon(FileReader.read(() -> {
            return Core.getInputStream("configurations/addons/alloy_heat_sinks.ncpf");
        }).addons.get(0), "https://www.curseforge.com/minecraft/customization/alloy-heat-sinks");
        addAddon(FileReader.read(() -> {
            return Core.getInputStream("configurations/addons/spicy_heat_sinks_stable.ncpf");
        }).addons.get(0), "https://www.curseforge.com/minecraft/customization/spicy-heat-sinks");
        addAddon(FileReader.read(() -> {
            return Core.getInputStream("configurations/addons/spicy_heat_sinks_unstable.ncpf");
        }).addons.get(0), "https://www.curseforge.com/minecraft/customization/spicy-heat-sinks");
    }
    private final EditorOverlay<net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block> sfrActiveModeratorOverlay = new EditorOverlay<net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block>("Active Moderators", "Highlights active moderators with a green outline", true){
        @Override
        public void render(Renderer renderer, float x, float y, float width, float height, net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block block, Multiblock multiblock){
            if(block.isModeratorActive()){
                block.drawOutline(renderer, x, y, width, height, Core.theme.getBlockColorOutlineActive());
            }
        }
    };
    private final EditorOverlay<net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block> msrActiveModeratorOverlay = new EditorOverlay<net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block>("Active Moderators", "Highlights active moderators with a green outline", true){
        @Override
        public void render(Renderer renderer, float x, float y, float width, float height, net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block block, Multiblock multiblock){
            if(block.isModeratorActive()){
                block.drawOutline(renderer, x, y, width, height, Core.theme.getBlockColorOutlineActive());
            }
        }
    };
    private final EditorOverlay<net.ncplanner.plannerator.multiblock.overhaul.turbine.Block> validCoilOverlay = new EditorOverlay<net.ncplanner.plannerator.multiblock.overhaul.turbine.Block>("Valid Coils", "Highlights valid coils with a green outline", true){
        @Override
        public void render(Renderer renderer, float x, float y, float width, float height, net.ncplanner.plannerator.multiblock.overhaul.turbine.Block block, Multiblock multiblock){
            if(block.isActive()&&block.isCoil()){
                block.drawOutline(renderer, x, y, width, height, Core.theme.getBlockColorOutlineActive());
            }
        }
    };
    private final EditorOverlay<net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block> sfrBlockRecipeOverlay = new EditorOverlay<net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block>("Block Recipes", "Shows the chosen recipe on blocks that have multiple recipes", true){
        @Override
        public void render(Renderer renderer, float x, float y, float width, float height, net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block block, Multiblock multiblock){
            net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block b = (net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block)block;
            if(b.fuel!=null&&(b.template.parent==null?b.template.getBlockRecipes():b.template.parent.getBlockRecipes()).size()>1){
                renderer.setWhite(b.template.parent==null?1:0.75f);
                renderer.drawImage(b.fuel.getDisplayTexture(), x+width*.125f, y+height*.125f, x+width*.875f, y+height*.875f);
            }
            if(b.irradiatorRecipe!=null&&(b.template.parent==null?b.template.getBlockRecipes():b.template.parent.getBlockRecipes()).size()>1){
                renderer.setWhite(b.template.parent==null?1:0.75f);
                renderer.drawImage(b.irradiatorRecipe.getDisplayTexture(), x+width*.125f, y+height*.125f, x+width*.875f, y+height*.875f);
            }
        }
    };
    private final EditorOverlay<net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block> msrBlockRecipeOverlay = new EditorOverlay<net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block>("Block Recipes", "Shows the chosen recipe on blocks that have multiple recipes", true){
        @Override
        public void render(Renderer renderer, float x, float y, float width, float height, net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block block, Multiblock multiblock){
            net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block b = (net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block)block;
            if(b.recipe!=null&&(b.template.parent==null?b.template.allRecipes:b.template.parent.allRecipes).size()>1){
                renderer.setWhite(b.template.parent==null?1:.75f);
                renderer.drawImage(b.recipe.inputDisplayTexture, x+width*.25f, y+height*.25f, x+width*.75f, y+height*.75f);
            }
        }
    };
    private final EditorOverlay<net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block> sfrPrimedCellOverlay = new EditorOverlay<net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block>("Primed Cells", "Shows which cells are primed", true){
        @Override
        public void render(Renderer renderer, float x, float y, float width, float height, net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block block, Multiblock multiblock){
            if(mode==1)return;
            net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block b = (net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block)block;
            if(b.template.fuelCell!=null&&b.fuel!=null){
                boolean self = b.fuel.stats.selfPriming;
                net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block src = b.source;
                if(src!=null||self){
                    b.drawCircle(renderer, x, y, width, height, Core.theme.getBlockColorSourceCircle(src==null?1:src.template.neutronSource.efficiency, self));
                }
            }
        }
        @Override
        public void refresh(Multiblock multiblock){
            decals.clear();
            if(mode==0)return;
            ((CuboidalMultiblock)multiblock).forEachInternalPosition((x, y, z) -> {
                net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block b = (net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block)multiblock.getBlock(x, y, z);
                if(b!=null){
                    net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block s = b.source;
                    if(s!=null){
                        Direction d = Direction.get(s.x,s.y,s.z,b.x,b.y,b.z);
                        if(d==null)return;//wat
                        int dist = Math.abs(s.x-b.x)+Math.abs(s.y-b.y)+Math.abs(s.z-b.z);
                        decals.enqueue(new NeutronSourceTargetDecal(s.x, s.y, s.z, d));
                        for(int i = 1; i<dist; i++){
                            decals.enqueue(new NeutronSourceLineDecal(b.x-d.x*i, b.y-d.y*i, b.z-d.z*i, d));
                        }
                        decals.enqueue(new NeutronSourceDecal(b.x, b.y, b.z, d.getOpposite()));
                    }
                }
            });
        }
    }.addMode("Circle", "Shows a marker on primed cells.").addMode("Decal", "Draws a line from neutron sources to the cells they are priming").addMode("Both", "Shows a marker on primed cells and a line to the neutron source priming them");
    private final EditorOverlay<net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block> msrPrimedVesselOverlay = new EditorOverlay<net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block>("Primed Vessels", "Shows which cells are primed", true){
        @Override
        public void render(Renderer renderer, float x, float y, float width, float height, net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block block, Multiblock multiblock){
            if(mode==1)return;
            net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block b = (net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block)block;
            if(b.template.fuelVessel&&(b.template.fuelVesselHasBaseStats||b.recipe!=null)){
                boolean self = b.recipe==null?b.template.fuelVesselSelfPriming:b.recipe.fuelVesselSelfPriming;
                net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block src = b.source;
                if(src!=null||self){
                    b.drawCircle(renderer, x, y, width, height, Core.theme.getBlockColorSourceCircle(src==null?1:src.template.sourceEfficiency, self));
                }
            }
        }
        @Override
        public void refresh(Multiblock multiblock){
            decals.clear();
            if(mode==0)return;
            ((CuboidalMultiblock)multiblock).forEachInternalPosition((x, y, z) -> {
                net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block b = (net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block)multiblock.getBlock(x, y, z);
                if(b!=null){
                    net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block s = b.source;
                    if(s!=null){
                        Direction d = Direction.get(s.x,s.y,s.z,b.x,b.y,b.z);
                        int dist = Math.abs(s.x-b.x)+Math.abs(s.y-b.y)+Math.abs(s.z-b.z);
                        decals.enqueue(new NeutronSourceTargetDecal(s.x, s.y, s.z, d));
                        for(int i = 1; i<dist; i++){
                            decals.enqueue(new NeutronSourceLineDecal(b.x-d.x*i, b.y-d.y*i, b.z-d.z*i, d));
                        }
                        decals.enqueue(new NeutronSourceDecal(b.x, b.y, b.z, d.getOpposite()));
                    }
                }
            });
        }
    }.addMode("Circle", "Shows a marker on primed vessels.").addMode("Decal", "Draws a line from neutron sources to the vessels they are priming").addMode("Both", "Shows a marker on primed vessels and a line to the neutron source priming them");
    private final EditorOverlay<net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block> sfrClusterOverlay = new EditorOverlay<net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block>("Clusters", "Outlines clusters in the reactor", true){
        @Override
        public void render(Renderer renderer, float x, float y, float width, float height, net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block block, Multiblock multiblock){
            net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block b = (net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block)block;
            OverhaulSFR.Cluster cluster = b.cluster;
            if(cluster!=null){
                Color primaryColor = null;
                if(cluster.netHeat>0){
                    primaryColor = Core.theme.getClusterOverheatingColor();
                }
                if(cluster.coolingPenaltyMult<1){
                    primaryColor = Core.theme.getClusterOvercoolingColor();
                }
                if(primaryColor!=null){
                    renderer.setColor(primaryColor, .125f);
                    renderer.fillRect(x, y, x+width, y+height);
                    renderer.setColor(primaryColor, .75f);
                    float border = width/8;
                    boolean top = cluster.contains(b.x, b.y, b.z-1);
                    boolean right = cluster.contains(b.x+1, b.y, b.z);
                    boolean bottom = cluster.contains(b.x, b.y, b.z+1);
                    boolean left = cluster.contains(b.x-1, b.y, b.z);
                    if(!top||!left||!cluster.contains(b.x-1, b.y, b.z-1)){//top left
                        renderer.fillRect(x, y, x+border, y+border);
                    }
                    if(!top){//top
                        renderer.fillRect(x+width/2-border, y, x+width/2+border, y+border);
                    }
                    if(!top||!right||!cluster.contains(b.x+1, b.y, b.z-1)){//top right
                        renderer.fillRect(x+width-border, y, x+width, y+border);
                    }
                    if(!right){//right
                        renderer.fillRect(x+width-border, y+height/2-border, x+width, y+height/2+border);
                    }
                    if(!bottom||!right||!cluster.contains(b.x+1, b.y, b.z+1)){//bottom right
                        renderer.fillRect(x+width-border, y+height-border, x+width, y+height);
                    }
                    if(!bottom){//bottom
                        renderer.fillRect(x+width/2-border, y+height-border, x+width/2+border, y+height);
                    }
                    if(!bottom||!left||!cluster.contains(b.x-1, b.y, b.z+1)){//bottom left
                        renderer.fillRect(x, y+height-border, x+border, y+height);
                    }
                    if(!left){//left
                        renderer.fillRect(x, y+height/2-border, x+border, y+height/2+border);
                    }
                }
                Color secondaryColor = null;
                if(!cluster.isConnectedToWall){
                    secondaryColor = Core.theme.getClusterDisconnectedColor();
                }
                if(!cluster.isCreated()){
                    secondaryColor = Core.theme.getClusterInvalidColor();
                }
                if(secondaryColor!=null){
                    renderer.setColor(secondaryColor, .75f);
                    float border = width/8;
                    boolean top = cluster.contains(b.x, b.y, b.z-1);
                    boolean right = cluster.contains(b.x+1, b.y, b.z);
                    boolean bottom = cluster.contains(b.x, b.y, b.z+1);
                    boolean left = cluster.contains(b.x-1, b.y, b.z);
                    if(!top){//top
                        renderer.fillRect(x+border, y, x+width/2-border, y+border);
                        renderer.fillRect(x+width/2+border, y, x+width-border, y+border);
                    }
                    if(!right){//right
                        renderer.fillRect(x+width-border, y+border, x+width, y+height/2-border);
                        renderer.fillRect(x+width-border, y+height/2+border, x+width, y+height-border);
                    }
                    if(!bottom){//bottom
                        renderer.fillRect(x+border, y+height-border, x+width/2-border, y+height);
                        renderer.fillRect(x+width/2+border, y+height-border, x+width-border, y+height);
                    }
                    if(!left){//left
                        renderer.fillRect(x, y+border, x+border, y+height/2-border);
                        renderer.fillRect(x, y+height/2+border, x+border, y+height-border);
                    }
                }
            }
        }
    };
    private final EditorOverlay<net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block> msrClusterOverlay = new EditorOverlay<net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block>("Clusters", "Outlines clusters in the reactor", true){
        @Override
        public void render(Renderer renderer, float x, float y, float width, float height, net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block block, Multiblock multiblock){
            net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block b = (net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block)block;
            OverhaulMSR.Cluster cluster = b.cluster;
            if(cluster!=null){
                Color primaryColor = null;
                if(cluster.netHeat>0){
                    primaryColor = Core.theme.getClusterOverheatingColor();
                }
                if(cluster.coolingPenaltyMult<1){
                    primaryColor = Core.theme.getClusterOvercoolingColor();
                }
                if(primaryColor!=null){
                    renderer.setColor(primaryColor, .125f);
                    renderer.fillRect(x, y, x+width, y+height);
                    renderer.setColor(primaryColor, .75f);
                    float border = width/8;
                    boolean top = cluster.contains(b.x, b.y, b.z-1);
                    boolean right = cluster.contains(b.x+1, b.y, b.z);
                    boolean bottom = cluster.contains(b.x, b.y, b.z+1);
                    boolean left = cluster.contains(b.x-1, b.y, b.z);
                    if(!top||!left||!cluster.contains(b.x-1, b.y, b.z-1)){//top left
                        renderer.fillRect(x, y, x+border, y+border);
                    }
                    if(!top){//top
                        renderer.fillRect(x+width/2-border, y, x+width/2+border, y+border);
                    }
                    if(!top||!right||!cluster.contains(b.x+1, b.y, b.z-1)){//top right
                        renderer.fillRect(x+width-border, y, x+width, y+border);
                    }
                    if(!right){//right
                        renderer.fillRect(x+width-border, y+height/2-border, x+width, y+height/2+border);
                    }
                    if(!bottom||!right||!cluster.contains(b.x+1, b.y, b.z+1)){//bottom right
                        renderer.fillRect(x+width-border, y+height-border, x+width, y+height);
                    }
                    if(!bottom){//bottom
                        renderer.fillRect(x+width/2-border, y+height-border, x+width/2+border, y+height);
                    }
                    if(!bottom||!left||!cluster.contains(b.x-1, b.y, b.z+1)){//bottom left
                        renderer.fillRect(x, y+height-border, x+border, y+height);
                    }
                    if(!left){//left
                        renderer.fillRect(x, y+height/2-border, x+border, y+height/2+border);
                    }
                }
                Color secondaryColor = null;
                if(!cluster.isConnectedToWall){
                    secondaryColor = Core.theme.getClusterDisconnectedColor();
                }
                if(!cluster.isCreated()){
                    secondaryColor = Core.theme.getClusterInvalidColor();
                }
                if(secondaryColor!=null){
                    renderer.setColor(secondaryColor, .75f);
                    float border = width/8;
                    boolean top = cluster.contains(b.x, b.y, b.z-1);
                    boolean right = cluster.contains(b.x+1, b.y, b.z);
                    boolean bottom = cluster.contains(b.x, b.y, b.z+1);
                    boolean left = cluster.contains(b.x-1, b.y, b.z);
                    if(!top){//top
                        renderer.fillRect(x+border, y, x+width/2-border, y+border);
                        renderer.fillRect(x+width/2+border, y, x+width-border, y+border);
                    }
                    if(!right){//right
                        renderer.fillRect(x+width-border, y+border, x+width, y+height/2-border);
                        renderer.fillRect(x+width-border, y+height/2+border, x+width, y+height-border);
                    }
                    if(!bottom){//bottom
                        renderer.fillRect(x+border, y+height-border, x+width/2-border, y+height);
                        renderer.fillRect(x+width/2+border, y+height-border, x+width-border, y+height);
                    }
                    if(!left){//left
                        renderer.fillRect(x, y+border, x+border, y+height/2-border);
                        renderer.fillRect(x, y+height/2+border, x+border, y+height-border);
                    }
                }
            }
        }
    };
    private final EditorOverlay<net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block> sfrFluxOverlay = new EditorOverlay<net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block>("Neutron Flux Propogation", "Shows the neutron flux propogation through the reactor", false){
        @Override
        public void refresh(Multiblock<net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block> multiblock){
            decals.clear();
            OverhaulSFR sfr = (OverhaulSFR)multiblock;
            switch(mode){
                case 0:
                    for(Decal d : sfr.initialFluxDecals)decals.enqueue(d);
                    break;
                case 1:
                    for(Decal d : sfr.finalFluxDecals)decals.enqueue(d);
                    break;
                case 3:
                    for(Decal d : sfr.shutdownFluxDecals)decals.enqueue(d);
                    break;
            }
            ArrayList<Decal> cellFluxes = new ArrayList<>();
            HashSet<Decal> badCellFluxes = new HashSet<>();
            HashSet<Decal> zeroCellFluxes = new HashSet<>();
            for(Decal d : decals){
                if(d instanceof CellFluxDecal){
                    if(((CellFluxDecal)d).flux==0){
                        zeroCellFluxes.add(d);
                        continue;
                    }
                    for(Decal d2 : cellFluxes){
                        if(d2.x==d.x&&d2.y==d.y&&d2.z==d.z){
                            badCellFluxes.add(d2);
                        }
                    }
                    cellFluxes.add(d);
                }
            }
            for(Decal d : zeroCellFluxes){
                if(d instanceof CellFluxDecal){
                    for(Decal d2 : cellFluxes){
                        if(d2.x==d.x&&d2.y==d.y&&d2.z==d.z){
                            badCellFluxes.add(d);
                        }
                    }
                }
            }
            for(Iterator<Decal> it = decals.iterator(); it.hasNext();){
                Decal next = it.next();
                if(badCellFluxes.contains(next))it.remove();
            }
        }
    }.addMode("Initial", "Shows the first flux propogation, before inactive cells are excluded").addMode("Final", "Shows the final flux propogation, after inactive cells are excluded and calculation is complete").addMode("Shutdown", "Shows the flux propogation after all neutron shields are closed");
    private final EditorOverlay<net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block> msrFluxOverlay = new EditorOverlay<net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block>("Neutron Flux Propogation", "Shows the neutron flux propogation through the reactor", false){
        @Override
        public void refresh(Multiblock<net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block> multiblock){
            decals.clear();
            OverhaulMSR msr = (OverhaulMSR)multiblock;
            switch(mode){
                case 0:
                    for(Decal d : msr.initialFluxDecals)decals.enqueue(d);
                    break;
                case 1:
                    for(Decal d : msr.finalFluxDecals)decals.enqueue(d);
                    break;
                case 3:
                    for(Decal d : msr.shutdownFluxDecals)decals.enqueue(d);
                    break;
            }
            ArrayList<Decal> cellFluxes = new ArrayList<>();
            HashSet<Decal> badCellFluxes = new HashSet<>();
            HashSet<Decal> zeroCellFluxes = new HashSet<>();
            for(Decal d : decals){
                if(d instanceof CellFluxDecal){
                    if(((CellFluxDecal)d).flux==0){
                        zeroCellFluxes.add(d);
                        continue;
                    }
                    for(Decal d2 : cellFluxes){
                        if(d2.x==d.x&&d2.y==d.y&&d2.z==d.z){
                            badCellFluxes.add(d2);
                        }
                    }
                    cellFluxes.add(d);
                }
            }
            for(Decal d : zeroCellFluxes){
                if(d instanceof CellFluxDecal){
                    for(Decal d2 : cellFluxes){
                        if(d2.x==d.x&&d2.y==d.y&&d2.z==d.z){
                            badCellFluxes.add(d);
                        }
                    }
                }
            }
            for(Iterator<Decal> it = decals.iterator(); it.hasNext();){
                Decal next = it.next();
                if(badCellFluxes.contains(next))it.remove();
            }
        }
    }.addMode("Initial", "Shows the first flux propogation, before inactive cells are excluded").addMode("Final", "Shows the final flux propogation, after inactive cells are excluded and calculation is complete").addMode("Shutdown", "Shows the flux propogation after all neutron shields are closed");    
    @Override
    public void getEditorOverlays(Multiblock multiblock, ArrayList overlays){
        if(multiblock instanceof OverhaulSFR){
            overlays.add(sfrActiveModeratorOverlay);
            overlays.add(sfrBlockRecipeOverlay);
            overlays.add(sfrPrimedCellOverlay);
            overlays.add(sfrClusterOverlay);
            overlays.add(sfrFluxOverlay);
        }
        if(multiblock instanceof OverhaulMSR){
            overlays.add(msrActiveModeratorOverlay);
            overlays.add(msrBlockRecipeOverlay);
            overlays.add(msrPrimedVesselOverlay);
            overlays.add(msrClusterOverlay);
            overlays.add(msrFluxOverlay);
        }
        if(multiblock instanceof OverhaulTurbine)overlays.add(validCoilOverlay);
    }
}