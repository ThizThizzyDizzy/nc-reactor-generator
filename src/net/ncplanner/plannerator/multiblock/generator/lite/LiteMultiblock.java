package net.ncplanner.plannerator.multiblock.generator.lite;
import java.util.ArrayList;
import java.util.function.Supplier;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.generator.lite.mutator.Mutator;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
public interface LiteMultiblock<T extends Multiblock> extends ThingWithVariables{
    public void importAndConvert(T multiblock);
    public String getTooltip();
    public void calculate();
    public void getMutators(ArrayList<Supplier<Mutator>> mutators);
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