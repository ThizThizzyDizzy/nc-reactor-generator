package net.ncplanner.plannerator.multiblock.generator.lite.anim;
public class SpinAnimation extends Animation{
    public SpinAnimation(float length){
        super(length);
    }
    @Override
    public double getCubeOffset(int x, int y, int z, int w, int h, int d, int axis){
        return 0;
    }
    @Override
    public double getYRotOffset(){
        return -180*Math.cos(Math.PI*getPercent())+180;
    }
}