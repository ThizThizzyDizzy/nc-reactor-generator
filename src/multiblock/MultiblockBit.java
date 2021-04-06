package multiblock;
import simplelibrary.Queue;
import simplelibrary.opengl.Renderer2D;
public class MultiblockBit extends Renderer2D{
    protected static final Queue<Direction> directions = new Queue<>();
    static{
        for(Direction d : Direction.values())directions.enqueue(d);
    }
    protected static final Queue<Axis> axes = axes();
    private static Queue<Axis> axes(){
        Queue<Axis> axes = new Queue<>();
        axes.enqueue(Axis.X);
        axes.enqueue(Axis.Y);
        axes.enqueue(Axis.Z);
        return axes;
    }
    protected String percent(double n, int digits){
        double fac = Math.pow(10, digits);
        double d = (Math.round(n*fac*100)/(double)Math.round(fac));
        return (digits==0?Math.round(d):d)+"%";
    }
    protected String round(double n, int digits){
        double fac = Math.pow(10, digits);
        double d = Math.round(n*fac)/(double)Math.round(fac);
        return (digits==0?Math.round(d):d)+"";
    }
}