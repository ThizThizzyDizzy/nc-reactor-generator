package net.ncplanner.plannerator.multiblock.generator.lite;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.Variable;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
public abstract class LiteMultiblock<T extends Multiblock> implements ThingWithVariables{
    public static final int[][] directions = new int[][]{
        {1, 0, 0},
        {0, 1, 0},
        {0, 0, 1},
        {-1, 0, 0},
        {0, -1, 0},
        {0, 0, -1}
    };
    private Variable[] vars;
    public abstract Variable[] genVariables();
    @Override
    public int getVariableCount(){
        if(vars==null)vars = genVariables();
        return vars.length;
    }
    @Override
    public Variable getVariable(int i){
        if(vars==null)vars = genVariables();
        return vars[i];
    }
    public abstract void importAndConvert(T multiblock);
    public abstract String getTooltip();
    public abstract void calculate();
    public abstract LiteMultiblock<T> copy();
    public abstract void copyFrom(LiteMultiblock<T> other);
    public abstract void copyVarsFrom(LiteMultiblock<T> other);
    public abstract T export(NCPFConfigurationContainer configg);
    public abstract int getDimension(int id);
    public abstract Image getBlockTexture(int x, int y, int z);
    public abstract float getCubeBounds(int x, int y, int z, int index);
    public abstract LiteGenerator<? extends LiteMultiblock<T>> importGenerator(LiteGenerator gen);
    public abstract void clear();
}