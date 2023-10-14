package net.ncplanner.plannerator.multiblock.generator.lite;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
public interface LiteMultiblock<T extends Multiblock> extends ThingWithVariables{
    public static final int[][] directions = new int[][]{
        {1, 0, 0},
        {0, 1, 0},
        {0, 0, 1},
        {-1, 0, 0},
        {0, -1, 0},
        {0, 0, -1}
    };
    public void importAndConvert(T multiblock);
    public String getTooltip();
    public void calculate();
    public LiteMultiblock<T> copy();
    public void copyFrom(LiteMultiblock<T> other);
    public void copyVarsFrom(LiteMultiblock<T> other);
    public T export(NCPFConfigurationContainer configg);
    public int getDimension(int id);
    public Image getBlockTexture(int x, int y, int z);
    public float getCubeBounds(int x, int y, int z, int index);
    public LiteGenerator<? extends LiteMultiblock<T>>[] createGenerators(LiteMultiblock<T> priorityMultiblock);
    public void clear();
}