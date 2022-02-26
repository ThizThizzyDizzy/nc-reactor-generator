package net.ncplanner.plannerator.multiblock.generator.lite.variable.setting;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.planner.gui.menu.MenuGenerator;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.ToggleBox;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.GridLayout;
public class SettingIndicies implements Setting<int[]>{
    private final String name;
    private int[] value;
    public final String[] names;
    private final Image[] images;
    public SettingIndicies(String name, int[] value, String[] names, Image[] images){
        this.name = name;
        this.value = value;
        this.names = names;
        this.images = images;
    }
    public SettingIndicies(String name, String[] names, Image[] images){
        this(name, gen(names), names, images);
    }
    @Override
    public String getName(){
        return name;
    }
    @Override
    public void set(int[] value){
        this.value = value;
    }
    @Override
    public int[] get(){
        return value;
    }
    private static int[] gen(String[] names){
        int[] indicies = new int[names.length];
        for(int i = 0; i<indicies.length; i++)indicies[i] = i;
        return indicies;
    }
    @Override
    public void addSettings(SingleColumnList list, MenuGenerator menu){
        GridLayout grid = new GridLayout(24, 2);
        ToggleBox[] boxes = new ToggleBox[names.length];
        for(int i = 0; i<names.length; i++){
            boolean on = false;
            for(int j : value)if(j==i)on = true;
            boxes[i] = grid.add(new ToggleBox(0, 0, 0, 0, names[i], on){
                {
                    textInset = 0;
                    onChange(() -> {
                        ArrayList<Integer> vals = new ArrayList<>();
                        for(int j = 0; j<boxes.length; j++){
                            ToggleBox box = boxes[j];
                            if(box.isToggledOn)vals.add(j);
                        }
                        int[] newVal = new int[vals.size()];
                        for(int j = 0; j<vals.size(); j++){
                            newVal[j] = vals.get(j);
                        }
                        set(newVal);
                    });
                }
            }.setImage(images[i]));
        }
        list.add(grid);
    }
}