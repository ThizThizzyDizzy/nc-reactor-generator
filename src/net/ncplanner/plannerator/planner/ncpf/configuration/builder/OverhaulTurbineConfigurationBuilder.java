package net.ncplanner.plannerator.planner.ncpf.configuration.builder;
import net.ncplanner.plannerator.multiblock.configuration.TextureManager;
import net.ncplanner.plannerator.ncpf.element.NCPFLegacyBlockElement;
import net.ncplanner.plannerator.ncpf.element.NCPFLegacyFluidElement;
import net.ncplanner.plannerator.planner.StringUtil;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulTurbineConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.Block;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.BlockReference;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.PlacementRule;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.Recipe;
import net.ncplanner.plannerator.planner.ncpf.module.OverhaulTurbineSettingsModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.BearingModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.BladeModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.CasingModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.CoilModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.ConnectorModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.ControllerModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.InletModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.OutletModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.ShaftModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.StatorModule;
public class OverhaulTurbineConfigurationBuilder{
    private final OverhaulTurbineConfiguration configuration;
    public OverhaulTurbineSettingsModule settings;
    public OverhaulTurbineConfigurationBuilder(String name, String version){
        configuration = new OverhaulTurbineConfiguration();
        configuration.metadata.name = name;
        configuration.metadata.version = version;
        settings = configuration.settings;
    }
    public OverhaulTurbineConfiguration build(){
        return configuration;
    }
    public Block block(String name, String displayName, String texture){
        Block block = new Block(new NCPFLegacyBlockElement(name));
        block.names.displayName = displayName;
        block.names.legacyNames.add(displayName);
        block.texture.texture = TextureManager.getImage(texture);
        configuration.blocks.add(block);
        return block;
    }
    public Block controller(String name, String displayName, String texture){
        Block block = block(name, displayName, texture);
        block.controller = new ControllerModule();
        block.casing = new CasingModule();
        return block;
    }
    public Block casing(String name, String displayName, String texture, boolean edge){
        Block block = block(name, displayName, texture);
        block.casing = new CasingModule(true);
        return block;
    }
    public Block inlet(String name, String displayName, String texture){
        Block block = block(name, displayName, texture);
        block.inlet = new InletModule();
        return block;
    }
    public Block outlet(String name, String displayName, String texture){
        Block block = block(name, displayName, texture);
        block.outlet = new OutletModule();
        return block;
    }
    public Block coil(String name, String displayName, String texture, float efficiency, String rules){
        Block coil = block(name, displayName, texture);
        coil.coil = new CoilModule();
        coil.coil.efficiency = efficiency;
        coil.coil.rules.add(parsePlacementRule(rules));
        return coil;
    }
    public Block bearing(String name, String displayName, String texture){
        Block bearing = block(name, displayName, texture);
        bearing.bearing = new BearingModule();
        return bearing;
    }
    public Block connector(String name, String displayName, String texture, String rules){
        Block connector = block(name, displayName, texture);
        connector.connector = new ConnectorModule();
        connector.connector.rules.add(parsePlacementRule(rules));
        return connector;
    }
    public Block blade(String name, String displayName, String texture, float efficiency, float expansion){
        Block blade = block(name, displayName, texture);
        blade.blade = new BladeModule();
        blade.blade.efficiency = efficiency;
        blade.blade.expansion = expansion;
        return blade;
    }
    public Block stator(String name, String displayName, String texture, float expansion){
        Block blade = block(name, displayName, texture);
        blade.stator = new StatorModule();
        blade.stator.expansion = expansion;
        return blade;
    }
    public Block shaft(String name, String displayName, String texture){
        Block shaft = block(name, displayName, texture);
        shaft.shaft = new ShaftModule();
        return shaft;
    }
    
    public static Recipe recipe(String inputName, String inputDisplayName, String inputTexture, String outputName, String outputDisplayName, String outputTexture, double power, double coefficient){
        Recipe recipe = new Recipe(new NCPFLegacyFluidElement(inputName));
        recipe.stats.power = power;
        recipe.stats.coefficient = coefficient;
        recipe.names.displayName = inputDisplayName;
        recipe.names.legacyNames.add(inputDisplayName);
        recipe.texture.texture = TextureManager.getImage(inputTexture);
        return recipe;
    }
    
    private PlacementRule parsePlacementRule(String rules){
        return new PlacementRule().parseNc(rules, PlacementRule::new, (str)->{
            if(str.startsWith("coil")) return CoilModule::new;
            else if(str.startsWith("bearing")) return BearingModule::new;
            else if(str.startsWith("connector")) return ConnectorModule::new;
            else if(str.startsWith("casing")) return CasingModule::new;
            else return null;
        }, (str)->{
            Block block = null;
            int shortest = 0;
            String[] strs = StringUtil.split(str, " ");
            if(strs.length!=2||!strs[1].startsWith("coil")){
                throw new IllegalArgumentException("Unknown rule bit: "+str);
            }
            for(Block b : configuration.blocks){
                for(String s : b.names.legacyNames){
                    if(str.endsWith(" coil")||str.endsWith(" coils")){
                        String withoutTheCoil = str.substring(0, str.indexOf(" coil"));
                        if(s.equals("nuclearcraft:turbine_dynamo_coil_"+withoutTheCoil)){
                            return new BlockReference(b);
                        }
                    }
                    if(StringUtil.toLowerCase(s).contains("coil")&&StringUtil.matches(StringUtil.toLowerCase(s), "(\\s|^)?"+StringUtil.replace(StringUtil.toLowerCase(strs[0]), "_", "[_ ]")+"(\\s|$)?.*")){
                        int len = s.length();
                        if(block==null||len<shortest){
                            block = b;
                            shortest = len;
                        }
                    }
                }
            }
            if(block==null)throw new IllegalArgumentException("Could not find block matching rule bit "+str+"!");
            return new BlockReference(block);
        });
    }
}