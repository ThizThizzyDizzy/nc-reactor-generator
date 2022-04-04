package net.ncplanner.plannerator.multiblock.generator.lite;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.BlockPos;
import net.ncplanner.plannerator.multiblock.BlockPosConsumer;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.Setting;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingBoolean;
public class Symmetry implements ThingWithSettings{
    public SettingBoolean mx = new SettingBoolean("X Mirror Symmetry", false);
    public SettingBoolean my = new SettingBoolean("Y Mirror Symmetry", false);
    public SettingBoolean mz = new SettingBoolean("Z Mirror Symmetry", false);
    public SettingBoolean rx180 = new SettingBoolean("X 180 Degree Rotational Symmetry", false);
    public SettingBoolean ry180 = new SettingBoolean("Y 180 Degree Rotational Symmetry", false);
    public SettingBoolean rz180 = new SettingBoolean("Z 180 Degree Rotational Symmetry", false);
    Setting[] settings = new Setting[]{mx,my,mz,rx180,ry180,rz180};
    @Override
    public int getSettingCount(){
        return settings.length;
    }
    @Override
    public Setting getSetting(int i){
        return settings[i];
    }
    public void apply(int x, int y, int z, int w, int h, int d, BlockPosConsumer consumer){
        ArrayList<BlockPos> positions = new ArrayList<>();
        positions.add(new BlockPos(x, y, z));
        if(mx.get()){
            ArrayList<BlockPos> newPositions = new ArrayList<>();
            positions.forEach((p) -> {
                newPositions.add(new BlockPos(w-p.x-1, p.y, p.z));
            });
            positions.addAll(newPositions);
        }
        if(my.get()){
            ArrayList<BlockPos> newPositions = new ArrayList<>();
            positions.forEach((p) -> {
                newPositions.add(new BlockPos(p.x, h-p.y-1, p.z));
            });
            positions.addAll(newPositions);
        }
        if(mz.get()){
            ArrayList<BlockPos> newPositions = new ArrayList<>();
            positions.forEach((p) -> {
                newPositions.add(new BlockPos(p.x, p.y, d-p.z-1));
            });
            positions.addAll(newPositions);
        }
        if(rx180.get()){
            ArrayList<BlockPos> newPositions = new ArrayList<>();
            positions.forEach((p) -> {
                newPositions.add(new BlockPos(p.x, h-p.y-1, d-p.z-1));
            });
            positions.addAll(newPositions);
        }
        if(ry180.get()){
            ArrayList<BlockPos> newPositions = new ArrayList<>();
            positions.forEach((p) -> {
                newPositions.add(new BlockPos(w-p.x-1, p.y, d-p.z-1));
            });
            positions.addAll(newPositions);
        }
        if(rz180.get()){
            ArrayList<BlockPos> newPositions = new ArrayList<>();
            positions.forEach((p) -> {
                newPositions.add(new BlockPos(w-p.x-1, h-p.y-1, p.z));
            });
            positions.addAll(newPositions);
        }
        positions.forEach((p) -> {
            if(p.x<0||p.y<0||p.z<0||p.x>=w||p.y>=h||p.z>=d)return;
            consumer.accept(p.x, p.y, p.z);
        });
    }
}