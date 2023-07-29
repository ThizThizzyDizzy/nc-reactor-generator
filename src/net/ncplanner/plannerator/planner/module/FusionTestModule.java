package net.ncplanner.plannerator.planner.module;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.overhaul.fusion.OverhaulFusionReactor;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.editor.overlay.EditorOverlay;
import net.ncplanner.plannerator.planner.file.FileReader;
import net.ncplanner.plannerator.planner.ncpf.Configurations;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulFusionConfiguration;
import net.ncplanner.plannerator.planner.ncpf.design.OverhaulFusionDefinition;
import net.ncplanner.plannerator.planner.ncpf.design.OverhaulFusionDesign;
import net.ncplanner.plannerator.planner.ncpf.module.OverhaulFusionSettingsModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.*;
public class FusionTestModule extends Module{
    public FusionTestModule(){
        super("fusion_test");
    }
    @Override
    public String getDisplayName(){
        return "Fusion Test";
    }
    @Override
    public String getDescription(){
        return "A testbed for future overhaul fusion reactors.";
    }
    @Override
    public void addMultiblockTypes(ArrayList multiblockTypes){
        multiblockTypes.add(new OverhaulFusionReactor());
    }
    @Override
    public void registerNCPF(){
        registerNCPFConfiguration(OverhaulFusionConfiguration::new);
//        registerNCPFDesign(OverhaulFusionDefinition::new, OverhaulFusionDesign::new);
        
        registerNCPFModule(OverhaulFusionSettingsModule::new);
        registerNCPFModule(BreedingBlanketModule::new);
        registerNCPFModule(BreedingBlanketStatsModule::new);
        registerNCPFModule(ConductorModule::new);
        registerNCPFModule(ConnectorModule::new);
        registerNCPFModule(CoolantRecipeStatsModule::new);
        registerNCPFModule(net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.CoreModule::new);
        registerNCPFModule(HeatingBlanketModule::new);
        registerNCPFModule(HeatsinkModule::new);
        registerNCPFModule(PoloidalElectromagnetModule::new);
        registerNCPFModule(RecipeStatsModule::new);
        registerNCPFModule(ReflectorModule::new);
        registerNCPFModule(ShieldingModule::new);
        registerNCPFModule(ToroidalElectromagnetModule::new);
    }
    @Override
    public void addConfigurations(){
        Configurations.configurations.add(FileReader.read(() -> {
            return Core.getInputStream("configurations/fusion_test.ncpf");
        }).configuration);//not using addConfiguration, because you shouldn't auto-load into fusion test
    }
    private final EditorOverlay<net.ncplanner.plannerator.multiblock.overhaul.fusion.Block> augmentedBlanketOverlay = new EditorOverlay<net.ncplanner.plannerator.multiblock.overhaul.fusion.Block>("Augmente4d Breeding Blanket", "Highlights augmented breeding blankets with a green outline", true){
        @Override
        public void render(Renderer renderer, float x, float y, float width, float height, net.ncplanner.plannerator.multiblock.overhaul.fusion.Block block, Multiblock<net.ncplanner.plannerator.multiblock.overhaul.fusion.Block> multiblock){
            if(block.isBreedingBlanketAugmented()){
                block.drawOutline(renderer, x, y, width, height, Core.theme.getBlockColorOutlineActive());
            }
        }
    };
    private final EditorOverlay<net.ncplanner.plannerator.multiblock.overhaul.fusion.Block> clusterOverlay = new EditorOverlay<net.ncplanner.plannerator.multiblock.overhaul.fusion.Block>("Clusters", "Outlines all clusters in the reactor", true){
        @Override
        public void render(Renderer renderer, float x, float y, float width, float height, net.ncplanner.plannerator.multiblock.overhaul.fusion.Block block, Multiblock multiblock){
            net.ncplanner.plannerator.multiblock.overhaul.fusion.Block b = (net.ncplanner.plannerator.multiblock.overhaul.fusion.Block)block;
            OverhaulFusionReactor.Cluster cluster = b.cluster;
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
    @Override
    public void getEditorOverlays(Multiblock multiblock, ArrayList overlays){
        if(multiblock instanceof OverhaulFusionReactor){
            overlays.add(augmentedBlanketOverlay);
            overlays.add(clusterOverlay);
        }
    }
}