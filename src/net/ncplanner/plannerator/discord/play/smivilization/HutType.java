package net.ncplanner.plannerator.discord.play.smivilization;
public enum HutType{
    STANDARD("hut", "campfire", 32),
    NIGHT("nighttime hut", "campfire", 48),
    SPACE("space hut", "sun", 256),
    TROPICAL("tropical hut", "campfire", 192),
    WASTELAND("wasteland hut", "radioactive campfire", 128),
    WINTER("winter hut", "fire", 64);
    public final String name;
    public final String campfireName;
    private final long price;
    private HutType(String name, String campfireName, long price){
        this.name = name;
        this.campfireName = campfireName;
        this.price = price;
    }
    public long getPrice(){
        return price;
    }
}