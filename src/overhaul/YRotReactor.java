package overhaul;
import java.util.ArrayList;
import java.util.Random;
public abstract class YRotReactor extends Reactor{
    public YRotReactor(Fuel fuel, Fuel.Type type, int x, int y, int z){
        super(fuel, type, x, y, z);
    }
    public YRotReactor(Fuel fuel, Fuel.Type type, int x, int y, int z, boolean symmetryX, boolean symmetryY, boolean symmetryZ){
        super(fuel, type, x, y, z, symmetryX, symmetryY, symmetryZ);
    }
    public static Reactor random(Fuel fuel, Fuel.Type type, int x, int y, int z, Random rand){
        return random(fuel, type, x, y, z, rand, null);
    }
    public static Reactor random(Fuel fuel, Fuel.Type type, int x, int y, int z, Random rand, ArrayList<ReactorPart> allowedParts){
        return new Reactor(fuel, type, x, y, z){
            @Override
            protected ReactorPart build(int X, int Y, int Z){
                return ReactorPart.random(rand, allowedParts);
            }
        };
    }
    public static Reactor random(Fuel fuel, Fuel.Type type, int x, int y, int z, Random rand, boolean xSymm, boolean ySymm, boolean zSymm){
        return random(fuel, type, x, y, z, rand, xSymm, ySymm, zSymm, null);
    }
    public static Reactor random(Fuel fuel, Fuel.Type type, int x, int y, int z, Random rand, boolean xSymm, boolean ySymm, boolean zSymm, ArrayList<ReactorPart> allowedParts){
        return new YRotReactor(fuel, type, x, y, z, xSymm, ySymm, zSymm){
            @Override
            protected ReactorPart build(int X, int Y, int Z){
                return ReactorPart.random(rand, allowedParts);
            }
        };
    }
    @Override
    protected void applyExtraTransformations(){
        for(int Y = y/2-1; Y<y; Y++){
            ReactorPart[][] layerCopy = new ReactorPart[x][z];
            for(int X = 0; X<x; X++){
                for(int Z = 0; Z<z; Z++){
                    layerCopy[x-X-1][z-Z-1] = parts[X][Y][Z];
                }
            }
            for(int X = 0; X<x; X++){
                for(int Z = 0; Z<z; Z++){
                    parts[X][Y][Z] = layerCopy[X][Z];
                }
            }
        }
    }
}