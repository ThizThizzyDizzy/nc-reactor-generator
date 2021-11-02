package net.ncplanner.plannerator.multiblock.generator.setting;
import net.ncplanner.plannerator.multiblock.generator.Priority;
import java.util.ArrayList;
import net.ncplanner.plannerator.planner.menu.component.MenuComponentLabel;
import net.ncplanner.plannerator.planner.menu.component.MenuComponentMinimaList;
import net.ncplanner.plannerator.planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.opengl.gui.components.MenuComponent;
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
    public void buildComponents(MenuComponentMinimaList generatorSettings){
        if(name!=null)generatorSettings.add(new MenuComponentLabel(0, 0, 0, 32, name, true));
        MenuComponentMinimaList list = generatorSettings.add(new MenuComponentMinimaList(0, 0, 0, value.size()*32, 0){
            @Override
            public void render(int millisSinceLastTick){
                for(simplelibrary.opengl.gui.components.MenuComponent c : components){
                    c.width = width-(hasVertScrollbar()?vertScrollbarWidth:0);
                }
                super.render(millisSinceLastTick);
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
//        moveCoreUp = priorityButtonHolder.add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Move Up", true, true).setTooltip("Move the selected priority up so it is more important"));
//        moveCoreUp.addActionListener((e) -> {
//            int index = corePrioritiesList.getSelectedIndex();
//            if(index==-1||index==0)return;
//            corePrioritiesList.components.add(index-1, corePrioritiesList.components.remove(index));
////            refreshPriorities();
//            corePrioritiesList.setSelectedIndex(index-1);
//        });
//        moveCoreDown = priorityButtonHolder.add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Move Down", true, true).setTooltip("Move the selected priority down so it is less important"));
//        moveCoreDown.addActionListener((e) -> {
//            int index = corePrioritiesList.getSelectedIndex();
//            if(index==-1||index==corePrioritiesList.components.size()-1)return;
//            corePrioritiesList.components.add(index+1, corePrioritiesList.components.remove(index));
////            refreshPriorities();
//            corePrioritiesList.setSelectedIndex(index+1);
//        });
    }
    private ArrayList<MenuComponent> refresh(MenuComponentMinimaList list){
        ArrayList<Priority> value = this.value;//to stop weird edge cases
        ArrayList<MenuComponent> components = new ArrayList<>();
        for(int i = 0; i<value.size(); i++){
            Priority p = value.get(i);
            MenuComponentLabel theLabel;
            components.add(theLabel = new MenuComponentLabel(0, 0, 0, 32, p.name));
            if(i!=0)theLabel.add(new MenuComponentMinimalistButton(0, 0, 32, 32, "^", true, true){
                @Override
                public void render(int millisSinceLastTick){
                    x = theLabel.width-32;
                    super.render(millisSinceLastTick);
                }
                @Override
                public void action(){
                    ArrayList<Priority> value = new ArrayList<>(SettingPriorities.this.value);
                    int index = value.indexOf(p);
                    if(index==-1||index==0)return;
                    value.add(index-1, value.remove(index));
                    setValue(value);
                    list.components = refresh(list);
                }
            }.setTooltip("Move Up"));
            if(i!=value.size()-1)theLabel.add(new MenuComponentMinimalistButton(0, 0, 32, 32, "v", true, true){
                @Override
                public void render(int millisSinceLastTick){
                    x = theLabel.width-64;
                    super.render(millisSinceLastTick);
                }
                @Override
                public void action(){
                    ArrayList<Priority> value = new ArrayList<>(SettingPriorities.this.value);
                    int index = value.indexOf(p);
                    if(index==-1||index==value.size()-1)return;
                    value.add(index+1, value.remove(index));
                    setValue(value);
                    list.components = refresh(list);
                }
            }.setTooltip("Move Down"));
        }
        return components;
    }
}