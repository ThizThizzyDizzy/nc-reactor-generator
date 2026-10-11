package net.ncplanner.plannerator.planner.gui.menu.configuration;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.NCPFModuleContainer;
import net.ncplanner.plannerator.ncpf.element.NCPFModuleElement;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.IconButton;
import net.ncplanner.plannerator.planner.gui.menu.component.LayoutPanel;
import net.ncplanner.plannerator.planner.gui.menu.component.Panel;
import net.ncplanner.plannerator.planner.gui.menu.component.TextDisplay;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.BorderLayout;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.GridLayout;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.LayeredLayout;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.ListButtonsLayout;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.module.DisplayNameModule;
import net.ncplanner.plannerator.planner.ncpf.module.ElementStatsModule;
import net.ncplanner.plannerator.planner.ncpf.module.LegacyNamesModule;
import net.ncplanner.plannerator.planner.ncpf.module.TextureModule;
public class NCPFElementComponent extends LayoutPanel{
    private final ListButtonsLayout buttons;
    private final BorderLayout content;
    public NCPFElementComponent(NCPFElement element){
        super(new LayeredLayout());
        add(new Panel().setBackgroundColor(Core.theme::getTextViewBackgroundColor));
        content = add(new BorderLayout());
        if(element!=null)element.withModule(TextureModule::new, (tex) -> {
                content.add(new Panel().setImage(tex.texture), BorderLayout.LEFT, 96);
            });

        LayoutPanel textGrid = content.add(new LayoutPanel(new GridLayout(2, 1){
            @Override
            public void arrangeComponents(){
                super.arrangeComponents();
                if(components.size()==2&&width<480){
                    components.get(0).width = width;
                    components.get(1).width = components.get(1).height = 0;
                }
            }
        }));

        TextDisplay mainText = textGrid.add(new TextDisplay().fitText());
        if(element==null)mainText.addText("No Target");
        else{
            element.withModule(DisplayNameModule::new, (nam) -> {
                if(nam.displayName!=null&&!nam.displayName.isEmpty())mainText.addText(nam.displayName);
            });
            if(element.definition instanceof NCPFModuleElement){
                mainText.addText(NCPFModuleContainer.recognizedModules.get(((NCPFModuleElement)element.definition).name).get().getFriendlyName());
            }
            mainText.addText("\n"+element.definition.toString());
            for(NCPFModule module : element.modules.modules.values()){
                if(module instanceof BlockFunctionModule){
                    mainText.addText("\n"+((BlockFunctionModule)module).getFunctionName());
                }
                if(module instanceof ElementStatsModule){
                    mainText.addText("\n"+((ElementStatsModule)module).getTooltip());
                }
            }
        }

        if(element!=null){
            element.withModule(LegacyNamesModule::new, (module) -> {
                TextDisplay legacyNames = textGrid.add(new TextDisplay().fitText());
                legacyNames.addText(module.legacyNames.size()+" Legacy Name"+(module.legacyNames.size()==1?"":"s")+":");
                for(String nam : module.legacyNames)legacyNames.addText("\n"+nam);
                mainText.setTooltip(mainText.getTooltip()+"\nLegacy Names:\n"+String.join("\n", module.legacyNames));
            });
        }
        if(textGrid.layout.components.size()==1)((GridLayout)textGrid.layout).columns = 1;

        buttons = content.add(new ListButtonsLayout(){
            @Override
            public void arrangeComponents(){
                for(int i = 0; i<components.size(); i++){
                    Component c = components.get(i);
                    c.y = height/4;
                    c.width = c.height = height/2;
                    c.x = width-(i+1)*height/2-height/4;
                }
            }
        }, BorderLayout.RIGHT);
    }
    public NCPFElementComponent addIconButton(String icon, String tooltip, Runnable onClick){
        buttons.add(new IconButton(icon, true, true).setTooltip(tooltip).addAction(onClick));
        return this;
    }
    public NCPFElementComponent addButton(String text, String tooltip, Runnable onClick){
        buttons.add(new Button(text, true, true).setTooltip(tooltip).addAction(onClick));
        return this;
    }
    @Override
    public void render2d(double deltaTime){
        content.leftWidth = height;
        content.rightWidth = buttons.components.isEmpty()?0:(buttons.components.size()+0.5f)*height/2;
        super.render2d(deltaTime);
    }
}
