package planner.menu.configuration.underhaul.fissionsfr;
import multiblock.configuration.underhaul.fissionsfr.PlacementRule;
import multiblock.configuration.underhaul.fissionsfr.RuleContainer;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuPlacementRulesConfiguration extends Menu{
    private final MenuComponentMinimaList list = add(new MenuComponentMinimaList(0, 0, 0, 0, 50));
    private final MenuComponentMinimalistButton add = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Add Rule", true, true));
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    private final RuleContainer container;
    private boolean refreshNeeded = false;
    public MenuPlacementRulesConfiguration(GUI gui, Menu parent, RuleContainer container){
        super(gui, parent);
        add.addActionListener((e) -> {
            PlacementRule rule = new PlacementRule();
            container.rules.add(rule);
            gui.open(new MenuPlacementRuleConfiguration(gui, this, rule));
        });
        back.addActionListener((e) -> {
            gui.open(parent);
        });
        this.container = container;
    }
    @Override
    public void tick(){
        if(refreshNeeded){
            onGUIOpened();
            refreshNeeded = false;
        }
        super.tick();
    }
    @Override
    public void onGUIOpened(){
        list.components.clear();
        for(PlacementRule rule : container.rules){
            list.add(new MenuComponentPlacementRuleConfiguration(rule));
        }
    }
    @Override
    public void render(int millisSinceLastTick){
        list.width = gui.helper.displayWidth();
        list.height = gui.helper.displayHeight()-back.height-add.height;
        for(simplelibrary.opengl.gui.components.MenuComponent component : list.components){
            component.width = list.width-(list.hasVertScrollbar()?list.vertScrollbarWidth:0);
        }
        add.width = back.width = gui.helper.displayWidth();
        add.height = back.height = gui.helper.displayHeight()/16;
        back.y = gui.helper.displayHeight()-back.height;
        add.y = back.y-add.height;
        super.render(millisSinceLastTick);
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        for(simplelibrary.opengl.gui.components.MenuComponent c : list.components){
            if(c instanceof MenuComponentPlacementRuleConfiguration){
                if(button==((MenuComponentPlacementRuleConfiguration) c).delete){
                    container.rules.remove(((MenuComponentPlacementRuleConfiguration) c).rule);
                    refreshNeeded = true;
                    return;
                }
                if(button==((MenuComponentPlacementRuleConfiguration) c).edit){
                    gui.open(new MenuPlacementRuleConfiguration(gui, this, ((MenuComponentPlacementRuleConfiguration) c).rule));
                    return;
                }
            }
        }
        super.buttonClicked(button);
    }
}