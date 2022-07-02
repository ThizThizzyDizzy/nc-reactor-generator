package net.ncplanner.plannerator.multiblock;
public enum Direction{
    PX(1,0,0),
    PY(0,1,0),
    PZ(0,0,1),
    NX(-1,0,0),
    NY(0,-1,0),
    NZ(0,0,-1);
    public static Direction get(int x1, int y1, int z1, int x2, int y2, int z2){
        return get(x2-x1, y2-y1, z2-z1);
    }
    public static Direction get(int x, int y, int z){
        if(x!=0)x/=Math.abs(x);
        if(y!=0)y/=Math.abs(y);
        if(z!=0)z/=Math.abs(z);
        for(Direction d : values()){
            if(d.x==x&&d.y==y&&d.z==z)return d;
        }
        return null;
    }
    public final int x;
    public final int y;
    public final int z;
    private Direction(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Direction getOpposite(){
        for(Direction d : values()){
            if(d.x==-x&&d.y==-y&&d.z==-z)return d;
        }
        return null;
    }
    @Override
    public String toString(){
        return super.toString().replace('N', '-').replace('P', '+');
    }
}