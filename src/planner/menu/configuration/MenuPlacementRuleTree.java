package planner.menu.configuration;
import java.util.ArrayList;
import java.util.HashMap;
import multiblock.configuration.AbstractPlacementRule;
import multiblock.configuration.IBlockTemplate;
import multiblock.configuration.IBlockType;
import multiblock.configuration.RuleContainer;
import planner.Core;
import planner.menu.component.MenuComponentMinimaList;
import simplelibrary.Stack;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuPlacementRuleTree<Container extends RuleContainer<BlockType, Template>, BlockType extends IBlockType, Template extends IBlockTemplate> extends ConfigurationMenu{
    private ArrayList<ArrayList<TreeElement>> tiers = new ArrayList<>();
    private final MenuComponentMinimaList list;
    private ArrayList<Object> highlighted = null;
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
    }
    @Override
    public void render(int millisSinceLastTick){
        super.render(millisSinceLastTick);
        list.width = gui.helper.displayWidth()-sidebar.width;
        list.height = gui.helper.displayHeight();
        int num = 1;
        for(ArrayList<TreeElement> tier : tiers){
            num = Math.max(num, tier.size()+1);
        }
        double size = (list.width-list.vertScrollbarWidth)/num;
        for(MenuComponent component : list.components){
            component.width = list.width;
            component.height = size;
            for(int i = 0; i<component.components.size(); i++){
                MenuComponent comp = component.components.get(i);
                comp.width = comp.height = size;
                comp.x = i*size;
            }
        }
    }
    private void refresh(){
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
                parentComponent.add(new MenuComponent(0, 0, 0, 0){
                    {
                        setTooltip(element.getTooltip());
                    }
                    @Override
                    public void render(){
                        if(highlighted==null||highlighted.contains(element.isSpecificBlock?element.template:element.blockType))Core.applyWhite();
                        else Core.applyWhite(.5f);
                        element.render(x, y, width, height);
                    }
                    @Override
                    public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
                        super.onMouseButton(x, y, button, pressed, mods);
                        if(button==0&&pressed&&element.template!=null){
                            highlighted = new ArrayList<>(getThings((Container)element.template));
                        }
                    }
                });
            }
            list.add(parentComponent);
        }
    }
    @Override
    public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
        super.onMouseButton(x, y, button, pressed, mods);
        if(button==-1&&pressed)highlighted = null;
    }
    Stack<Container> elements = new Stack<>();
    private int getThingCount(Container element){
        if(elements.contains(element))return 0;
        elements.push(element);
        int count = 0;
        for(AbstractPlacementRule<BlockType, Template> rule : element.rules){
            boolean ruleRule = rule.block==null&&rule.blockType==null;
            if(ruleRule)count+=getThingCount((Container)rule);
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
    private class TreeElement{
        private final boolean isSpecificBlock;
        private BlockType blockType;
        private Template template;
        private int tier;
        public TreeElement(Template template, int tier){
            this.template = template;
            this.tier = tier;
            isSpecificBlock = true;
        }
        public TreeElement(BlockType blockType, int tier){
            this.blockType = blockType;
            this.tier = tier;
            isSpecificBlock = false;
        }
        private void render(double x, double y, double width, double height){
            if(isSpecificBlock)drawRect(x, y, x+width, y+height, Core.getTexture(template.getDisplayTexture()));
            else{
                String text = blockType.getDisplayName();
                double textLength = FontManager.getLengthForStringWithHeight(text, height);
                double scale = Math.min(1, (width)/textLength);
                double textHeight = (int)((height)*scale)-4;
                Core.applyColor(Core.theme.getComponentTextColor(0));
                drawCenteredText(x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, text);
            }
        }
        private String getTooltip(){
            if(isSpecificBlock)return template.getDisplayName();
            else return null;
        }
    }
    private HashMap<Object, Integer> getThingsWithTiers(Container element){
        HashMap<Object, Integer> things = new HashMap<>();
        for(AbstractPlacementRule<BlockType, Template> rule : element.rules){
            boolean ruleRule = rule.block==null&&rule.blockType==null;
            if(!ruleRule)things.put(rule.isSpecificBlock?rule.block:rule.blockType, 0);
            HashMap<Object, Integer> moreThings = getThingsWithTiers((Container)rule);
            for(Object ob : moreThings.keySet()){
                things.put(ob, Math.min(things.getOrDefault(ob, Integer.MAX_VALUE), moreThings.get(ob)-(ruleRule?0:1)));
            }
        }
        int min = 0;
        for(int i : things.values())if(i<min)min = i;
        HashMap<Object, Integer> adjusted = new HashMap<>();
        for(Object o : things.keySet())adjusted.put(o, things.get(o)-min);
        return adjusted;
    }
    private ArrayList<Object> getThings(Container element){
        ArrayList<Object> things = new ArrayList<>();
        if(element instanceof IBlockTemplate)things.add(element);
        for(AbstractPlacementRule<BlockType, Template> rule : element.rules){
            boolean ruleRule = rule.block==null&&rule.blockType==null;
            if(!ruleRule)things.add(rule.isSpecificBlock?rule.block:rule.blockType);
            for(Object thing : getThings((Container)rule))if(!things.contains(thing))things.add(thing);
        }
        return things;
    }
    private ArrayList<Object> getThingsOnce(Container element){
        ArrayList<Object> things = new ArrayList<>();
        if(element instanceof IBlockTemplate)things.add(element);
        for(AbstractPlacementRule<BlockType, Template> rule : element.rules){
            boolean ruleRule = rule.block==null&&rule.blockType==null;
            if(!ruleRule)things.add(rule.isSpecificBlock?rule.block:rule.blockType);
            if(ruleRule)for(Object thing : getThingsOnce((Container)rule))if(!things.contains(thing))things.add(thing);
        }
        return things;
    }
}