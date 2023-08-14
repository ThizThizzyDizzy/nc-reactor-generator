package net.ncplanner.plannerator.planner.gui.menu.component.layout;
import net.ncplanner.plannerator.planner.gui.Component;
public class BorderLayout extends Layout{
    public static final int TOP = 0;
    public static final int BOTTOM = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
    public static final int CENTER = 4;
    private Component top, bottom, left, right, center;
    private Number topHeight, bottomHeight, leftWidth, rightWidth;
    @Override
    public void arrangeComponents(){
        if(top!=null){
            top.x = top.y = 0;
            top.width = width;
            if(topHeight!=null)top.height = topHeight.floatValue();
        }
        if(bottom!=null){
            bottom.x = 0;
            bottom.width = width;
            if(bottomHeight!=null)bottom.height = bottomHeight.floatValue();
            bottom.y = height-bottom.height;
        }
        float centerY = top==null?0:top.height;
        float centerHeight = height-centerY-(bottom==null?0:bottom.height);
        if(left!=null){
            left.x = 0;
            left.y = centerY;
            left.height = centerHeight;
            if(leftWidth!=null)left.width = leftWidth.floatValue();
        }
        if(right!=null){
            right.x = 0;
            right.y = centerY;
            right.height = centerHeight;
            if(rightWidth!=null)right.width = rightWidth.floatValue();
        }
        float centerX = left==null?0:left.width;
        float centerWidth = width-centerX-(right==null?0:right.width);
        if(center!=null){
            center.x = centerX;
            center.y = centerY;
            center.width = centerWidth;
            center.height = centerHeight;
        }
    }
    public <T extends Component> T add(T component, int side){
        return add(component, side, null);
    }
    public <T extends Component> T add(T component, int side, Number size){
        switch(side){
            case TOP:
                top = component;
                topHeight = size;
                return super.add(component);
            case BOTTOM:
                bottom = component;
                bottomHeight = size;
                return super.add(component);
            case LEFT:
                left = component;
                leftWidth = size;
                return super.add(component);
            case RIGHT:
                right = component;
                rightWidth = size;
                return super.add(component);
            case CENTER:
                center = component;
                return super.add(component);
            default:
                throw new RuntimeException("Invalid side: "+side);
        }
    }
    @Override
    public <T extends Component> T add(T component){
        if(center==null){
            center = component;
            return super.add(component);
        }
        if(top==null){
            top = component;
            return super.add(component);
        }
        if(bottom==null){
            bottom = component;
            return super.add(component);
        }
        if(left==null){
            left = component;
            return super.add(component);
        }
        if(right==null){
            right = component;
            return super.add(component);
        }
        throw new RuntimeException("Border Layout can only hold 5 components!");
    }
}