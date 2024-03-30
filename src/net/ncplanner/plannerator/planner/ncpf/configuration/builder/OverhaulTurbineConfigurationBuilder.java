package net.ncplanner.plannerator.planner.ncpf.configuration.builder;
import java.util.ArrayList;
import java.util.HashMap;
import net.ncplanner.plannerator.multiblock.configuration.TextureManager;
import net.ncplanner.plannerator.ncpf.NCPFPlacementRule;
import net.ncplanner.plannerator.ncpf.element.NCPFLegacyBlockElement;
import net.ncplanner.plannerator.ncpf.element.NCPFLegacyFluidElement;
import net.ncplanner.plannerator.planner.StringUtil;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulTurbineConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.BlockReference;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.Recipe;
import net.ncplanner.plannerator.planner.ncpf.module.LegacyNamesModule;
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
    private HashMap<CoilModule, ArrayList<String>> pendingCoilRules = new HashMap<>();
    private HashMap<ConnectorModule, ArrayList<String>> pendingConnectorRules = new HashMap<>();
    public OverhaulTurbineConfigurationBuilder(String name, String version){
        configuration = new OverhaulTurbineConfiguration();
        configuration.metadata.name = name;
        configuration.metadata.version = version;
        settings = configuration.settings = new OverhaulTurbineSettingsModule();
    }
    public OverhaulTurbineConfiguration build(){
        for(CoilModule coil : pendingCoilRules.keySet()){
            for(String rule : pendingCoilRules.get(coil))coil.rules.add(parsePlacementRule(rule));
        }
        for(ConnectorModule connector : pendingConnectorRules.keySet()){
            for(String rule : pendingConnectorRules.get(connector))connector.rules.add(parsePlacementRule(rule));
        }
        return configuration;
    }
    public BlockElement block(String name, String displayName, String texture){
        BlockElement block = new BlockElement(new NCPFLegacyBlockElement(name));
        block.names.displayName = displayName;
        block.getOrCreateModule(LegacyNamesModule::new).legacyNames.add(displayName);
        block.texture.texture = TextureManager.getImage(texture);
        configuration.blocks.add(block);
        return block;
    }
    public BlockElement controller(String name, String displayName, String texture){
        BlockElement block = block(name, displayName, texture);
        block.controller = new ControllerModule();
        block.casing = new CasingModule();
        return block;
    }
    public BlockElement casing(String name, String displayName, String texture, boolean edge){
        BlockElement block = block(name, displayName, texture);
        block.casing = new CasingModule(true);
        return block;
    }
    public BlockElement inlet(String name, String displayName, String texture){
        BlockElement block = block(name, displayName, texture);
        block.inlet = new InletModule();
        return block;
    }
    public BlockElement outlet(String name, String displayName, String texture){
        BlockElement block = block(name, displayName, texture);
        block.outlet = new OutletModule();
        return block;
    }
    public BlockElement coil(String name, String displayName, String texture, float efficiency, String rules){
        BlockElement coil = block(name, displayName, texture);
        coil.coil = new CoilModule();
        coil.coil.efficiency = efficiency;
        if(!pendingCoilRules.containsKey(coil.coil))pendingCoilRules.put(coil.coil, new ArrayList<>());
        pendingCoilRules.get(coil.coil).add(rules);
        return coil;
    }
    public BlockElement bearing(String name, String displayName, String texture){
        BlockElement bearing = block(name, displayName, texture);
        bearing.bearing = new BearingModule();
        return bearing;
    }
    public BlockElement connector(String name, String displayName, String texture, String rules){
        BlockElement connector = block(name, displayName, texture);
        connector.connector = new ConnectorModule();
        if(!pendingConnectorRules.containsKey(connector.connector))pendingConnectorRules.put(connector.connector, new ArrayList<>());
        pendingConnectorRules.get(connector.connector).add(rules);
        return connector;
    }
    public BlockElement blade(String name, String displayName, String texture, float efficiency, float expansion){
        BlockElement blade = block(name, displayName, texture);
        blade.blade = new BladeModule();
        blade.blade.efficiency = efficiency;
        blade.blade.expansion = expansion;
        return blade;
    }
    public BlockElement stator(String name, String displayName, String texture, float expansion){
        BlockElement blade = block(name, displayName, texture);
        blade.stator = new StatorModule();
        blade.stator.expansion = expansion;
        return blade;
    }
    public BlockElement shaft(String name, String displayName, String texture){
        BlockElement shaft = block(name, displayName, texture);
        shaft.shaft = new ShaftModule();
        return shaft;
    }
    
    public static Recipe recipe(String inputName, String inputDisplayName, String inputTexture, String outputName, String outputDisplayName, String outputTexture, double power, double coefficient){
        Recipe recipe = new Recipe(new NCPFLegacyFluidElement(inputName));
        recipe.stats.power = power;
        recipe.stats.coefficient = coefficient;
        recipe.names.displayName = inputDisplayName;
        recipe.getOrCreateModule(LegacyNamesModule::new).legacyNames.add(inputDisplayName);
        recipe.texture.texture = TextureManager.getImage(inputTexture);
        return recipe;
    }
    
    private NCPFPlacementRule parsePlacementRule(String rules){
        return new NCPFPlacementRule().parseNc(rules, NCPFPlacementRule::new, (str)->{
            if(str.startsWith("coil")) return CoilModule::new;
            else if(str.startsWith("bearing")) return BearingModule::new;
            else if(str.startsWith("connector")) return ConnectorModule::new;
            else if(str.startsWith("casing")) return CasingModule::new;
            else return null;
        }, (str)->{
            BlockElement block = null;
            int shortest = 0;
            String[] strs = StringUtil.split(str, " ");
            if(strs.length!=2||!strs[1].startsWith("coil")){
                throw new IllegalArgumentException("Unknown rule bit: "+str);
            }
            for(BlockElement b : configuration.blocks){
                LegacyNamesModule names = b.getModule(LegacyNamesModule::new);
                if(names!=null)for(String s : names.legacyNames){
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