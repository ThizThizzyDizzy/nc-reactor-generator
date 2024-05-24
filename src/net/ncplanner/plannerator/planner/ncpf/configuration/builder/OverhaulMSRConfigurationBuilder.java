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
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulMSRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockReference;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Fuel;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.HeaterRecipe;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.IrradiatorRecipe;
import net.ncplanner.plannerator.planner.ncpf.module.AirModule;
import net.ncplanner.plannerator.planner.ncpf.module.LegacyNamesModule;
import net.ncplanner.plannerator.planner.ncpf.module.OverhaulMSRSettingsModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.CasingModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.ConductorModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.ControllerModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.FuelVesselModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.HeaterModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.IrradiatorModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.ModeratorModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.NeutronShieldModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.NeutronSourceModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.PortModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.RecipePortsModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.ReflectorModule;
public class OverhaulMSRConfigurationBuilder{
    private final OverhaulMSRConfiguration configuration;
    public OverhaulMSRSettingsModule settings;
    private HashMap<HeaterModule, ArrayList<String>> pendingRules = new HashMap<>();
    public OverhaulMSRConfigurationBuilder(String name, String version){
        configuration = new OverhaulMSRConfiguration();
        configuration.metadata.name = name;
        configuration.metadata.version = version;
        settings = configuration.settings = new OverhaulMSRSettingsModule();
    }
    public OverhaulMSRConfiguration build(){
        for(BlockElement b : configuration.blocks){
            b.withModule(LegacyNamesModule::new, (legacy)->{
                for(int i = 0; i<legacy.legacyNames.size(); i++){
                    for(int j = i+1; j<legacy.legacyNames.size(); j++){
                        if(legacy.legacyNames.get(j).equals(legacy.legacyNames.get(i)))legacy.legacyNames.remove(j);
                    }
                }
            });
        }
        for(HeaterModule heater : pendingRules.keySet()){
            for(String rule : pendingRules.get(heater))heater.rules.add(parsePlacementRule(rule).copyTo(NCPFPlacementRule::new));
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
        public BlockBuilder toggled(BlockBuilder toggled){
            block.toggled = toggled.block;
            toggled.block.unToggled = block;
            return this;
        }
        public BlockBuilder source(float efficiency){
            block.casing = new CasingModule();
            block.neutronSource = new NeutronSourceModule();
            block.neutronSource.efficiency = efficiency;
            return this;
        }
        public BlockBuilder heater(String rules){
            block.heater = new HeaterModule();
            if(!pendingRules.containsKey(block.heater))pendingRules.put(block.heater, new ArrayList<>());
            pendingRules.get(block.heater).add(rules);
            return this;
        }
        public BlockBuilder vessel(){
            block.fuelVessel = new FuelVesselModule();
            return this;
        }
        public BlockBuilder irradiator(){
            block.irradiator = new IrradiatorModule();
            return this;
        }
        public BlockBuilder conductor(){
            block.conductor = new ConductorModule();
            return this;
        }
        public BlockBuilder moderator(int flux, float efficiency){
            block.moderator = new ModeratorModule();
            block.moderator.flux = flux;
            block.moderator.efficiency = efficiency;
            return this;
        }
        public BlockBuilder reflector(float efficiency, float reflectivity){
            block.reflector = new ReflectorModule();
            block.reflector.efficiency = efficiency;
            block.reflector.reflectivity = reflectivity;
            return this;
        }
    }
    public void port(BlockElement parent, String name, String displayName, String texture, String outputDisplayName, String outputTexture, String type){
        BlockBuilder in = block(name, displayName, texture);
        in.blockstate("active", false);
        if(type!=null)in.blockstate("type", type);
        in.block.port = new PortModule();
        in.block.parent = parent;
        BlockBuilder out = block(name, outputDisplayName, outputTexture);
        out.blockstate("active", true);
        if(type!=null)out.blockstate("type", type);
        out.block.port = new PortModule();
        out.block.port.output = true;
        out.block.parent = parent;
        in.toggled(out);
        parent.recipePorts = new RecipePortsModule();
        parent.recipePorts.input = new BlockReference(in.block);
        parent.recipePorts.output = new BlockReference(out.block);
    }
    public HeaterRecipe heaterRecipe(BlockElement block, String inputName, String inputDisplayName, String inputTexture, String outputName, String outputDisplayName, String outputTexture, int inputRate, int outputRate, int cooling){
        HeaterRecipe recipe = new HeaterRecipe(new NCPFLegacyFluidElement(inputName));
        recipe.names.displayName = inputDisplayName;
        recipe.getOrCreateModule(LegacyNamesModule::new).legacyNames.add(inputDisplayName);
        recipe.texture.texture = TextureManager.getImage(inputTexture);
        recipe.stats.cooling = cooling;
        block.heaterRecipes.add(recipe);
        return recipe;
    }
    public void shield(String name, String type, String displayName, String texture, String closedTexture, int heatPerFlux, float efficiency){
        BlockBuilder block = block(name, displayName, texture);
        block.blockstate("type", type);
        block.blockstate("active", false);
        block.legacy(name+"[active=false]");
        block.block.neutronShield = new NeutronShieldModule();
        block.block.neutronShield.heatPerFlux = heatPerFlux;
        block.block.neutronShield.efficiency = efficiency;
        BlockBuilder closed = block(name, displayName, closedTexture);
        closed.blockstate("type", type);
        closed.blockstate("active", true);
        closed.legacy(name+"[active=true]");
        block.toggled(closed);
        block.block.neutronShield.closed = new BlockReference(closed.block);
    }
    public class IrradiatorRecipeBuilder{
        public final IrradiatorRecipe recipe;
        public IrradiatorRecipeBuilder(IrradiatorRecipe recipe){
            this.recipe = recipe;
        }
        public IrradiatorRecipeBuilder legacy(String... legacyNames){
            LegacyNamesModule module = recipe.getOrCreateModule(LegacyNamesModule::new);
            module.legacyNames.addAll(Arrays.asList(legacyNames));
            return this;
        }
    }
    public IrradiatorRecipeBuilder irradiatorRecipe(NCPFElementDefinition definition, String inputDisplayName, String inputTexture, float efficiency, float heat){
        IrradiatorRecipe recipe = new IrradiatorRecipe(definition);
        recipe.names.displayName = inputDisplayName;
        recipe.getOrCreateModule(LegacyNamesModule::new).legacyNames.add(inputDisplayName);
        recipe.texture.texture = TextureManager.getImage(inputTexture);
        recipe.stats.efficiency = efficiency;
        recipe.stats.heat = heat;
        for(BlockElement b : configuration.blocks)if(b.irradiator!=null)b.irradiatorRecipes.add(recipe);
        return new IrradiatorRecipeBuilder(recipe);
    }
    public Fuel fuel(String inputName, String inputDisplayName, String inputTexture, String outputName, String outputDisplayName, String outputTexture, float efficiency, int heat, int time, int criticality, boolean selfPriming){
        Fuel fuel = new Fuel(new NCPFLegacyFluidElement(inputName));
        fuel.names.displayName = inputDisplayName;
        fuel.getOrCreateModule(LegacyNamesModule::new).legacyNames.add(inputDisplayName);
        fuel.texture.texture = TextureManager.getImage(inputTexture);
        fuel.stats.efficiency = efficiency;
        fuel.stats.heat = heat;
        fuel.stats.time = time;
        fuel.stats.criticality = criticality;
        fuel.stats.selfPriming = selfPriming;
        for(BlockElement b : configuration.blocks)if(b.fuelVessel!=null)b.fuels.add(fuel);
        return fuel;
    }
    
    private NCPFPlacementRule parsePlacementRule(String rules){
        return new NCPFPlacementRule().parseNc(rules, NCPFPlacementRule::new, (str)->{
            if (str.startsWith("cell")) return FuelVesselModule::new;
            else if (str.startsWith("vessel")) return FuelVesselModule::new;
            else if (str.startsWith("moderator")) return ModeratorModule::new;
            else if (str.startsWith("reflector")) return ReflectorModule::new;
            else if (str.startsWith("casing")) return CasingModule::new;
            else if (str.startsWith("air")) return AirModule::new;
            else if (str.startsWith("conductor")) return ConductorModule::new;
            else if (str.startsWith("sink")) return HeaterModule::new;
            else if (str.startsWith("heater")) return HeaterModule::new;
            else if (str.startsWith("shield")) return NeutronShieldModule::new;
            else if (str.startsWith("irradiator")) return IrradiatorModule::new;
            else return null;
        }, (str)->{
            BlockElement block = null;
            int shortest = 0;
            str = StringUtil.superReplace(str, " heat heater", " heater", " heat sink", " sink");
            if(str.startsWith("water heater")||str.startsWith("water sink"))str = "standard"+str.substring("water".length());
            String[] strs = StringUtil.split(str, " ");
            if (strs.length != 2 || !(strs[1].startsWith("heater")||strs[1].startsWith("sink"))) {
                throw new IllegalArgumentException("Unknown rule bit: " + str);
            }
            for (BlockElement b : configuration.blocks) {
                if (b.parent != null) continue;
                LegacyNamesModule names = b.getModule(LegacyNamesModule::new);
                if(names!=null)for (String s : names.legacyNames) {
                    if(str.endsWith(" heater")||str.endsWith(" heaters")){
                        String withoutTheHeater = str.substring(0, str.indexOf(" heater"));
                        if(s.equals("nuclearcraft:salt_fission_heater_"+withoutTheHeater)){
                            return new BlockReference(b);
                        }
                    }
                    if(str.endsWith(" sink")||str.endsWith(" sinks")){
                        String withoutTheSink = str.substring(0, str.indexOf(" sink"));
                        if(s.equals("nuclearcraft:salt_fission_heater_"+withoutTheSink)){
                            return new BlockReference(b);
                        }
                    }
                    if (StringUtil.toLowerCase(s).contains("heater")
                            && StringUtil.matches(StringUtil.toLowerCase(s), "(\\s|^)?" + StringUtil.replace(StringUtil.toLowerCase(strs[0]), "_", "[_ ]") + "(\\s|$)?.*")) {
                        int len = s.length();
                        if (block == null || len < shortest) {
                            block = b;
                            shortest = len;
                        }
                    }
                }
            }
            if (block == null) throw new IllegalArgumentException("Could not find block matching rule bit " + str + "!");
            return new BlockReference(block);
        });
    }
}