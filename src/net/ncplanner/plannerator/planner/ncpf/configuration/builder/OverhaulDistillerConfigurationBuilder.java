package net.ncplanner.plannerator.planner.ncpf.configuration.builder;
import java.util.Arrays;
import net.ncplanner.plannerator.multiblock.configuration.TextureManager;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.element.NCPFLegacyBlockElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulDistillerConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulDistiller.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.module.LegacyNamesModule;
import net.ncplanner.plannerator.planner.ncpf.module.configuration.settings.OverhaulDistillerSettingsModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulDistiller.CasingModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulDistiller.ControllerModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulDistiller.LiquidDistributerModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulDistiller.PowerPortModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulDistiller.ProcessPortModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulDistiller.ReboilingUnitModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulDistiller.RefluxUnitModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulDistiller.SieveAssemblyModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulDistiller.SieveTrayModule;
public class OverhaulDistillerConfigurationBuilder extends ConfigurationBuilder<OverhaulDistillerConfiguration>{
    public OverhaulDistillerSettingsModule settings;
    public OverhaulDistillerConfigurationBuilder(String name, String version){
        super(new OverhaulDistillerConfiguration(), name, version);
        settings = configuration.settings;
    }
    public BlockBuilder block(String name, String displayName, String texture){
        return block(new NCPFLegacyBlockElement(name), displayName, texture);
    }
    public BlockBuilder block(NCPFElementDefinition definition, String displayName, String texture){
        BlockElement block = new BlockElement();
        block.definition = definition;
        block.names.displayName = displayName;
        block.texture.texture = TextureManager.getImage(texture);
        configuration.blocks.add(block);
        return new BlockBuilder(block);
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
        public BlockBuilder nbt(String tag){
            ((NCPFLegacyBlockElement)block.definition).nbt = tag;
            return this;
        }
        public BlockBuilder legacy(String... legacyNames){
            LegacyNamesModule module = block.getOrCreateModule(LegacyNamesModule::new);
            module.legacyNames.addAll(Arrays.asList(legacyNames));
            return this;
        }
        public BlockBuilder controller(){
            block.controller = new ControllerModule();
            return casing(false);
        }
        public BlockBuilder casing(boolean edge){
            block.casing = new CasingModule(edge);
            return this;
        }
        public BlockBuilder powerPort(){
            block.powerPort = new PowerPortModule();
            return casing(false);
        }
        public BlockBuilder processPort(int index){
            block.processPort = new ProcessPortModule();
            block.processPort.index = index;
            return casing(false).nbt("{setting:"+index+"}");
        }
        public BlockBuilder sieveTray(){
            block.sieveTray = new SieveTrayModule();
            return this;
        }
        public BlockBuilder refluxUnit(){
            block.refluxUnit = new RefluxUnitModule();
            return this;
        }
        public BlockBuilder reboilingUnit(){
            block.reboilingUnit = new ReboilingUnitModule();
            return this;
        }
        public BlockBuilder liquidDistributor(){
            block.liquidDistributer = new LiquidDistributerModule();
            return this;
        }
        public BlockBuilder sieve(float efficiency){
            block.sieveAssembly = new SieveAssemblyModule();
            block.sieveAssembly.efficiency = efficiency;
            return this;
        }
    }
}
