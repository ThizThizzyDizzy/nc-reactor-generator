package generator.setting;
import java.util.ArrayList;
import multiblock.ppe.PostProcessingEffect;
import planner.menu.component.MenuComponentLabel;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentToggleBox;
import simplelibrary.opengl.gui.components.MenuComponent;
public class SettingPostProcessingEffects implements Setting<ArrayList<PostProcessingEffect>>{
    private final String name;
    private final ArrayList<PostProcessingEffect> possibleEffects;
    private ArrayList<PostProcessingEffect> value = new ArrayList<>();
    public SettingPostProcessingEffects(String name, ArrayList<PostProcessingEffect> postProcessingEffects){
        this.name = name;
        this.possibleEffects = postProcessingEffects;
    }
    @Override
    public ArrayList<PostProcessingEffect> getValue(){
        return value;
    }
    @Override
    public void setValue(ArrayList<PostProcessingEffect> value){
        this.value = new ArrayList<>(value);//to prevent concurrent modification
    }
    @Override
    public void buildComponents(MenuComponentMinimaList generatorSettings){
        if(name!=null)generatorSettings.add(new MenuComponentLabel(0, 0, 0, 32, name, true));
        MenuComponentMinimaList list = generatorSettings.add(new MenuComponentMinimaList(0, 0, 0, possibleEffects.size()*32, 0){
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
        ArrayList<PostProcessingEffect> value = this.value;//to stop weird edge cases
        ArrayList<MenuComponent> components = new ArrayList<>();
        for(PostProcessingEffect s : value){
            components.add(new MenuComponentToggleBox(0, 0, 0, 32, s.name, true){
                @Override
                public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
                    super.onMouseButton(x, y, button, pressed, mods);
                    if(!isToggledOn){
                        ArrayList<PostProcessingEffect> newValue = new ArrayList<>(SettingPostProcessingEffects.this.value);
                        newValue.remove(s);
                        setValue(newValue);
                        list.components = refresh(list);
                    }
                }
            });
        }
        for(PostProcessingEffect e : possibleEffects){
            if(value.contains(e))continue;
            components.add(new MenuComponentToggleBox(0, 0, 0, 32, e.name, false){
                @Override
                public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
                    super.onMouseButton(x, y, button, pressed, mods);
                    if(isToggledOn){
                        ArrayList<PostProcessingEffect> newValue = new ArrayList<>(SettingPostProcessingEffects.this.value);
                        newValue.add(e);
                        setValue(newValue);
                        list.components = refresh(list);
                    }
                }
            });
        }
        return components;
    }
}