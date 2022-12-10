package net.ncplanner.plannerator.multiblock.generator.setting;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.generator.Priority;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
public class SettingPriorities implements Setting<ArrayList<Priority>>{
    private final String name;
    private ArrayList<Priority> value = new ArrayList<>();
    public SettingPriorities(String name, ArrayList<Priority> defaultValue){
        this.name = name;
        this.value = defaultValue;
    }
    @Override
    public ArrayList<Priority> getValue(){
        return value;
    }
    @Override
    public void setValue(ArrayList<Priority> value){
        this.value = new ArrayList<>(value);//to prevent concurrent modification
    }
    @Override
    public void buildComponents(SingleColumnList generatorSettings){
        if(name!=null)generatorSettings.add(new Label(0, 0, 0, 32, name, true));
        SingleColumnList list = generatorSettings.add(new SingleColumnList(0, 0, 0, value.size()*32, 0){
            @Override
            public void render2d(double deltaTime){
                for(Component c : components){
                    c.width = width-(hasVertScrollbar()?vertScrollbarWidth:0);
                }
                super.render2d(deltaTime);
            }
        }.disableSelection());
        list.components = refresh(list);
//        corePrioritiesList.components.clear();
//        for(Priority priority : priorities){
//            if(priority.isCore())corePrioritiesList.add(new MenuComponentPriority(priority));
//        }
//        MenuComponent priorityButtonHolder = generatorSettings.add(new MenuComponent(0, 0, 0, 32){
//            @Override
//            public void renderBackground(){
//                components.get(1).x = width/2;
//                components.get(0).width = components.get(1).width = width/2;
//                components.get(0).height = components.get(1).height = height;
//            }
//            @Override
//            public void render(){}
//        });
//        moveCoreUp = priorityButtonHolder.add(new Button("Move Up", true, true).setTooltip("Move the selected priority up so it is more important"));
//        moveCoreUp.addAction(() -> {
//            int index = corePrioritiesList.getSelectedIndex();
//            if(index==-1||index==0)return;
//            corePrioritiesList.components.add(index-1, corePrioritiesList.components.remove(index));
////            refreshPriorities();
//            corePrioritiesList.setSelectedIndex(index-1);
//        });
//        moveCoreDown = priorityButtonHolder.add(new Button("Move Down", true, true).setTooltip("Move the selected priority down so it is less important"));
//        moveCoreDown.addAction(() -> {
//            int index = corePrioritiesList.getSelectedIndex();
//            if(index==-1||index==corePrioritiesList.components.size()-1)return;
//            corePrioritiesList.components.add(index+1, corePrioritiesList.components.remove(index));
////            refreshPriorities();
//            corePrioritiesList.setSelectedIndex(index+1);
//        });
    }
    private ArrayList<Component> refresh(SingleColumnList list){
        ArrayList<Priority> value = this.value;//to stop weird edge cases
        ArrayList<Component> components = new ArrayList<>();
        for(int i = 0; i<value.size(); i++){
            Priority p = value.get(i);
            Label theLabel;
            components.add(theLabel = new Label(0, 0, 0, 32, p.name));
            if(i!=0)theLabel.add(new Button(0, 0, 32, 32, "^", true, true){
                {
                    addAction(() -> {
                        ArrayList<Priority> value = new ArrayList<>(SettingPriorities.this.value);
                        int index = value.indexOf(p);
                        if(index==-1||index==0)return;
                        value.add(index-1, value.remove(index));
                        setValue(value);
                        list.components = refresh(list);
                    });
                }
                @Override
                public void render2d(double deltaTime){
                    x = theLabel.width-32;
                    super.render2d(deltaTime);
                }
            }.setTooltip("Move Up"));
            if(i!=value.size()-1)theLabel.add(new Button(0, 0, 32, 32, "v", true, true){
                {
                    addAction(() -> {
                        ArrayList<Priority> value = new ArrayList<>(SettingPriorities.this.value);
                        int index = value.indexOf(p);
                        if(index==-1||index==value.size()-1)return;
                        value.add(index+1, value.remove(index));
                        setValue(value);
                        list.components = refresh(list);
                    });
                }
                @Override
                public void render2d(double deltaTime){
                    x = theLabel.width-64;
                    super.render2d(deltaTime);
                }
            }.setTooltip("Move Down"));
        }
        return components;
    }
}