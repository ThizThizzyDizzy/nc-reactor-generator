package overhaul;
public class PlacementRule{
    public static PlacementRule atLeast(int num, ReactorBit bit){
        return between(num, Integer.MAX_VALUE, bit);//I could do a hard-coded 6 sides, but what if you're in the fourth dimension?
    }
    public static PlacementRule exactly(int num, ReactorBit bit){
        return between(num, num, bit);
    }
    public static PlacementRule between(int min, int max, ReactorBit bit){
        return new PlacementRule(Type.BETWEEN, min, max, bit);
    }
    public static PlacementRule axis(ReactorBit bit){
        return new PlacementRule(Type.AXIS, 0, 0, bit);
    }
    private final Type type;
    private final int min;
    private final int max;
    private final ReactorBit bit;
    public PlacementRule(Type type, int min, int max, ReactorBit bit){
        this.type = type;
        this.min = min;
        this.max = max;
        this.bit = bit;
    }
    private boolean isValid(Reactor reactor, int x, int y, int z, boolean checkActive){
        switch(type){
            case AXIS:
                if(reactor.matches(x+1, y, z, bit, checkActive)&&reactor.matches(x-1, y, z, bit, checkActive))return true;
                if(reactor.matches(x, y+1, z, bit, checkActive)&&reactor.matches(x, y-1, z, bit, checkActive))return true;
                if(reactor.matches(x, y, z+1, bit, checkActive)&&reactor.matches(x, y, z-1, bit, checkActive))return true;
                return false;
            case BETWEEN:
                int count = 0;
                if(reactor.matches(x+1, y, z, bit, checkActive))count++;
                if(reactor.matches(x-1, y, z, bit, checkActive))count++;
                if(reactor.matches(x, y+1, z, bit, checkActive))count++;
                if(reactor.matches(x, y-1, z, bit, checkActive))count++;
                if(reactor.matches(x, y, z+1, bit, checkActive))count++;
                if(reactor.matches(x, y, z-1, bit, checkActive))count++;
                return count>=min&&count<=max;
            default:
                throw new IllegalArgumentException("Unknown Placement Rule Type: "+type.toString()+"!");
        }
    }
    public boolean isValid(Reactor reactor, int x, int y, int z){
        return isValid(reactor, x, y, z, false);
    }
    public boolean isActive(Reactor reactor, int x, int y, int z){
        return isValid(reactor, x, y, z, true);
    }
    private static enum Type{
        BETWEEN,AXIS
    }
}