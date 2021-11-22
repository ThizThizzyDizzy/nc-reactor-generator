package net.ncplanner.plannerator.planner.gui.menu.configuration.tree;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.planner.gui.Component;
import static org.lwjgl.glfw.GLFW.*;
public class MenuComponentTreeElement extends Component{
    private final MenuPlacementRuleTree ruleTree;
    public final TreeElement element;
    public MenuComponentTreeElement(MenuPlacementRuleTree ruleTree, TreeElement element){
        super(0, 0, 0, 0);
        this.ruleTree = ruleTree;
        this.element = element;
        setTooltip(element.getTooltip());
    }
    @Override
    public void draw(double deltaTime){
        Renderer renderer = new Renderer();
        float alpha;
        if(ruleTree.highlighted==null||ruleTree.highlighted.contains(element.isSpecificBlock?element.template:element.blockType))alpha = 1;
        else alpha = .25f;
        renderer.setWhite(alpha);
        element.render(renderer, x, y, width, height, alpha);
    }
    @Override
    public void onMouseButton(double x, double y, int button, int action, int mods){
        super.onMouseButton(x, y, button, action, mods);
        if(button==GLFW_MOUSE_BUTTON_LEFT&&action==GLFW_PRESS){
            ruleTree.highlight(element);
        }
    }
}