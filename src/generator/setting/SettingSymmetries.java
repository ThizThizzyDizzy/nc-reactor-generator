package generator.setting;
import java.util.ArrayList;
import multiblock.symmetry.Symmetry;
import planner.menu.component.MenuComponentLabel;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentToggleBox;
import simplelibrary.opengl.gui.components.MenuComponent;
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
    public void buildComponents(MenuComponentMinimaList generatorSettings){
        if(name!=null)generatorSettings.add(new MenuComponentLabel(0, 0, 0, 32, name, true));
        MenuComponentMinimaList list = generatorSettings.add(new MenuComponentMinimaList(0, 0, 0, possibleSymmetries.size()*32, 0){
            @Override
            public void render(int millisSinceLastTick){
                for(simplelibrary.opengl.gui.components.MenuComponent c : components){
                    c.width = width-(hasVertScrollbar()?vertScrollbarWidth:0);
                }
                super.render(millisSinceLastTick);
            }
        }.disableSelection());
        list.components = refresh(list);
    }
    private ArrayList<MenuComponent> refresh(MenuComponentMinimaList list){
        ArrayList<Symmetry> value = this.value;//to stop weird edge cases
        ArrayList<MenuComponent> components = new ArrayList<>();
        for(Symmetry s : value){
            components.add(new MenuComponentToggleBox(0, 0, 0, 32, s.name, true){
                @Override
                public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
                    super.onMouseButton(x, y, button, pressed, mods);
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
            components.add(new MenuComponentToggleBox(0, 0, 0, 32, s.name, false){
                @Override
                public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
                    super.onMouseButton(x, y, button, pressed, mods);
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