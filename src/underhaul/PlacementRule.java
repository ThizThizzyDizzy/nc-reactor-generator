package underhaul;
public class PlacementRule{
    public static PlacementRule or(PlacementRule... rules){
        return new PlacementRule(Type.OR, 0, 0, null, rules);
    }
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
    public static PlacementRule noPancake(){
        return new PlacementRule(Type.NO_PANCAKE, 0, 0, null);
    }
    final Type type;
    final int min;
    final int max;
    final ReactorBit bit;
    final PlacementRule[] rules;
    public PlacementRule(Type type, int min, int max, ReactorBit bit, PlacementRule... rules){
        this.type = type;
        this.min = min;
        this.max = max;
        this.bit = bit;
        this.rules = rules;
    }
    private boolean isValid(Reactor reactor, int x, int y, int z, boolean checkActive){
        switch(type){
            case OR:
                for(PlacementRule rule : rules){
                    if(rule.isValid(reactor, x, y, z, checkActive))return true;
                }
                return false;
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
            case NO_PANCAKE:
                return reactor.x>1&&reactor.y>1&&reactor.z>1;
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
    public static enum Type{
        BETWEEN,AXIS,OR,NO_PANCAKE;
    }
}