package planner.menu.configuration.tree;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import multiblock.configuration.AbstractPlacementRule;
import multiblock.configuration.IBlockTemplate;
import multiblock.configuration.IBlockType;
import multiblock.configuration.RuleContainer;
import planner.Core;
import planner.menu.component.MenuComponentLabel;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.configuration.ConfigurationMenu;
import simplelibrary.Stack;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuPlacementRuleTree<Container extends RuleContainer<BlockType, Template>, BlockType extends IBlockType, Template extends IBlockTemplate> extends ConfigurationMenu{
    private ArrayList<ArrayList<TreeElement>> tiers = new ArrayList<>();
    private final MenuComponentMinimaList list;
    public ArrayList<Object> highlighted = null;
    private final MenuComponentLabel lbl;
    public MenuPlacementRuleTree(GUI gui, Menu parent, ArrayList<Container> blocks){
        super(gui, parent, null, "Rule Tree");
        ArrayList<Object> allThings = new ArrayList<>();
        for(Container r : blocks){
            if(r.rules.isEmpty())continue;
            for(Object thing : getThings(r)){
                if(!allThings.contains(thing))allThings.add(thing);
            }
        }
        HashMap<Object, Integer> tieredThings = new HashMap<>();
        if(Core.isControlPressed()){
            for(Object o : allThings){
                if(o instanceof RuleContainer){
                    tieredThings.put(o, getThingCount((Container)o));
                }else tieredThings.put(o, 0);
            }
        }else{
            int tier = 0;
            while(!allThings.isEmpty()){
                ArrayList<Object> thisTier = new ArrayList<>();
                for(Object o : allThings){
                    if(o instanceof RuleContainer&&!((RuleContainer)o).rules.isEmpty()){
                        ArrayList<Object> theseThings = getThings((Container)o);
                        theseThings.removeAll(tieredThings.keySet());
                        theseThings.remove(o);
                        if(theseThings.isEmpty())thisTier.add(o);
                    }else thisTier.add(o);
                }
                for(Object o : thisTier)tieredThings.put(o, tier);
                allThings.removeAll(thisTier);
                if(thisTier.isEmpty()){
                    for(Object o : allThings){
                        if(o instanceof IBlockTemplate)System.out.println(((IBlockTemplate)o).getDisplayName());
                        else System.out.println(o.toString());
                    }
                    break;
                }
                tier++;
            }
        }
        ArrayList<TreeElement> things = new ArrayList<>();
        for(Object thing : tieredThings.keySet()){
            if(thing instanceof IBlockType){
                things.add(new TreeElement((BlockType)thing, tieredThings.get(thing)));
            }
            if(thing instanceof IBlockTemplate){
                things.add(new TreeElement((Template)thing, tieredThings.get(thing)));
            }
        }
        for(TreeElement element : things){
            while(tiers.size()<=element.tier)tiers.add(new ArrayList<>());
            tiers.get(element.tier).add(element);
        }
        list = add(new MenuComponentMinimaList(sidebar.width, 0, 0, 0, 20));
        refresh();
        addToSidebarBottom(lbl = new MenuComponentLabel(0, 0, 0, 32, "", true));
    }
    private boolean running = true;
    @Override
    public void onGUIOpened(){
        super.onGUIOpened();
        Thread t = new Thread(() -> {
            while(running){
                improveShuffle();
                improveShift();
                if(System.currentTimeMillis()-lastImprovement>10000){
                    running = false;
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }
    @Override
    public synchronized void tick(){
        super.tick();
        lbl.text = Math.round(getFullTreeLength())+(running?"...":"");
    }
    @Override
    public void onGUIClosed(){
        super.onGUIClosed();
        running = false;
    }
    long lastImprovement = System.currentTimeMillis();
    private Random rand = new Random();
    private synchronized void improveShuffle(){
        double preLen = getFullTreeLength();
        ArrayList<ArrayList<TreeElement>> tiersCopy = new ArrayList<>();
        for(ArrayList<TreeElement> tier : tiers)tiersCopy.add(new ArrayList<>(tier));
        int i = rand.nextBoolean()?-1:rand.nextInt(tiers.size());
        for(int j = 0; j<tiers.size(); j++){
            ArrayList<TreeElement> tier = tiers.get(j);
            if(i==-1||i==j)Collections.shuffle(tier);
        }
        refresh();
        double postLen = getFullTreeLength();
        if(preLen<=postLen){
            tiers = tiersCopy;
            refresh();
        }else{
            lastImprovement = System.currentTimeMillis();
        }
    }
    private synchronized void improveShift(){
        double preLen = getFullTreeLength();
        ArrayList<ArrayList<TreeElement>> tiersCopy = new ArrayList<>();
        for(ArrayList<TreeElement> tier : tiers)tiersCopy.add(new ArrayList<>(tier));
        int idx = rand.nextInt(tiers.size());
        ArrayList<TreeElement> tier = tiers.get(idx);
        if(tier.isEmpty())return;
        int idx1 = rand.nextInt(tier.size());
        int idx2 = rand.nextInt(tier.size());
        tier.add(idx2, tier.remove(idx1));
        refresh();
        double postLen = getFullTreeLength();
        if(preLen<=postLen){
            tiers = tiersCopy;
            refresh();
        }else{
            lastImprovement = System.currentTimeMillis();
        }
    }
    @Override
    public synchronized void render(int millisSinceLastTick){
        super.render(millisSinceLastTick);
        list.width = gui.helper.displayWidth()-sidebar.width;
        list.height = gui.helper.displayHeight();
        arrange();
    }
    private synchronized void arrange(){
        int num = 1;
        for(ArrayList<TreeElement> tier : tiers){
            num = Math.max(num, tier.size()+1);
        }
        double size = (list.width-list.vertScrollbarWidth)/num;
        for(MenuComponent component : list.components){
            double numEmpty = num-component.components.size();
            component.width = list.width-list.vertScrollbarWidth;
            component.height = size;
            for(int i = 0; i<component.components.size(); i++){
                MenuComponent comp = component.components.get(i);
                comp.width = comp.height = size;
                comp.x = i*size+(i==0?0:numEmpty*size/2);
            }
        }
        double Y = 0;
        for(int j = 0; j<list.components.size(); j++){
            list.components.get(j).y = Y;
            Y+=list.components.get(j).height;
        }
    }
    private synchronized void refresh(){
        list.components.clear();
        for(int tier = 0; tier<tiers.size(); tier++){
            if(tiers.get(tier).isEmpty())continue;
            final int t = tier;
            MenuComponent parentComponent = new MenuComponent(0, 0, 0, 0){
                @Override
                public void render(){}
            };
                parentComponent.add(new MenuComponent(0, 0, 0, 0){
                    @Override
                    public void render(){
                        Core.applyColor(Core.theme.getComponentTextColor(0));
                        drawCenteredText(x, y+height/3, x+width, y+height*2/3, t+"");
                    }
                });
            for(TreeElement element : tiers.get(tier)){
                parentComponent.add(new MenuComponentTreeElement(this, element));
            }
            list.add(parentComponent);
        }
        arrange();
    }
    @Override
    public synchronized void onMouseButton(double x, double y, int button, boolean pressed, int mods){
        super.onMouseButton(x, y, button, pressed, mods);
        if(button==1&&pressed)highlighted = null;
    }
    Stack<Container> elements = new Stack<>();
    private int getThingCount(Container element){
        if(elements.contains(element))return 0;
        elements.push(element);
        int count = 0;
        for(AbstractPlacementRule<BlockType, Template> rule : element.rules){
            boolean ruleRule = rule.block==null&&rule.blockType==null;
            if(ruleRule){
                if(rule.ruleType==AbstractPlacementRule.RuleType.AND)count+=getThingCount((Container)rule);
                else count+=getMinThingCount((Container)rule);
            }
            else{
                int c = rule.min*((rule.isSpecificBlock?getThingCount((Container)rule.block):0)+1);
                if(rule.ruleType==AbstractPlacementRule.RuleType.AXIAL)c*=2;
                if(rule.ruleType==AbstractPlacementRule.RuleType.EDGE)c=2;
                if(rule.ruleType==AbstractPlacementRule.RuleType.VERTEX)c=3;
                count+=c;
            }
        }
        elements.pop();
        return count;
    }
    private int getMinThingCount(Container element){
        if(elements.contains(element))return 0;
        elements.push(element);
        int count = Integer.MAX_VALUE;
        for(AbstractPlacementRule<BlockType, Template> rule : element.rules){
            boolean ruleRule = rule.block==null&&rule.blockType==null;
            int coun = 0;
            if(ruleRule){
                if(rule.ruleType==AbstractPlacementRule.RuleType.AND)coun+=getThingCount((Container)rule);
                else coun+=getMinThingCount((Container)rule);
            }
            else{
                int c = rule.min*((rule.isSpecificBlock?getThingCount((Container)rule.block):0)+1);
                if(rule.ruleType==AbstractPlacementRule.RuleType.AXIAL)c*=2;
                if(rule.ruleType==AbstractPlacementRule.RuleType.EDGE)c=2;
                if(rule.ruleType==AbstractPlacementRule.RuleType.VERTEX)c=3;
                coun+=c;
            }
            if(coun<count)count = coun;
        }
        if(count==Integer.MAX_VALUE)count = 0;
        elements.pop();
        return count;
    }
    private ArrayList<Object> getThings(Container element){
        ArrayList<Object> things = new ArrayList<>();
        if(elements.contains(element))return things;
        elements.push(element);
        if(element instanceof IBlockTemplate)if(!things.contains(element))things.add(element);
        for(AbstractPlacementRule<BlockType, Template> rule : element.rules){
            boolean ruleRule = rule.block==null&&rule.blockType==null;
            if(!ruleRule){
                things.add(rule.isSpecificBlock?rule.block:rule.blockType);
                if(rule.block!=null)for(Object thing : getThings((Container)rule.block))if(!things.contains(thing))things.add(thing);
            }
            for(Object thing : getThings((Container)rule))if(!things.contains(thing))things.add(thing);
        }
        elements.pop();
        return things;
    }
    private ArrayList<Object> getThingsOnce(Container element){
        ArrayList<Object> things = new ArrayList<>();
        for(AbstractPlacementRule<BlockType, Template> rule : element.rules){
            boolean ruleRule = rule.block==null&&rule.blockType==null;
            if(!ruleRule)things.add(rule.isSpecificBlock?rule.block:rule.blockType);
            if(ruleRule)for(Object thing : getThingsOnce((Container)rule))if(!things.contains(thing))things.add(thing);
        }
        return things;
    }
    private TreeElement getTreeElement(Object o){
        for(ArrayList<TreeElement> tier : tiers){
            for(TreeElement element : tier){
                if(o==(element.isSpecificBlock?element.template:element.blockType))return element;
            }
        }
        return null;
    }
    private MenuComponentTreeElement getComponent(TreeElement element){
        for(MenuComponent component : list.components){
            for(MenuComponent comp2 : component.components){
                if(comp2 instanceof MenuComponentTreeElement){
                    MenuComponentTreeElement te = (MenuComponentTreeElement)comp2;
                    if(te.element==element)return te;
                }
            }
        }
        return null;
    }
    public void highlight(TreeElement element){
        highlighted = new ArrayList<>();
        ArrayList<Object> things = new ArrayList<>();
        if(element.template!=null) things.addAll(getThings((Container)element.template));
        if(element.blockType!=null)highlighted.add(element.blockType);
        for(ArrayList<TreeElement> tier : tiers){
            for(TreeElement elem : tier){
                if(things.contains(elem.isSpecificBlock?elem.template:elem.blockType))highlighted.add(elem.isSpecificBlock?elem.template:elem.blockType);
                else{
                    if(elem.template!=null){
                        if(getThings((Container)elem.template).contains(element.isSpecificBlock?element.template:element.blockType))highlighted.add(elem.template);
                    }
                }
            }
        }
    }
    public double getFullTreeLength(){
        double length = 0;
        for(ArrayList<TreeElement> tier : tiers){
            for(TreeElement element : tier){
                length+=getTreeElementLength(element);
            }
        }
        return length;
    }
    public double getTreeElementLength(TreeElement element){
        if(element.template==null)return 0;
        double len = 0;
        for(Object o : getThingsOnce((Container)element.template)){
            len+=getLinkLength(element, getTreeElement(o));
        }
        return len;
    }
    public double getLinkLength(TreeElement elem1, TreeElement elem2){
        if(elem2==null)return 0;//just in case
        MenuComponentTreeElement c1 = getComponent(elem1);
        MenuComponentTreeElement c2 = getComponent(elem2);
        double c1x = c1.x+((MenuComponent)c1.parent).x;
        double c1y = c1.y+((MenuComponent)c1.parent).y;
        double c2x = c2.x+((MenuComponent)c2.parent).x;
        double c2y = c2.y+((MenuComponent)c2.parent).y;
        return Math.sqrt(Math.pow(c1x-c2x, 2)+Math.pow(c1y-c2y, 2));
    }
    @Override
    public synchronized void onMouseMove(double x, double y){
        super.onMouseMove(x, y);
    }
    @Override
    public synchronized boolean onMouseScrolled(double x, double y, double dx, double dy){
        return super.onMouseScrolled(x, y, dx, dy);
    }
    @Override
    public synchronized void keyEvent(int key, int scancode, boolean isPress, boolean isRepeat, int modifiers){
        super.keyEvent(key, scancode, isPress, isRepeat, modifiers);
    }
    @Override
    public synchronized void onCharTyped(char c){
        super.onCharTyped(c);
    }
    @Override
    public synchronized boolean onFilesDropped(double x, double y, String[] files){
        return super.onFilesDropped(x, y, files);
    }
    @Override
    public synchronized boolean onReturnPressed(MenuComponent component){
        return super.onReturnPressed(component);
    }
    @Override
    public synchronized boolean onTabPressed(MenuComponent component){
        return super.onTabPressed(component);
    }
    @Override
    public synchronized void onWindowFocused(boolean focused){
        super.onWindowFocused(focused);
    }
}