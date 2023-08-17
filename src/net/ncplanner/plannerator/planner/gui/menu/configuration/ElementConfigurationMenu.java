package net.ncplanner.plannerator.planner.gui.menu.configuration;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.configuration.NCPFConfiguration;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.element.NCPFSettingsElement;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.Panel;
import net.ncplanner.plannerator.planner.gui.menu.component.TextBox;
import net.ncplanner.plannerator.planner.gui.menu.component.TextureButton;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.GridLayout;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.ListLayout;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.SplitLayout;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuPickElementDefinition;
import net.ncplanner.plannerator.planner.ncpf.module.DisplayNamesModule;
import net.ncplanner.plannerator.planner.ncpf.module.TextureModule;
import static net.ncplanner.plannerator.ncpf.element.NCPFSettingsElement.Type;
public class ElementConfigurationMenu extends ConfigurationMenu{
    private final NCPFConfiguration config;
    private final NCPFElement element;
    public ElementConfigurationMenu(Menu parent, NCPFConfigurationContainer configuration, NCPFConfiguration config, NCPFElement element){
        super(parent, configuration, element.getDisplayName(), new SplitLayout(SplitLayout.Y_AXIS, 0, 192, 0));
        this.config = config;
        this.element = element;
        SplitLayout definition = add(new SplitLayout(SplitLayout.X_AXIS, 0, 192, 0));
        definition.add(new TextureButton(()->element.getOrCreateModule(TextureModule::new).texture, (img)->element.getOrCreateModule(TextureModule::new).texture = img));
        SplitLayout definitionList = definition.add(new SplitLayout(SplitLayout.Y_AXIS, 0, 48, 0));
        SplitLayout definitionHeader = definitionList.add(new SplitLayout(SplitLayout.X_AXIS, 0.3f));
        definitionHeader.add(new Button(element.definition.getTypeName(), true).addAction(() -> {
            new MenuPickElementDefinition(gui, this, (def) -> {
                element.definition = def;
                gui.open(new ElementConfigurationMenu(parent, configuration, config, element));
            }).open();
        }));
        TextBox displayName = definitionHeader.add(new TextBox(element.getOrCreateModule(DisplayNamesModule::new).displayName, true, "Display Name").onChange((t) -> {
            element.getOrCreateModule(DisplayNamesModule::new).displayName = t;
        }));
        if(element.definition instanceof NCPFSettingsElement){
            NCPFSettingsElement def = (NCPFSettingsElement)element.definition;
            boolean hasBlockstate = false;
            boolean hasMetadata = false;
            for(Type type : def.types.values()){
                if(type==Type.METADATA)hasMetadata = true;
                if(type==Type.BLOCKSTATE)hasBlockstate = true;
            }
            SplitLayout definitionFields = definitionList.add(new SplitLayout(SplitLayout.X_AXIS, hasBlockstate?0.5f:1));
            ListLayout defFields = definitionFields.add(new ListLayout(48));
            for(String setting : def.settings){
                if(def.types.get(setting)==Type.METADATA||def.types.get(setting)==Type.BLOCKSTATE)continue;
                Label label = new Label("TODO "+setting);
                if(hasMetadata){
                    SplitLayout line = defFields.add(new SplitLayout(SplitLayout.X_AXIS, 0.7f));
                    line.add(label);
                    line.add(new Label("TODO Metadata"));
                    hasMetadata = false;
                }else{
                    defFields.add(label);
                }
            }
            if(hasBlockstate){
                definitionFields.add(new Label("TODO Blockstate"));
            }else definitionFields.add(new Panel());
        }else definitionList.add(new Panel());
        SplitLayout settings = add(new SplitLayout(SplitLayout.Y_AXIS, 0.5f));
        GridLayout moduleLists = settings.add(new GridLayout(0, 1));
        //TODO add modules
        GridLayout lists = settings.add(new GridLayout(0, 1));//block recipes
        //TODO add lists
    }
}