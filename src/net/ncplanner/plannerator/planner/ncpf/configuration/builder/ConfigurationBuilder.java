package net.ncplanner.plannerator.planner.ncpf.configuration.builder;
import net.ncplanner.plannerator.multiblock.configuration.TextureManager;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.configuration.NCPFConfiguration;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.element.NCPFLegacyBlockElement;
import net.ncplanner.plannerator.ncpf.element.NCPFLegacyFluidElement;
import net.ncplanner.plannerator.ncpf.element.NCPFLegacyItemElement;
import net.ncplanner.plannerator.planner.ncpf.module.ConfigurationMetadataModule;
import net.ncplanner.plannerator.planner.ncpf.module.DisplayNameModule;
import net.ncplanner.plannerator.planner.ncpf.module.GlobalElementsModule;
import net.ncplanner.plannerator.planner.ncpf.module.TagsModule;
import net.ncplanner.plannerator.planner.ncpf.module.TextureModule;
public class ConfigurationBuilder<T extends NCPFConfiguration>{
    protected final T configuration;
    public ConfigurationBuilder(T configuration, String name, String version){
        this.configuration = configuration;
        configuration.init(false);
        configuration.withModule(ConfigurationMetadataModule::new, (metadata) -> {
            metadata.name = name;
            metadata.version = version;
        });
    }
    public T build(){
        return configuration;
    }
    public ElementBuilder globalElement(NCPFElementDefinition definition, String displayName, String texture){
        NCPFElement element = new NCPFElement(definition);
        element.withModuleOrCreate(DisplayNameModule::new, (m) -> m.displayName = displayName);
        element.withModuleOrCreate(TextureModule::new, (t) -> t.texture = TextureManager.getImage(texture));
        configuration.withModuleOrCreate(GlobalElementsModule::new, (m) -> m.elements.add(element));
        return new ElementBuilder(element);
    }
    public class ElementBuilder{
        public final NCPFElement element;
        public ElementBuilder(NCPFElement element){
            this.element = element;
        }
        public ElementBuilder oredict(String oredict){
            return tag(oredict);
        }
        public ElementBuilder tag(String tag){
            element.withModuleOrCreate(TagsModule::new, (t) -> t.tags.add(tag));
            return this;
        }
        public NCPFElement build(){
            return element;
        }
    }
    public NCPFLegacyBlockElementBuilder legacyBlock(String name){
        return new NCPFLegacyBlockElementBuilder(new NCPFLegacyBlockElement(name));
    }
    public class NCPFLegacyBlockElementBuilder{
        public final NCPFLegacyBlockElement definition;
        public NCPFLegacyBlockElementBuilder(NCPFLegacyBlockElement definition){
            this.definition = definition;
        }
        public NCPFLegacyBlockElementBuilder metadata(int metadata){
            definition.metadata = metadata;
            return this;
        }
        public NCPFLegacyBlockElementBuilder blockstate(String key, Object value){
            definition.blockstate.put(key, value);
            return this;
        }
        public NCPFLegacyBlockElement build(){
            return definition;
        }
    }
    public NCPFLegacyItemElementBuilder legacyItem(String name){
        return new NCPFLegacyItemElementBuilder(new NCPFLegacyItemElement(name));
    }
    public class NCPFLegacyItemElementBuilder{
        public final NCPFLegacyItemElement definition;
        public NCPFLegacyItemElementBuilder(NCPFLegacyItemElement definition){
            this.definition = definition;
        }
        public NCPFLegacyItemElementBuilder metadata(int metadata){
            definition.metadata = metadata;
            return this;
        }
        public NCPFLegacyItemElement build(){
            return definition;
        }
    }
    public NCPFLegacyFluidElementBuilder legacyFluid(String name){
        return new NCPFLegacyFluidElementBuilder(new NCPFLegacyFluidElement(name));
    }
    public class NCPFLegacyFluidElementBuilder{
        public final NCPFLegacyFluidElement definition;
        public NCPFLegacyFluidElementBuilder(NCPFLegacyFluidElement definition){
            this.definition = definition;
        }
        public NCPFLegacyFluidElement build(){
            return definition;
        }
    }
}
