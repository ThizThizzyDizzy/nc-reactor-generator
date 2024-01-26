package net.ncplanner.plannerator.planner.gui.menu.component.config2;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.ListLayout;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.SplitLayout;
public class C2ConfigComponentBase extends SplitLayout{
    public final ListLayout content;
    public boolean expanded = true;
    private boolean wasExpanded = true;
    public C2ConfigComponentBase(){
        super(SplitLayout.X_AXIS, 0, 32, 0);
        fitContent();
        add(new Label("", true){
            @Override
            public void draw(double deltaTime){
                super.draw(deltaTime);
                if(height>32)height = 32;
            }
        });//just an indent lol
        content = add(new ListLayout().fitContent());
    }
    @Override
    public void draw(double deltaTime){
        if(!expanded){
            for(Component component : getAllComponents()){
                if(component instanceof Label||component instanceof Button)component.height = 0;
            }
        }else if(!wasExpanded){
            for(Component component : getAllComponents()){
                if(component instanceof Label||component instanceof Button)component.height = 32;
            }
        }
        wasExpanded = expanded;
        super.draw(deltaTime);
    }
}