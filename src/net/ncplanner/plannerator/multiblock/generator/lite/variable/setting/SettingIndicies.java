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
    public String[] names;
    public Image[] images;
    public SettingIndicies(String name){
        this.name = name;
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
            }.setImage(images[i])).allowSliding();
        }
        list.add(grid);
    }
    private String[] insert(String air, String[] array){
        if(air==null)return array;
        String[] newArr = new String[array.length+1];
        newArr[0] = air;
        for(int i = 0; i<array.length; i++){
            newArr[i+1] = array[i];
        }
        return newArr;
    }
    private Image[] insert(Image air, Image[] array){
        Image[] newArr = new Image[array.length+1];
        newArr[0] = air;
        for(int i = 0; i<array.length; i++){
            newArr[i+1] = array[i];
        }
        return newArr;
    }
    private int[] shift(int[] array){
        for(int i = 0; i<array.length; i++)array[i]++;
        return array;
    }
    private int[] insert(int air, int[] array){
        int[] newArr = new int[array.length+1];
        newArr[0] = air;
        for(int i = 0; i<array.length; i++){
            newArr[i+1] = array[i];
        }
        return newArr;
    }
    public void init(String[] names, Image[] images, String air){
        this.names = insert(air, names);
        this.images = air==null?images:insert(null, images);
    }
    public void init(String[] names, Image[] images){
        init(names, images, null);
    }
}