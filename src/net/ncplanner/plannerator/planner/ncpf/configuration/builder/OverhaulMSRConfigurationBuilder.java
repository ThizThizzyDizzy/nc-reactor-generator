package net.ncplanner.plannerator.planner.ncpf.configuration.builder;
import net.ncplanner.plannerator.multiblock.configuration.TextureManager;
import net.ncplanner.plannerator.ncpf.element.NCPFLegacyBlockElement;
import net.ncplanner.plannerator.ncpf.element.NCPFLegacyFluidElement;
import net.ncplanner.plannerator.ncpf.element.NCPFLegacyItemElement;
import net.ncplanner.plannerator.planner.StringUtil;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulMSRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Block;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockReference;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Fuel;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.HeaterRecipe;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.IrradiatorRecipe;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.PlacementRule;
import net.ncplanner.plannerator.planner.ncpf.module.AirModule;
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
    public OverhaulMSRConfigurationBuilder(String name, String version){
        configuration = new OverhaulMSRConfiguration();
        configuration.metadata.name = name;
        configuration.metadata.version = version;
        settings = configuration.settings;
    }
    public OverhaulMSRConfiguration build(){
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
        block.casing = new CasingModule(edge);
        return block;
    }
    public Block port(Block parent, String name, String displayName, String texture, String outputDisplayName, String outputTexture){
        Block in = block(name, displayName, texture);
        in.port = new PortModule();
        in.parent = parent;
        Block out = block(name, outputDisplayName, outputTexture);
        out.port = new PortModule();
        out.port.output = true;
        out.parent = parent;
        in.toggled = out;
        out.unToggled = in;
        parent.recipePorts = new RecipePortsModule();
        parent.recipePorts.input = new BlockReference(in);
        parent.recipePorts.output = new BlockReference(out);
        return in;
    }
    public Block source(String name, String displayName, String texture, float efficiency){
        Block block = block(name, StringUtil.superRemove(displayName, " Neutron Source"), texture);
        block.casing = new CasingModule();
        block.neutronSource = new NeutronSourceModule();
        block.neutronSource.efficiency = efficiency;
        return block;
    }
    public Block heater(String name, String displayName, String texture, String rules){
        Block block = block(name, displayName, texture);
        block.heater = new HeaterModule();
        block.heater.rules.add(parsePlacementRule(rules));
        return block;
    }
    public HeaterRecipe heaterRecipe(Block block, String inputName, String inputDisplayName, String inputTexture, String outputName, String outputDisplayName, String outputTexture, int inputRate, int outputRate, int cooling){
        HeaterRecipe recipe = new HeaterRecipe(new NCPFLegacyFluidElement(inputName));
        recipe.names.displayName = inputDisplayName;
        recipe.names.legacyNames.add(inputDisplayName);
        recipe.texture.texture = TextureManager.getImage(inputTexture);
        recipe.stats.cooling = cooling;
        block.heaterRecipes.add(recipe);
        return recipe;
    }
    public Block vessel(String name, String displayName, String texture){
        Block block = block(name, displayName, texture);
        block.fuelVessel = new FuelVesselModule();
        return block;
    }
    public Block irradiator(String name, String displayName, String texture){
        Block block = block(name, displayName, texture);
        block.irradiator = new IrradiatorModule();
        return block;
    }
    public Block conductor(String name, String displayName, String texture){
        Block block = block(name, displayName, texture);
        block.conductor = new ConductorModule();
        return block;
    }
    public Block moderator(String name, String displayName, String texture, int flux, float efficiency){
        Block block = block(name, displayName, texture);
        block.moderator = new ModeratorModule();
        block.moderator.flux = flux;
        block.moderator.efficiency = efficiency;
        return block;
    }
    public Block reflector(String name, String displayName, String texture, float efficiency, float reflectivity){
        Block block = block(name, displayName, texture);
        block.reflector = new ReflectorModule();
        block.reflector.efficiency = efficiency;
        block.reflector.reflectivity = reflectivity;
        return block;
    }
    public Block shield(String name, String displayName, String texture, String closedTexture, int heatPerFlux, float efficiency){
        Block block = block(name, displayName, texture);
        block.neutronShield = new NeutronShieldModule();
        block.neutronShield.heatPerFlux = heatPerFlux;
        block.neutronShield.efficiency = efficiency;
        Block closed = block(name, displayName, closedTexture);
        block.toggled = closed;
        closed.unToggled = block;
        return block;
    }
    
    public IrradiatorRecipe irradiatorRecipe(String inputName, String inputDisplayName, String inputTexture, String outputName, String outputDisplayName, String outputTexture, float efficiency, float heat){
        IrradiatorRecipe recipe = new IrradiatorRecipe(new NCPFLegacyItemElement(inputName));
        recipe.names.displayName = inputDisplayName;
        recipe.names.legacyNames.add(inputDisplayName);
        recipe.texture.texture = TextureManager.getImage(inputTexture);
        recipe.stats.efficiency = efficiency;
        recipe.stats.heat = heat;
        for(Block b : configuration.blocks)if(b.irradiator!=null)b.irradiatorRecipes.add(recipe);
        return recipe;
    }
    public Fuel fuel(String inputName, String inputDisplayName, String inputTexture, String outputName, String outputDisplayName, String outputTexture, float efficiency, int heat, int time, int criticality, boolean selfPriming){
        Fuel fuel = new Fuel(new NCPFLegacyFluidElement(inputName));
        fuel.names.displayName = inputDisplayName;
        fuel.names.legacyNames.add(inputDisplayName);
        fuel.texture.texture = TextureManager.getImage(inputTexture);
        fuel.stats.efficiency = efficiency;
        fuel.stats.heat = heat;
        fuel.stats.time = time;
        fuel.stats.criticality = criticality;
        fuel.stats.selfPriming = selfPriming;
        for(Block b : configuration.blocks)if(b.fuelVessel!=null)b.fuels.add(fuel);
        return fuel;
    }
    
    private PlacementRule parsePlacementRule(String rules){
        return new PlacementRule().parseNc(rules, PlacementRule::new, (str)->{
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
            Block block = null;
            int shortest = 0;
            str = StringUtil.superReplace(str, " heat heater", " heater", " heat sink", " sink");
            if(str.startsWith("water heater")||str.startsWith("water sink"))str = "standard"+str.substring("water".length());
            String[] strs = StringUtil.split(str, " ");
            if (strs.length != 2 || !(strs[1].startsWith("heater")||strs[1].startsWith("sink"))) {
                throw new IllegalArgumentException("Unknown rule bit: " + str);
            }
            for (Block b : configuration.blocks) {
                if (b.parent != null) continue;
                for (String s : b.names.legacyNames) {
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