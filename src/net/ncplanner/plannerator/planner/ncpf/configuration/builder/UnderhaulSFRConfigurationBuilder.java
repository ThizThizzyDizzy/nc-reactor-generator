package net.ncplanner.plannerator.planner.ncpf.configuration.builder;
import java.util.Arrays;
import java.util.function.Supplier;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.multiblock.configuration.TextureManager;
import net.ncplanner.plannerator.ncpf.NCPFModuleReference;
import net.ncplanner.plannerator.ncpf.NCPFPlacementRule;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.element.NCPFLegacyBlockElement;
import net.ncplanner.plannerator.ncpf.element.NCPFLegacyFluidElement;
import net.ncplanner.plannerator.ncpf.element.NCPFLegacyItemElement;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.ncpf.configuration.UnderhaulSFRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.ActiveCoolerRecipe;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.BlockReference;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.Fuel;
import net.ncplanner.plannerator.planner.ncpf.module.LegacyNamesModule;
import net.ncplanner.plannerator.planner.ncpf.module.UnderhaulSFRSettingsModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.ActiveCoolerModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.CasingModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.ControllerModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.CoolerModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.FuelCellModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.ModeratorModule;
public class UnderhaulSFRConfigurationBuilder{
    private final UnderhaulSFRConfiguration configuration;
    public UnderhaulSFRSettingsModule settings;
    private BlockElement activeCooler;
    public UnderhaulSFRConfigurationBuilder(String name, String version){
        configuration = new UnderhaulSFRConfiguration();
        configuration.metadata.name = name;
        configuration.metadata.version = version;
        settings = configuration.settings = new UnderhaulSFRSettingsModule();
    }
    public UnderhaulSFRConfiguration build(){
        return configuration;
    }
    public BlockBuilder block(String name, String displayName, String texture){
        return block(new NCPFLegacyBlockElement(name), displayName, texture);
    }
    public BlockBuilder block(NCPFElementDefinition definition, String displayName, String texture){
        BlockElement block = new BlockElement(definition);
        block.names.displayName = displayName;
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
            return this;
        }
        public BlockBuilder casing(){
            block.casing = new CasingModule();
            return this;
        }
        public BlockBuilder cell(){
            block.fuelCell = new FuelCellModule();
            return this;
        }
        public BlockBuilder moderator(){
            block.moderator = new ModeratorModule();
            return this;
        }
        public BlockBuilder activeCooler(){
            block.activeCooler = new ActiveCoolerModule();
            activeCooler = block;
            return this;
        }
        public BlockBuilder cooler(int cooling, NCPFPlacementRule... rules){
            block.cooler = new CoolerModule();
            block.cooler.cooling = cooling;
            block.cooler.rules.addAll(Arrays.asList(rules));
            return this;
        }
    }
    public class ActiveCoolerRecipeBuilder{
        public final ActiveCoolerRecipe recipe;
        public ActiveCoolerRecipeBuilder(ActiveCoolerRecipe recipe){
            this.recipe = recipe;
        }
        public ActiveCoolerRecipeBuilder legacy(String... legacyNames){
            LegacyNamesModule module = recipe.getOrCreateModule(LegacyNamesModule::new);
            module.legacyNames.addAll(Arrays.asList(legacyNames));
            return this;
        }
        public ActiveCoolerRecipeBuilder texture(String texture){
            recipe.texture.texture = TextureManager.getImage(texture);
            return this;
        }
        public ActiveCoolerRecipeBuilder texture(String template, int tint){
            recipe.texture.texture = TextureManager.generateTexture(template, new Color(tint|0xff000000));
            return this;
        }
    }
    public ActiveCoolerRecipeBuilder activeRecipe(int cooling, String liquid, String displayName, NCPFPlacementRule... rules){
        ActiveCoolerRecipe recipe = new ActiveCoolerRecipe(new NCPFLegacyFluidElement(liquid));
        recipe.names.displayName = displayName;
        recipe.stats.cooling = cooling;
        for(NCPFPlacementRule r : rules){
            recipe.stats.rules.add(r);
        }
        activeCooler.activeCoolerRecipes.add(recipe);
        return new ActiveCoolerRecipeBuilder(recipe);
    }
    
    public NCPFPlacementRule atLeast(int min, Supplier<NCPFModule> block){
        NCPFPlacementRule rule = new NCPFPlacementRule();
        rule.rule = NCPFPlacementRule.RuleType.BETWEEN;
        rule.target = new NCPFModuleReference(block);
        rule.min = (byte)Math.min(6,Math.max(1,min));
        rule.max = 6;
        return rule;
    }
    public NCPFPlacementRule atLeast(int min, BlockElement block){
        NCPFPlacementRule rule = new NCPFPlacementRule();
        rule.rule = NCPFPlacementRule.RuleType.BETWEEN;
        rule.target = new BlockReference(block);
        rule.min = (byte)Math.min(6,Math.max(1,min));
        rule.max = 6;
        return rule;
    }
    public NCPFPlacementRule exactly(int num, Supplier<NCPFModule> block){
        NCPFPlacementRule rule = new NCPFPlacementRule();
        rule.rule = NCPFPlacementRule.RuleType.BETWEEN;
        rule.target = new NCPFModuleReference(block);
        rule.min = rule.max = (byte)Math.min(6,Math.max(1,num));
        return rule;
    }
    public NCPFPlacementRule exactly(int num, BlockElement block){
        NCPFPlacementRule rule = new NCPFPlacementRule();
        rule.rule = NCPFPlacementRule.RuleType.BETWEEN;
        rule.target = new BlockReference(block);
        rule.min = rule.max = (byte)Math.min(6,Math.max(1,num));
        return rule;
    }
    public NCPFPlacementRule axis(Supplier<NCPFModule> block){
        NCPFPlacementRule rule = new NCPFPlacementRule();
        rule.rule = NCPFPlacementRule.RuleType.AXIAL;
        rule.target = new NCPFModuleReference(block);
        rule.min = 1;
        rule.max = 3;
        return rule;
    }
    public NCPFPlacementRule axis(BlockElement block){
        NCPFPlacementRule rule = new NCPFPlacementRule();
        rule.rule = NCPFPlacementRule.RuleType.AXIAL;
        rule.target = new BlockReference(block);
        rule.min = 1;
        rule.max = 3;
        return rule;
    }
    public NCPFPlacementRule or(NCPFPlacementRule... rules){
        NCPFPlacementRule rule = new NCPFPlacementRule();
        rule.rule = NCPFPlacementRule.RuleType.OR;
        for(NCPFPlacementRule r : rules){
            rule.rules.add(r);
        }
        return rule;
    }
    public NCPFPlacementRule and(NCPFPlacementRule... rules){
        NCPFPlacementRule rule = new NCPFPlacementRule();
        rule.rule = NCPFPlacementRule.RuleType.AND;
        for(NCPFPlacementRule r : rules){
            rule.rules.add(r);
        }
        return rule;
    }
    public NCPFPlacementRule vertex(Supplier<NCPFModule> block){
        NCPFPlacementRule rule = new NCPFPlacementRule();
        rule.rule = NCPFPlacementRule.RuleType.VERTEX;
        rule.target = new NCPFModuleReference(block);
        return rule;
    }
    public Fuel fuel(String name, String displayName, float power, float heat, int time, String texture){
        Fuel fuel = new Fuel(new NCPFLegacyItemElement(name));
        fuel.stats.power = power;
        fuel.stats.heat = heat;
        fuel.stats.time = time;
        fuel.names.displayName = displayName;
        fuel.getOrCreateModule(LegacyNamesModule::new).legacyNames.add(displayName);
        fuel.texture.texture = TextureManager.getImage(texture);
        configuration.fuels.add(fuel);
        return fuel;
    }
}