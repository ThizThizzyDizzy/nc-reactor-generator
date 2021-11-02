package net.ncplanner.plannerator.planner.menu.configuration.tree;
import net.ncplanner.plannerator.Renderer;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentTreeElement extends MenuComponent{
    private final MenuPlacementRuleTree ruleTree;
    public final TreeElement element;
    public MenuComponentTreeElement(MenuPlacementRuleTree ruleTree, TreeElement element){
        super(0, 0, 0, 0);
        this.ruleTree = ruleTree;
        this.element = element;
        setTooltip(element.getTooltip());
    }
    @Override
    public void render(){
        Renderer renderer = new Renderer();
        float alpha;
        if(ruleTree.highlighted==null||ruleTree.highlighted.contains(element.isSpecificBlock?element.template:element.blockType))alpha = 1;
        else alpha = .25f;
        renderer.setWhite(alpha);
        element.render(renderer, x, y, width, height, alpha);
    }
    @Override
    public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
        super.onMouseButton(x, y, button, pressed, mods);
        if(button==0&&pressed){
            ruleTree.highlight(element);
        }
    }
}