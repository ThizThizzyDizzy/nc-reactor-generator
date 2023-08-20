package net.ncplanner.plannerator.planner.gui.menu.configuration;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.NCPFModuleContainer;
import net.ncplanner.plannerator.ncpf.element.NCPFModuleElement;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.IconButton;
import net.ncplanner.plannerator.planner.gui.menu.component.LayoutPanel;
import net.ncplanner.plannerator.planner.gui.menu.component.Panel;
import net.ncplanner.plannerator.planner.gui.menu.component.TextDisplay;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.BorderLayout;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.LayeredLayout;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.ListButtonsLayout;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.module.DisplayNameModule;
import net.ncplanner.plannerator.planner.ncpf.module.ElementStatsModule;
import net.ncplanner.plannerator.planner.ncpf.module.TextureModule;
public class NCPFElementComponent extends LayoutPanel{
    private final ListButtonsLayout buttons;
    public NCPFElementComponent(NCPFElement element){
        super(new LayeredLayout());
        add(new Panel().setBackgroundColor(Core.theme::getTextViewBackgroundColor));
        BorderLayout content = add(new BorderLayout());
        if(element!=null)element.withModule(TextureModule::new, (tex)->{
            content.add(new Panel().setImage(tex.texture), BorderLayout.LEFT, 96);
        });
        TextDisplay display = content.add(new TextDisplay().fitText(), BorderLayout.CENTER);
        if(element==null)display.addText("No Target");
        else{
            element.withModule(DisplayNameModule::new, (nam)->{
                display.addText(nam.displayName);
            });
            if(element.definition instanceof NCPFModuleElement){
                display.addText(NCPFModuleContainer.recognizedModules.get(((NCPFModuleElement)element.definition).name).get().getFriendlyName());
            }
            display.addText("\n"+element.definition.toString());
            for(NCPFModule module : element.modules.modules.values()){
                if(module instanceof BlockFunctionModule){
                    display.addText("\n"+((BlockFunctionModule)module).getFunctionName());
                }
                if(module instanceof ElementStatsModule){
                    display.addText("\n"+((ElementStatsModule)module).getTooltip());
                }
            }
        }
        buttons = add(new ListButtonsLayout());
    }
    public NCPFElementComponent addIconButton(String icon, String tooltip, Runnable onClick){
        buttons.add(new IconButton(icon, true, true).setTooltip(tooltip).addAction(onClick));
        return this;
    }
    public NCPFElementComponent addButton(String text, String tooltip, Runnable onClick){
        buttons.add(new Button(text, true, true).setTooltip(tooltip).addAction(onClick));
        return this;
    }
}