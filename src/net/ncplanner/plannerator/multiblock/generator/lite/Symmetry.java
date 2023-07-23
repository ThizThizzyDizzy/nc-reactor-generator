package net.ncplanner.plannerator.multiblock.generator.lite;
import net.ncplanner.plannerator.multiblock.BlockPosConsumer;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.Setting;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingBoolean;
import net.ncplanner.plannerator.ncpf.DefinedNCPFObject;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class Symmetry extends DefinedNCPFObject implements ThingWithSettings{
    public SettingBoolean mx = new SettingBoolean("X Mirror Symmetry", false).allowSliding();
    public SettingBoolean my = new SettingBoolean("Y Mirror Symmetry", false).allowSliding();
    public SettingBoolean mz = new SettingBoolean("Z Mirror Symmetry", false).allowSliding();
    public SettingBoolean rx180 = new SettingBoolean("X 180 Degree Rotational Symmetry", false).allowSliding();
    public SettingBoolean ry180 = new SettingBoolean("Y 180 Degree Rotational Symmetry", false).allowSliding();
    public SettingBoolean rz180 = new SettingBoolean("Z 180 Degree Rotational Symmetry", false).allowSliding();
    Setting[] settings = new Setting[]{mx,my,mz,rx180,ry180,rz180};
    @Override
    public int getSettingCount(){
        return settings.length;
    }
    @Override
    public Setting getSetting(int i){
        return settings[i];
    }
    private final net.ncplanner.plannerator.multiblock.Symmetry symmetry = new net.ncplanner.plannerator.multiblock.Symmetry(false, false, false, false, false, false);
    public void apply(int x, int y, int z, int w, int h, int d, BlockPosConsumer consumer){
        symmetry.mx = mx.get();
        symmetry.my = my.get();
        symmetry.mz = mz.get();
        symmetry.rx180 = rx180.get();
        symmetry.ry180 = ry180.get();
        symmetry.rz180 = rz180.get();
        symmetry.apply(x, y, z, w, h, d, consumer);
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        mx.set(ncpf.getBoolean("mirror_x"));
        my.set(ncpf.getBoolean("mirror_y"));
        mz.set(ncpf.getBoolean("mirror_z"));
        rx180.set(ncpf.getBoolean("rotate_180_x"));
        ry180.set(ncpf.getBoolean("rotate_180_y"));
        rz180.set(ncpf.getBoolean("rotate_180_z"));
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setBoolean("mirror_x", mx.get());
        ncpf.setBoolean("mirror_y", my.get());
        ncpf.setBoolean("mirror_z", mz.get());
        ncpf.setBoolean("rotate_180_x", rx180.get());
        ncpf.setBoolean("rotate_180_y", ry180.get());
        ncpf.setBoolean("rotate_180_z", rz180.get());
    }
}