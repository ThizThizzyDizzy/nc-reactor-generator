package net.ncplanner.plannerator.planner.file.reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import net.ncplanner.plannerator.multiblock.Direction;
import net.ncplanner.plannerator.planner.ncpf.design.OverhaulMSRDesign;
import net.ncplanner.plannerator.planner.ncpf.design.OverhaulSFRDesign;
public class LegacyNeutronSourceHandler{
    public static void addNeutronSource(OverhaulSFRDesign sfr, int x, int y, int z, net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Block source){
        HashMap<int[], Integer> possible = new HashMap<>();
        for(Direction d : Direction.values()){
            int i = 0;
            while(true){
                i++;
                int X = x+d.x*i;
                int Y = y+d.y*i;
                int Z = z+d.z*i;
                if(X<0||Y<0||Z<0||X>=sfr.design.length||Y>=sfr.design[0].length||Z>=sfr.design[0][0].length){
                    possible.put(new int[]{x+d.x*(i-1),y+d.y*(i-1),z+d.z*(i-1)}, i);
                    break;
                }
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Block b = sfr.design[X][Y][Z];
                if(b==null)continue;//air
                if(b.fuelCell!=null||b.reflector!=null||b.irradiator!=null)break;
            }
        }
        ArrayList<int[]> keys = new ArrayList<>(possible.keySet());
        Collections.sort(keys, (o1, o2) -> {
            return possible.get(o1)-possible.get(o2);
        });
        for(int[] key : keys){
            net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Block was = sfr.design[key[0]][key[1]][key[2]];
            if(tryAddNeutronSource(sfr, source, key[0], key[1], key[2]))break;
        }
    }
    private static boolean tryAddNeutronSource(OverhaulSFRDesign sfr, net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Block source, int X, int Y, int Z){
        net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Block b = sfr.design[X][Y][Z];
        if(b!=null&&(b.neutronSource!=null))return false;
        sfr.design[X][Y][Z] = source;
        return true;
    }
    public static void addNeutronSource(OverhaulMSRDesign msr, int x, int y, int z, net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Block source){
        HashMap<int[], Integer> possible = new HashMap<>();
        for(Direction d : Direction.values()){
            int i = 0;
            while(true){
                i++;
                int X = x+d.x*i;
                int Y = y+d.y*i;
                int Z = z+d.z*i;
                if(X<0||Y<0||Z<0||X>=msr.design.length||Y>=msr.design[0].length||Z>=msr.design[0][0].length){
                    possible.put(new int[]{x+d.x*(i-1),y+d.y*(i-1),z+d.z*(i-1)}, i);
                    break;
                }
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Block b = msr.design[X][Y][Z];
                if(b==null)continue;//air
                if(b.fuelVessel!=null||b.reflector!=null||b.irradiator!=null)break;
            }
        }
        ArrayList<int[]> keys = new ArrayList<>(possible.keySet());
        Collections.sort(keys, (o1, o2) -> {
            return possible.get(o1)-possible.get(o2);
        });
        for(int[] key : keys){
            net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Block was = msr.design[key[0]][key[1]][key[2]];
            if(tryAddNeutronSource(msr, source, key[0], key[1], key[2]))break;
        }
    }
    private static boolean tryAddNeutronSource(OverhaulMSRDesign msr, net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Block source, int X, int Y, int Z){
        net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Block b = msr.design[X][Y][Z];
        if(b!=null&&(b.neutronSource!=null))return false;
        msr.design[X][Y][Z] = source;
        return true;
    }
}