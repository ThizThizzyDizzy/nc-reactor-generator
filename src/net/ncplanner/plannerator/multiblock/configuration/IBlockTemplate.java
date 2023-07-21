package net.ncplanner.plannerator.multiblock.configuration;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.ncpf.NCPFElement;
public interface IBlockTemplate extends ThingWithLegacyNames{
    public NCPFElement asElement();
    String getName();
    String getDisplayName();
    Image getTexture();
    Image getDisplayTexture();
}
