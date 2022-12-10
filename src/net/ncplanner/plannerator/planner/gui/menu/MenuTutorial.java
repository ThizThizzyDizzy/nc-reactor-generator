package net.ncplanner.plannerator.planner.gui.menu;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.DropdownList;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.tutorial.MenuComponentTutorial;
import net.ncplanner.plannerator.planner.gui.menu.component.tutorial.MenuComponentTutorialCategory;
import net.ncplanner.plannerator.planner.gui.menu.component.tutorial.MenuComponentTutorialDisplay;
import net.ncplanner.plannerator.planner.tutorial.Tutorial;
import net.ncplanner.plannerator.planner.tutorial.TutorialCategory;
public class MenuTutorial extends Menu{
    public SingleColumnList tutorialList = add(new SingleColumnList(0, 0, 0, 0, 32));
    public Button done = add(new Button("Done", true));
    public Label title = add(new Label(0, 0, 0, 0, "Tutorials", true));
    public Label categoryLabel = add(new Label(0, 0, 0, 0, "Category", true));
    public DropdownList categoryBox = add(new DropdownList(0, 0, 0, 48));
    public Label tutorialLabel = add(new Label(0, 0, 0, 0, "Tutorial", true));
    public DropdownList tutorialBox = add(new DropdownList(0, 0, 0, 48));
    public TutorialCategory selectedCategory = Tutorial.categories.get(0);
    public Tutorial selectedTutorial = selectedCategory.tutorials.get(0);
    public MenuComponentTutorialDisplay tutorialDisplay = tutorialList.add(new MenuComponentTutorialDisplay(selectedTutorial));
    public MenuTutorial(GUI gui, Menu parent){
        super(gui, parent);
        for(TutorialCategory category : Tutorial.categories){
            categoryBox.add(new MenuComponentTutorialCategory(category));
        }
        categoryBox.setSelectedIndex(0);
        onCategoryChanged();
        done.addAction(() -> {
            gui.open(new MenuTransition(gui, this, parent, MenuTransition.SplitTransitionX.slideOut(done.width/gui.getWidth()), 4));
        });
    }
    @Override
    public void onOpened(){
        onCategoryChanged();
    }
    public void onCategoryChanged(){
        tutorialBox.clear();
        for(Tutorial tutorial : selectedCategory.tutorials){
            tutorialBox.add(new MenuComponentTutorial(tutorial));
        }
        tutorialBox.setSelectedIndex(0);
    }
    public void onTutorialChanged(){
        tutorialDisplay.tutorial = selectedTutorial;
        tutorialList.scrollY = 0;
    }
    @Override
    public void drawBackground(double deltaTime){
        super.drawBackground(deltaTime);
        categoryLabel.x = tutorialLabel.x = categoryBox.x = tutorialBox.x = done.x = 0;
        tutorialList.x = categoryLabel.width = tutorialLabel.width = categoryBox.width = tutorialBox.width = done.width = title.x = 300;
        title.width = gui.getWidth()-title.x;
        categoryLabel.height = tutorialLabel.height = 48;
        done.height = title.height = 64;
        tutorialList.y = categoryLabel.y = done.y+done.height;
        categoryBox.y = categoryLabel.y+categoryLabel.height;
        tutorialLabel.y = categoryBox.y+categoryBox.height;
        tutorialBox.y = tutorialLabel.y+tutorialLabel.height;
        tutorialList.width = gui.getWidth()-tutorialList.x;
        tutorialList.height = gui.getHeight()-tutorialList.y;
        tutorialDisplay.width = tutorialList.width-tutorialList.vertScrollbarWidth;
        selectedTutorial.preRender();//this is just to initialize components...
        tutorialDisplay.height = selectedTutorial.getHeight()*tutorialDisplay.width;
        if(selectedCategory!=Tutorial.categories.get(categoryBox.getSelectedIndex())){
            selectedCategory = Tutorial.categories.get(categoryBox.getSelectedIndex());
            onCategoryChanged();
        }
        if(selectedTutorial!=selectedCategory.tutorials.get(tutorialBox.getSelectedIndex())){
            selectedTutorial = selectedCategory.tutorials.get(tutorialBox.getSelectedIndex());
            onTutorialChanged();
        }
//        renderer.setAverageColor(Core.theme.getDarkButtonColor(),Core.theme.getBackgroundColor());
//        drawRect(0, 0, done.width, gui.getHeight(), 0);
        super.drawBackground(deltaTime);
    }
}