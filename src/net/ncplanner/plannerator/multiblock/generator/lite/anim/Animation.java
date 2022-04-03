package net.ncplanner.plannerator.multiblock.generator.lite.anim;
public abstract class Animation{
    public final float length;
    public float pos;
    public Animation(float length){
        this.length = length;
    }
    public abstract double getCubeOffset(int x, int y, int z, int w, int h, int d, int axis);
    public abstract double getYRotOffset();
    public float getPercent(){
        return pos/length;
    }
}