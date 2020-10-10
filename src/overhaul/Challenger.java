package overhaul;
import common.SettingBoolean;
import common.SettingDouble;
import common.SettingInt;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import static overhaul.GenerationModel.models;
import static overhaul.Priority.priorities;
import static overhaul.Priority.presets;
public class Challenger{
    public static void init(){
        models.add(new GenerationModel("Challenger", "Generates reactors for the reactor-challenges channel\n\nGenerates one random and one modified-from-best reactor per cycle using only cell structures until the stats beat the best one\nSymmetry is encouraged, but not forced.\nThen, without changing the core structure, tries to cool the reactor", new SettingInt("Target Output", 0, 0), new SettingDouble("Target Efficiency", 0, 0), new SettingBoolean("Target Shutdownable", false), new SettingInt("Target Irradiation", 0), new SettingDouble("Change Chance", 1, 0.1, 100, .1), new SettingDouble("Morph Chance", .1, 0.01, 100, .1), new SettingBoolean("Epicenter mode", false), new SettingDouble("Epicenter Magnitude", 10, 1), new SettingBoolean("Lock Core", false)){
            @Override
            public Reactor generate(Reactor last, int x, int y, int z, Random rand){
                if(last==null)return Reactor.random(x, y, z, rand);
                if(score(last)>=1){//core is good, time to cool
                    if(getBoolean("Epicenter")){
                        int epicenterX = rand.nextInt(last.x);
                        int epicenterY = rand.nextInt(last.y);
                        int epicenterZ = rand.nextInt(last.z);
                        return new Reactor(x, y, z, rand.nextBoolean(), rand.nextBoolean(), rand.nextBoolean()){
                            @Override
                            protected ReactorPart build(int X, int Y, int Z){
                                double dist = Math.sqrt(Math.pow(X-epicenterX,2)+Math.pow(Y-epicenterY,2)+Math.pow(Z-epicenterZ,2));
                                double weight = getDouble("Epicenter Magnitude")/dist;
                                if(rand.nextDouble()*weight<getDouble("Morph Chance")/100d){
                                    return ReactorPart.random(rand);
                                }
                                if(rand.nextDouble()*weight<getDouble("Change Chance")/100d){
                                    ReactorPart.Type type = ReactorPart.Type.AIR;
                                    if(parts[X][Y][Z]!=null)type = parts[X][Y][Z].type;
                                    switch(type){
                                        case FUEL_CELL:
                                            return getBoolean("Lock Core")?last.parts[X][Y][Z]:ReactorPart.BEST_CELL;
                                        case HEATSINK:
                                        case AIR:
                                        case CONDUCTOR:
                                            return ReactorPart.random(rand, ReactorPart.GROUP_HEATSINK);
                                        case MODERATOR:
                                            return getBoolean("Lock Core")?last.parts[X][Y][Z]:ReactorPart.random(rand, ReactorPart.GROUP_MODERATOR);
                                        case REFLECTOR:
                                            return getBoolean("Lock Core")?last.parts[X][Y][Z]:ReactorPart.random(rand, ReactorPart.GROUP_REFLECTOR);
                                        case IRRADIATOR:
                                            return ReactorPart.IRRADIATOR;
                                    }
                                    return ReactorPart.random(rand);
                                }else{
                                    return last.parts[X][Y][Z];
                                }
                            }
                            @Override
                            protected Fuel.Group buildFuel(int X, int Y, int Z){
                                return (getBoolean("Lock Core")||rand.nextDouble()>getDouble("Change Chance")/100d)?Fuel.Group.test(last.fuel[X][Y][Z], last.fuelType[X][Y][Z], randomFuel(this)):randomFuel(this);
                            }
                        };
                    }else{
                        return new Reactor(x, y, z, rand.nextBoolean(), rand.nextBoolean(), rand.nextBoolean()){
                            @Override
                            protected ReactorPart build(int X, int Y, int Z){
                                if(rand.nextDouble()<getDouble("Morph Chance")/100d){
                                    return ReactorPart.random(rand);
                                }
                                if(rand.nextDouble()<getDouble("Change Chance")/100d){
                                    ReactorPart.Type type = ReactorPart.Type.AIR;
                                    if(parts[X][Y][Z]!=null)type = parts[X][Y][Z].type;
                                    switch(type){
                                        case FUEL_CELL:
                                            return getBoolean("Lock Core")?last.parts[X][Y][Z]:ReactorPart.BEST_CELL;
                                        case HEATSINK:
                                        case AIR:
                                        case CONDUCTOR:
                                            return ReactorPart.random(rand, ReactorPart.GROUP_HEATSINK);
                                        case MODERATOR:
                                            return getBoolean("Lock Core")?last.parts[X][Y][Z]:ReactorPart.random(rand, ReactorPart.GROUP_MODERATOR);
                                        case REFLECTOR:
                                            return getBoolean("Lock Core")?last.parts[X][Y][Z]:ReactorPart.random(rand, ReactorPart.GROUP_REFLECTOR);
                                    }
                                    return ReactorPart.random(rand);
                                }else{
                                    return last.parts[X][Y][Z];
                                }
                            }
                            @Override
                            protected Fuel.Group buildFuel(int X, int Y, int Z){
                                return (getBoolean("Lock Core")||rand.nextDouble()>getDouble("Change Chance")/100d)?Fuel.Group.test(last.fuel[X][Y][Z], last.fuelType[X][Y][Z], randomFuel(this)):randomFuel(this);
                            }
                        };
                    }
                }
                ArrayList<ReactorPart> allowed = new ArrayList<>(ReactorPart.GROUP_CORE);
                allowed.removeAll(ReactorPart.GROUP_CELLS);
                allowed.add(ReactorPart.BEST_CELL);
                Reactor random = Reactor.random(x, y, z, rand, rand.nextBoolean(), rand.nextBoolean(), rand.nextBoolean(), allowed);
                Reactor modified = new Reactor(x, y, z, rand.nextBoolean(), rand.nextBoolean(), rand.nextBoolean()){
                    @Override
                    protected ReactorPart build(int X, int Y, int Z){
                        if(rand.nextDouble()<getDouble("Change Chance")/100d){
                            return ReactorPart.random(rand, allowed);
                        }else{
                            return last.parts[X][Y][Z];
                        }
                    }
                    @Override
                    protected Fuel.Group buildFuel(int X, int Y, int Z){
                        return (getBoolean("Lock Core")||rand.nextDouble()>getDouble("Change Chance")/100d)?Fuel.Group.test(last.fuel[X][Y][Z], last.fuelType[X][Y][Z], randomFuel(this)):randomFuel(this);
                    }
                };
                double randScore = score(random);
                double modScore = score(modified);
                if(randScore>=1&&(modScore<1||randScore<modScore))return random;
                if(modScore>=1&&(randScore<1||modScore<randScore))return modified;
                return modScore>=randScore?modified:random;
            }
        });
        priorities.add(new Priority("Challenge"){
            @Override
            protected double doCompare(Reactor main, Reactor other){
                double mainScore = score(main);
                double otherScore = score(other);
                return mainScore-otherScore;
            }
        });
        presets.add(new Priority.Preset("Challenge", "Valid (>0 output)", "Minimize Bad Cells", "Challenge", "Stability"));
    }
    public static double score(Reactor r){
        double targetOutput = GenerationModel.get("Challenger").getInteger("Output");
        double targetEfficiency = GenerationModel.get("Challenger").getDouble("Efficiency");
        double targetIrradiation = GenerationModel.get("Challenger").getInteger("Irradiation");
        double targetShutdownable = GenerationModel.get("Challenger").getBoolean("Shutdown")?1:0;
        double outputScore = Math.max(0,Math.min(1,r.totalOutput/targetOutput));
        double efficiencyScore = Math.max(0,Math.min(1,r.totalEfficiency/targetEfficiency));
        double irradiationScore = Math.max(0,Math.min(1,r.totalIrradiation/targetIrradiation));
        double shutdownScore = Math.max(0,Math.min(1,r.getShutdownFactor()/targetShutdownable));
        
        if(Double.isNaN(outputScore))outputScore = 1;
        if(Double.isNaN(efficiencyScore))efficiencyScore = 1;
        if(Double.isNaN(irradiationScore))irradiationScore = 1;
        if(Double.isNaN(shutdownScore))shutdownScore = 1;
        return outputScore*efficiencyScore*irradiationScore*shutdownScore;
    }
    public static boolean canAddFuel(Fuel.Group f, Reactor r){
        return true;
    }
    private static Fuel.Group randomFuel(Reactor r){
        Fuel.Group g = Main.instance.randomFuel();
        if(canAddFuel(g, r))return g;
        return randomFuel(r);
    }
}