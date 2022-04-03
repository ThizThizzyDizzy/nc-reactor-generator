package net.ncplanner.plannerator.multiblock.generator.lite.anim;
public class BlankAnimation extends Animation{
    public BlankAnimation(){
        super(1);
    }
    @Override
    public double getCubeOffset(int x, int y, int z, int w, int h, int d, int axis){
        return 0;
    }
    @Override
    public double getYRotOffset(){
        return 0;
    }
}