package net.ncplanner.plannerator.multiblock.generator.lite.anim;
public class DualAxisSplitAnimation extends Animation{
    private final int axis;
    private final float splitMultiplier;
    private final int axis2;
    public DualAxisSplitAnimation(int axis, int axis2, float length, float splitMultiplier){
        super(length);
        this.axis = axis;
        this.splitMultiplier = splitMultiplier;
        this.axis2 = axis2;
    }
    @Override
    public double getCubeOffset(int x, int y, int z, int w, int h, int d, int axis){
        int[] dims = new int[]{w,h,d};
        int[] pos = new int[]{x,y,z};
        if(axis>2)return getCubeOffset(x, y, z, w, h, d, axis-3);
        float off = 0;
        if(axis==this.axis){
            float gradualOff = getPercent();
            float splitPos = getPercent()*(dims[axis]);
            float specificOff = 0;
            int p = pos[axis];
            if(p<splitPos-1)specificOff--;
            else if(p<splitPos)specificOff-=splitPos-p;
            off+=(gradualOff+specificOff)*splitMultiplier;
        }
        if(axis==this.axis-3){
            float gradualOff = getPercent();
            float splitPos = getPercent()*(dims[axis]);
            float specificOff = 0;
            int p = dims[axis]-pos[axis]-1;
            if(p<splitPos-1)specificOff--;
            else if(p<splitPos)specificOff-=splitPos-p;
            off+= -(gradualOff+specificOff)*splitMultiplier;
        }
        return off;
    }
    @Override
    public double getYRotOffset(){
        return 0;
    }
}