package net.ncplanner.plannerator.multiblock;
public enum Edge{
    PXPY(Direction.PX,Direction.PY),
    PXNY(Direction.PX,Direction.NY),
    NXPY(Direction.NX,Direction.PY),
    NXNY(Direction.NX,Direction.NY),
    
    PXPZ(Direction.PX,Direction.PZ),
    PXNZ(Direction.PX,Direction.NZ),
    NXPZ(Direction.NX,Direction.PZ),
    NXNZ(Direction.NX,Direction.NZ),
    
    PYPZ(Direction.PY,Direction.PZ),
    PYNZ(Direction.PY,Direction.NZ),
    NYPZ(Direction.NY,Direction.PZ),
    NYNZ(Direction.NY,Direction.NZ);
    public final Direction[] directions;
    private Edge(Direction... directions){
        this.directions = directions;
    }
}