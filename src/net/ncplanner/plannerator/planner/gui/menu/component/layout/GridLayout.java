package net.ncplanner.plannerator.planner.gui.menu.component.layout;
import net.ncplanner.plannerator.planner.gui.Component;
public class GridLayout extends Layout{
    private final int columns;
    private final int rows;
    public GridLayout(int columns, int rows){
        if(rows==0&&columns==0)throw new IllegalArgumentException("Cannot create grid layout with infinite rows and columns!");
        if(columns==0)columns = Integer.MAX_VALUE;
        if(rows==0)rows = Integer.MAX_VALUE;
        this.columns = columns;
        this.rows = rows;
    }
    @Override
    public void arrangeComponents(){
        int actualColumns = columns==Integer.MAX_VALUE?roundup(components.size()/(float)rows):columns;
        int actualRows = rows==Integer.MAX_VALUE?roundup(components.size()/(float)columns):rows;
        float compWidth = width/actualColumns;
        float compHeight = height/actualRows;
        for(int i = 0; i<components.size(); i++){
            int y = i/actualColumns;
            int x = i%actualColumns;
            Component component = components.get(i);
            component.x = x*compWidth;
            component.y = y*compHeight;
            component.width = compWidth;
            component.height = compHeight;
        }
    }
    private int roundup(float f){
        if(f==(int)f)return (int)f;
        return (int)f+1;
    }
}