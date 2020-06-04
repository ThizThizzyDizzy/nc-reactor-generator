package overhaul;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Random;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.imageio.ImageIO;
public class ReactorPart implements ReactorBit{
    public static final ArrayList<ReactorPart> parts = new ArrayList<>();//TODO update cooling values https://docs.google.com/spreadsheets/d/1zo8frawlKxA--vsu_dTYUKl1jtTdc-whzMTAVY9-4bs/edit#gid=606129178
    public static final ArrayList<ReactorPart> GROUP_CORE = new ArrayList<>();
    public static final ArrayList<ReactorPart> GROUP_CELLS = new ArrayList<>();
    public static final ArrayList<ReactorPart> GROUP_HEATSINK = new ArrayList<>();
    public static final ArrayList<ReactorPart> GROUP_MODERATOR = new ArrayList<>();
    public static final ArrayList<ReactorPart> GROUP_REFLECTOR = new ArrayList<>();
    public static final ReactorPart AIR = air();
    public static FuelCell BEST_CELL;
    public static final FuelCell FUEL_CELL = fuelCell(null, "{FUEL};False;None");
    public static final FuelCell FUEL_CELL_CF_252 = fuelCell("Cf-252", "{FUEL};True;Cf-252");
    public static final FuelCell FUEL_CELL_PO_BE = fuelCell("Po-Be", "{FUEL};True;Po-Be");
    public static final FuelCell FUEL_CELL_RA_BE = fuelCell("Ra-Be", "{FUEL};True;Ra-Be");
    public static final Heatsink HEATSINK_WATER = heatsink("Water", "Water", PlacementRule.atLeast(1, Type.FUEL_CELL));
    public static final Heatsink HEATSINK_IRON = heatsink("Iron", "Iron", PlacementRule.atLeast(1, Type.MODERATOR));
    public static final Heatsink HEATSINK_REDSTONE = heatsink("Redstone", "Redstone", PlacementRule.atLeast(1, Type.FUEL_CELL), PlacementRule.atLeast(1, Type.MODERATOR));
    public static final Heatsink HEATSINK_QUARTZ = heatsink("Quartz", "Quartz", PlacementRule.atLeast(1, HEATSINK_REDSTONE));
    public static final Heatsink HEATSINK_GLOWSTONE = heatsink("Glowstone", "Glowstone", PlacementRule.atLeast(2, Type.MODERATOR));
    public static final Heatsink HEATSINK_OBSIDIAN = heatsink("Obsidian", "Obsidian", PlacementRule.axis(HEATSINK_GLOWSTONE));
    public static final Heatsink HEATSINK_NETHER_BRICK = heatsink("Nether Brick", "NetherBrick", PlacementRule.atLeast(1, HEATSINK_OBSIDIAN));
    public static final Heatsink HEATSINK_LAPIS = heatsink("Lapis", "Lapis", PlacementRule.atLeast(1, Type.FUEL_CELL), PlacementRule.atLeast(1, Type.CASING));
    public static final Heatsink HEATSINK_GOLD = heatsink("Gold", "Gold", PlacementRule.atLeast(2, HEATSINK_IRON));
    public static final Heatsink HEATSINK_PRISMARINE = heatsink("Prismarine", "Prismarine", PlacementRule.atLeast(2, HEATSINK_WATER));
    public static final Heatsink HEATSINK_LEAD = heatsink("Lead", "Lead", PlacementRule.atLeast(1, HEATSINK_IRON));
    public static final Heatsink HEATSINK_SLIME = heatsink("Slime", "Slime", PlacementRule.exactly(1, HEATSINK_WATER), PlacementRule.atLeast(2, HEATSINK_LEAD));
    public static final Heatsink HEATSINK_END_STONE = heatsink("End Stone", "EndStone", PlacementRule.atLeast(1, Type.REFLECTOR));
    public static final Heatsink HEATSINK_PURPUR = heatsink("Purpur", "Purpur", PlacementRule.exactly(1, HEATSINK_IRON), PlacementRule.atLeast(1, HEATSINK_END_STONE));
    public static final Heatsink HEATSINK_DIAMOND = heatsink("Diamond", "Diamond", PlacementRule.atLeast(1, HEATSINK_GOLD),PlacementRule.atLeast(1, Type.FUEL_CELL));
    public static final Heatsink HEATSINK_EMERALD = heatsink("Emerald", "Emerald", PlacementRule.atLeast(1, HEATSINK_PRISMARINE),PlacementRule.atLeast(1, Type.MODERATOR));
    public static final Heatsink HEATSINK_COPPER = heatsink("Copper", "Copper", PlacementRule.atLeast(1, HEATSINK_WATER));
    public static final Heatsink HEATSINK_TIN = heatsink("Tin", "Tin", PlacementRule.axis(HEATSINK_LAPIS));
    public static final Heatsink HEATSINK_BORON = heatsink("Boron", "Boron", PlacementRule.exactly(1, HEATSINK_QUARTZ), PlacementRule.atLeast(1, Type.CASING));
    public static final Heatsink HEATSINK_LITHIUM = heatsink("Lithium", "Lithium", PlacementRule.axis(HEATSINK_LEAD), PlacementRule.atLeast(1, Type.CASING));
    public static final Heatsink HEATSINK_MAGNESIUM = heatsink("Magnesium", "Magnesium", PlacementRule.exactly(1, Type.MODERATOR), PlacementRule.atLeast(1, Type.CASING));
    public static final Heatsink HEATSINK_MANGANESE = heatsink("Manganese", "Manganese", PlacementRule.atLeast(2, Type.FUEL_CELL));
    public static final Heatsink HEATSINK_ALUMINUM = heatsink("Aluminum", "Aluminum", PlacementRule.atLeast(1, HEATSINK_QUARTZ), PlacementRule.atLeast(1, HEATSINK_LAPIS));
    public static final Heatsink HEATSINK_SILVER = heatsink("Silver", "Silver", PlacementRule.atLeast(2, HEATSINK_GLOWSTONE), PlacementRule.atLeast(1, HEATSINK_TIN));
    public static final Heatsink HEATSINK_FLUORITE = heatsink("Fluorite", "Fluorite", PlacementRule.atLeast(1, HEATSINK_GOLD), PlacementRule.atLeast(1, HEATSINK_PRISMARINE));
    public static final Heatsink HEATSINK_VILLIAUMITE = heatsink("Villiaumite", "Villiaumite", PlacementRule.atLeast(1, HEATSINK_END_STONE), PlacementRule.atLeast(1, HEATSINK_REDSTONE));
    public static final Heatsink HEATSINK_CAROBBIITE = heatsink("Carobbiite", "Carobbiite", PlacementRule.atLeast(1, HEATSINK_COPPER), PlacementRule.atLeast(1, HEATSINK_END_STONE));
    public static final Heatsink HEATSINK_ARSENIC = heatsink("Arsenic", "Arsenic", PlacementRule.axis(Type.REFLECTOR));
    public static final Heatsink HEATSINK_NITROGEN = heatsink("Nitrogen", "Nitrogen", PlacementRule.atLeast(2, HEATSINK_COPPER), PlacementRule.atLeast(1, HEATSINK_PURPUR));
    public static final Heatsink HEATSINK_HELIUM = heatsink("Helium", "Helium", PlacementRule.exactly(2, HEATSINK_REDSTONE), PlacementRule.atLeast(1, Type.CASING));
    public static final Heatsink HEATSINK_ENDERIUM = heatsink("Enderium", "Enderium", PlacementRule.atLeast(3, Type.MODERATOR));
    public static final Heatsink HEATSINK_CRYOTHEUM = heatsink("Cryotheum", "Cryotheum", PlacementRule.atLeast(3, Type.FUEL_CELL));
    public static final Moderator BERYLLIUM = moderator("Beryllium", "Beryllium");
    public static final Moderator GRAPHITE = moderator("Graphite", "Graphite");
    public static final Moderator HEAVY_WATER = moderator("Heavy Water", "HeavyWater");
    public static final NeutronShield SHIELD_BORON_SILVER = shield("Boron-Silver", "Boron-Silver");
    public static final ReactorPart CONDUCTOR = conductor();
    public static final Reflector REFLECTOR_BERYLLIUM_CARBON = reflector("Beryllium-Carbon", "Beryllium-Carbon");
    public static final Reflector REFLECTOR_LEAD_STEEL = reflector("Lead-Steel", "Lead-Steel");
    public static ReactorPart CASING = new ReactorPart(Type.CASING, "Casing", null, null);
    private static <T extends ReactorPart> T add(T p){
        parts.add(p);
        return p;
    }
    private static ReactorPart air(){
        ReactorPart part = new ReactorPart(Type.AIR, "Air", null, "air");
        return add(part);
    }
    private static FuelCell fuelCell(String name, String jsonName){
        FuelCell part = new FuelCell(name, jsonName);
        GROUP_CORE.add(part);
        return add(part);
    }
    private static Heatsink heatsink(String name, String jsonName, PlacementRule... rules){
        Heatsink part = new Heatsink(name, jsonName, rules);
        GROUP_HEATSINK.add(part);
        return add(part);
    }
    private static Moderator moderator(String name, String jsonName){
        Moderator part = new Moderator(name, jsonName);
        GROUP_CORE.add(part);
        GROUP_MODERATOR.add(part);
        return add(part);
    }
    private static NeutronShield shield(String name, String jsonName){
        NeutronShield part = new NeutronShield(name, jsonName);
        GROUP_CORE.add(part);
        GROUP_MODERATOR.add(part);
        return add(part);
    }
    private static ReactorPart conductor(){
        ReactorPart part = new ReactorPart(Type.CONDUCTOR, "Conductor", "Conductors", "conductor");
        return add(part);
    }
    private static Reflector reflector(String name, String jsonName){
        Reflector part = new Reflector(name, jsonName);
        GROUP_CORE.add(part);
        GROUP_REFLECTOR.add(part);
        return add(part);
    }
    public static ReactorPart random(Random rand){
        return random(rand, null);
    }
    public static ArrayList<ReactorPart> getSelectedParts(){
        int[] is = Main.instance.listParts.getSelectedIndices();
        ArrayList<ReactorPart> selected = new ArrayList<>();
        for(int i = 0; i<is.length; i++){
            selected.add(parts.get(is[i]));
        }
        return selected;
    }
    public static ReactorPart random(Random rand, ArrayList<ReactorPart> allowedParts){
        if(allowedParts==null){
            int[] is = Main.instance.listParts.getSelectedIndices();
            if(is.length==0)return ReactorPart.AIR;
            return parts.get(is[rand.nextInt(is.length)]);
        }
        if(allowedParts.isEmpty())return AIR;
        return allowedParts.get(rand.nextInt(allowedParts.size()));
    }
    public static PartContainer parse(String string){
        if(string.equals("Reflectors"))return new PartContainer(REFLECTOR_BERYLLIUM_CARBON);
        if(string.contains(";")){
            String[] strs = string.split("\\Q;");
            ReactorPart part = null;
            Fuel fuel = Fuel.parse(strs[0]);
            Fuel.Type type = Fuel.Type.parse(strs[0]);
            if(strs.length==2)part = FUEL_CELL_CF_252;//pre-source compat
            else{
                boolean b = Boolean.parseBoolean(strs[1]);
                if(b){
                    String source = strs[2];
                    if(source.equals("Self")||source.equals("None"))part = FUEL_CELL;
                    for(ReactorPart p : parts){
                        if(p.type!=Type.FUEL_CELL)continue;
                        if(p.name.contains(source))part = p;
                    }
                }else{//no source
                    part = FUEL_CELL;
                }
            }
            if(part==null)return null;
            return new PartContainer(part, fuel, type);
        }
        for(ReactorPart part : parts){
            if(part.name.replace(" ", "_").equalsIgnoreCase(string))return new PartContainer(part);
        }
        for(ReactorPart part : parts){
            if(part.name.toLowerCase().replace(" ", "_").startsWith(string.toLowerCase()))return new PartContainer(part);
        }
        for(ReactorPart part : parts){
            if(part.name.toLowerCase().replace(" ", "_").contains(string.toLowerCase()))return new PartContainer(part);
        }
        for(ReactorPart part : parts){
            if(part.name.toLowerCase().replace(" ", "").startsWith(string.toLowerCase()))return new PartContainer(part);
        }
        for(ReactorPart part : parts){
            if(part.name.toLowerCase().replace(" ", "").contains(string.toLowerCase()))return new PartContainer(part);
        }
        for(ReactorPart part : parts){
            if(string.toLowerCase().replace(" ", "_").contains(part.name.toLowerCase()))return new PartContainer(part);
        }
        for(ReactorPart part : parts){
            if(string.toLowerCase().replace(" ", "").contains(part.name.toLowerCase()))return new PartContainer(part);
        }
        if(string.contains(" "))return parse(string.replace(" ", "").trim());
        return null;
    }
    public final Type type;
    private final String name;
    private BufferedImage image;
    private final String texture;
    /**
     * For exporting to Hellrage's Reactor Planner
     */
    public String jsonName;
    public ReactorPart(Type type, String name, String jsonName, String texture){
        this.type = type;
        this.name = name;
        this.jsonName = jsonName;
        this.texture = texture;
    }
    @Override
    public String toString(){
        return name;
    }
    boolean matches(ReactorBit bit){
        return this==bit||type==bit;
    }
    boolean matches(Iterable<ReactorBit> bits){
        for(ReactorBit bit : bits){
            if(matches(bit))return true;
        }
        return false;
    }
    public BufferedImage getImage(){
        if(image!=null)return image;
        try{
            if(new File("nbproject").exists()){
                image = ImageIO.read(new File("src\\textures\\"+texture.replace("/", "\\")+".png"));
            }else{
                JarFile jar = new JarFile(new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath().replace("%20", " ")));
                Enumeration enumEntries = jar.entries();
                while(enumEntries.hasMoreElements()){
                    JarEntry file = (JarEntry)enumEntries.nextElement();
                    if(file.getName().equals("textures/"+texture.replace("\\", "/")+".png")){
                        image = ImageIO.read(jar.getInputStream(file));
                        break;
                    }
                }
            }
        }catch(IOException ex){
            System.err.println("Couldn't read file: "+texture);
            image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        }
        return image;
    }
    public static enum Type implements ReactorBit{
        AIR(false),
        FUEL_CELL(true),
        HEATSINK(true),
        MODERATOR(false),
        CONDUCTOR(false),
        REFLECTOR(false),
        CASING(false);
        public final boolean canCluster;//For some reason, this is the only type-specific variable that I didn't hard-code everywhere (like Line-of-sight)
        private Type(boolean canCluster){
            this.canCluster = canCluster;
        }
    }
    public static class PartContainer{
        public final ReactorPart part;
        public final Fuel fuel;
        public final Fuel.Type type;
        public PartContainer(ReactorPart part){
            this(part, null, null);
        }
        public PartContainer(ReactorPart part, Fuel fuel, Fuel.Type type){
            this.part = part;
            this.fuel = fuel;
            this.type = type;
        }
    }
}