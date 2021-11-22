package net.ncplanner.plannerator.multiblock.generator.setting;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.editor.symmetry.Symmetry;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.ToggleBox;
public class SettingSymmetries implements Setting<ArrayList<Symmetry>>{
    private final String name;
    private final ArrayList<Symmetry> possibleSymmetries;
    private ArrayList<Symmetry> value = new ArrayList<>();
    public SettingSymmetries(String name, ArrayList<Symmetry> symmetries){
        this.name = name;
        this.possibleSymmetries = symmetries;
    }
    @Override
    public ArrayList<Symmetry> getValue(){
        return value;
    }
    @Override
    public void setValue(ArrayList<Symmetry> value){
        this.value = new ArrayList<>(value);//to prevent concurrent modification
    }
    @Override
    public void buildComponents(SingleColumnList generatorSettings){
        if(name!=null)generatorSettings.add(new Label(0, 0, 0, 32, name, true));
        SingleColumnList list = generatorSettings.add(new SingleColumnList(0, 0, 0, possibleSymmetries.size()*32, 0){
            @Override
            public void render2d(double deltaTime){
                for(Component c : components){
                    c.width = width-(hasVertScrollbar()?vertScrollbarWidth:0);
                }
                super.render2d(deltaTime);
            }
        }.disableSelection());
        list.components = refresh(list);
    }
    private ArrayList<Component> refresh(SingleColumnList list){
        ArrayList<Symmetry> value = this.value;//to stop weird edge cases
        ArrayList<Component> components = new ArrayList<>();
        for(Symmetry s : value){
            components.add(new ToggleBox(0, 0, 0, 32, s.name, true){
                @Override
                public void onMouseButton(double x, double y, int button, int action, int mods){
                    super.onMouseButton(x, y, button, action, mods);
                    if(!isToggledOn){
                        ArrayList<Symmetry> newValue = new ArrayList<>(SettingSymmetries.this.value);
                        newValue.remove(s);
                        setValue(newValue);
                        list.components = refresh(list);
                    }
                }
            });
        }
        for(Symmetry s : possibleSymmetries){
            if(value.contains(s))continue;
            components.add(new ToggleBox(0, 0, 0, 32, s.name, false){
                @Override
                public void onMouseButton(double x, double y, int button, int action, int mods){
                    super.onMouseButton(x, y, button, action, mods);
                    if(isToggledOn){
                        ArrayList<Symmetry> newValue = new ArrayList<>(SettingSymmetries.this.value);
                        newValue.add(s);
                        setValue(newValue);
                        list.components = refresh(list);
                    }
                }
            });
        }
        return components;
    }
}