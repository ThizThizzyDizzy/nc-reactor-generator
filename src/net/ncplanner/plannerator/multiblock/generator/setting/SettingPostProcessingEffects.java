package net.ncplanner.plannerator.multiblock.generator.setting;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.editor.ppe.PostProcessingEffect;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.ToggleBox;
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
    public void buildComponents(SingleColumnList generatorSettings){
        if(name!=null)generatorSettings.add(new Label(0, 0, 0, 32, name, true));
        SingleColumnList list = generatorSettings.add(new SingleColumnList(0, 0, 0, possibleEffects.size()*32, 0){
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
        ArrayList<PostProcessingEffect> value = this.value;//to stop weird edge cases
        ArrayList<Component> components = new ArrayList<>();
        for(PostProcessingEffect s : value){
            components.add(new ToggleBox(0, 0, 0, 32, s.name, true){
                @Override
                public void onMouseButton(double x, double y, int button, int action, int mods){
                    super.onMouseButton(x, y, button, action, mods);
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
            components.add(new ToggleBox(0, 0, 0, 32, e.name, false){
                @Override
                public void onMouseButton(double x, double y, int button, int action, int mods){
                    super.onMouseButton(x, y, button, action, mods);
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