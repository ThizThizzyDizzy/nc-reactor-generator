package net.ncplanner.plannerator.planner.gui.menu.component.layout;
import net.ncplanner.plannerator.planner.gui.Component;
public class SidebarLayout extends Layout{//TODO combine with SplitLayout
    private final float sidebarWidth;
    public SidebarLayout(float sidebarWidth){
        this.sidebarWidth = sidebarWidth;
    }
    @Override
    public void arrangeComponents(){
        Component sidebar = components.get(0);
        if(sidebar!=null){
            sidebar.x = sidebar.y = 0;
            sidebar.width = sidebarWidth;
            sidebar.height = height;
        }
        Component content = components.get(1);
        if(content!=null){
            content.x = sidebarWidth;
            content.y = 0;
            content.width = width-sidebarWidth;
            content.height = height;
        }
    }
    @Override
    public <T extends Component> T add(T component){
        if(components.size()>=2)throw new RuntimeException("Sidebar Layout can only hold two components!");
        return super.add(component);
    }
}