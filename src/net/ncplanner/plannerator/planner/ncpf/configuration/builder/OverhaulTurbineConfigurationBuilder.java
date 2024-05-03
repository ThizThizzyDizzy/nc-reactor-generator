package net.ncplanner.plannerator.planner.ncpf.configuration.builder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import net.ncplanner.plannerator.multiblock.configuration.TextureManager;
import net.ncplanner.plannerator.ncpf.NCPFPlacementRule;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
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
        for(BlockElement b : configuration.blocks){
            b.withModule(LegacyNamesModule::new, (legacy)->{
                for(int i = 0; i<legacy.legacyNames.size(); i++){
                    for(int j = i+1; j<legacy.legacyNames.size(); j++){
                        if(legacy.legacyNames.get(j).equals(legacy.legacyNames.get(i)))legacy.legacyNames.remove(j);
                    }
                }
            });
        }
        for(CoilModule coil : pendingCoilRules.keySet()){
            for(String rule : pendingCoilRules.get(coil))coil.rules.add(parsePlacementRule(rule));
        }
        for(ConnectorModule connector : pendingConnectorRules.keySet()){
            for(String rule : pendingConnectorRules.get(connector))connector.rules.add(parsePlacementRule(rule));
        }
        return configuration;
    }
    public BlockBuilder block(String name, String displayName, String texture){
        return block(new NCPFLegacyBlockElement(name), displayName, texture);
    }
    public BlockBuilder block(NCPFElementDefinition definition, String displayName, String texture){
        BlockElement block = new BlockElement(definition);
        block.names.displayName = displayName;
        block.getOrCreateModule(LegacyNamesModule::new).legacyNames.add(displayName);
        block.texture.texture = TextureManager.getImage(texture);
        configuration.blocks.add(block);
        return new BlockBuilder(block).legacy(displayName);
    }
    public class BlockBuilder{
        public final BlockElement block;
        public BlockBuilder(BlockElement block){
            this.block = block;
        }
        public BlockBuilder blockstate(String key, Object value){
            ((NCPFLegacyBlockElement)block.definition).blockstate.put(key, value);
            return this;
        }
        public BlockBuilder legacy(String... legacyNames){
            LegacyNamesModule module = block.getOrCreateModule(LegacyNamesModule::new);
            module.legacyNames.addAll(Arrays.asList(legacyNames));
            return this;
        }
        public BlockBuilder controller(){
            block.controller = new ControllerModule();
            block.casing = new CasingModule();
            return this;
        }
        public BlockBuilder casing(boolean edge){
            block.casing = new CasingModule(edge);
            return this;
        }
        public BlockBuilder inlet(){
            block.inlet = new InletModule();
            return this;
        }
        public BlockBuilder outlet(){
            block.outlet = new OutletModule();
            return this;
        }
        public BlockBuilder coil(float efficiency, String rules){
            block.coil = new CoilModule();
            block.coil.efficiency = efficiency;
            if(!pendingCoilRules.containsKey(block.coil))pendingCoilRules.put(block.coil, new ArrayList<>());
            pendingCoilRules.get(block.coil).add(rules);
            return this;
        }
        public BlockBuilder bearing(){
            block.bearing = new BearingModule();
            return this;
        }
        public BlockBuilder connector(String rules){
            block.connector = new ConnectorModule();
            if(!pendingConnectorRules.containsKey(block.connector))pendingConnectorRules.put(block.connector, new ArrayList<>());
            pendingConnectorRules.get(block.connector).add(rules);
            return this;
        }
        public BlockBuilder blade(float efficiency, float expansion){
            block.blade = new BladeModule();
            block.blade.efficiency = efficiency;
            block.blade.expansion = expansion;
            return this;
        }
        public BlockBuilder stator(float expansion){
            block.stator = new StatorModule();
            block.stator.expansion = expansion;
            return this;
        }
        public BlockBuilder shaft(){
            block.shaft = new ShaftModule();
            return this;
        }
    }
    
    public Recipe recipe(String inputName, String inputDisplayName, String inputTexture, String outputName, String outputDisplayName, String outputTexture, double power, double coefficient){
        Recipe recipe = new Recipe(new NCPFLegacyFluidElement(inputName));
        recipe.stats.power = power;
        recipe.stats.coefficient = coefficient;
        recipe.names.displayName = inputDisplayName;
        recipe.getOrCreateModule(LegacyNamesModule::new).legacyNames.add(inputDisplayName);
        recipe.texture.texture = TextureManager.getImage(inputTexture);
        configuration.recipes.add(recipe);
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