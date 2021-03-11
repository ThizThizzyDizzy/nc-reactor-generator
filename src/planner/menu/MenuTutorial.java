package planner.menu;
import planner.Core;
import planner.menu.component.MenuComponentDropdownList;
import planner.menu.component.MenuComponentLabel;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.tutorial.MenuComponentTutorial;
import planner.menu.component.tutorial.MenuComponentTutorialCategory;
import planner.menu.component.tutorial.MenuComponentTutorialDisplay;
import planner.tutorial.Tutorial;
import planner.tutorial.TutorialCategory;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuTutorial extends Menu{
    public MenuComponentMinimaList tutorialList = add(new MenuComponentMinimaList(0, 0, 0, 0, 32, true));
    public MenuComponentMinimalistButton done = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Done", true, true));
    public MenuComponentLabel title = add(new MenuComponentLabel(0, 0, 0, 0, "Tutorials", true));
    public MenuComponentLabel categoryLabel = add(new MenuComponentLabel(0, 0, 0, 0, "Category", true));
    public MenuComponentDropdownList categoryBox = add(new MenuComponentDropdownList(0, 0, 0, 48));
    public MenuComponentLabel tutorialLabel = add(new MenuComponentLabel(0, 0, 0, 0, "Tutorial", true));
    public MenuComponentDropdownList tutorialBox = add(new MenuComponentDropdownList(0, 0, 0, 48));
    public TutorialCategory selectedCategory = Tutorial.categories.get(0);
    public Tutorial selectedTutorial = selectedCategory.tutorials.get(0);
    public MenuComponentTutorialDisplay tutorialDisplay = tutorialList.add(new MenuComponentTutorialDisplay(selectedTutorial));
    public MenuTutorial(GUI gui, simplelibrary.opengl.gui.Menu parent){
        super(gui, parent);
        for(TutorialCategory category : Tutorial.categories){
            categoryBox.add(new MenuComponentTutorialCategory(category));
        }
        done.addActionListener((e) -> {
            gui.open(new MenuTransition(gui, this, parent, MenuTransition.SplitTransition.slideOut(done.width/gui.helper.displayWidth()), 4));
        });
    }
    @Override
    public void onGUIOpened(){
        onCategoryChanged();
    }
    public void onCategoryChanged(){
        tutorialBox.clear();
        for(Tutorial tutorial : selectedCategory.tutorials){
            tutorialBox.add(new MenuComponentTutorial(tutorial));
        }
    }
    public void onTutorialChanged(){
        tutorialDisplay.tutorial = selectedTutorial;
    }
    @Override
    public void renderBackground(){
        categoryLabel.x = tutorialLabel.x = categoryBox.x = tutorialBox.x = done.x = 0;
        tutorialList.x = categoryLabel.width = tutorialLabel.width = categoryBox.width = tutorialBox.width = done.width = title.x = 300;
        title.width = gui.helper.displayWidth()-title.x;
        categoryLabel.height = tutorialLabel.height = 48;
        done.height = title.height = 64;
        tutorialList.y = categoryLabel.y = done.y+done.height;
        categoryBox.y = categoryLabel.y+categoryLabel.height;
        tutorialLabel.y = categoryBox.y+categoryBox.height;
        tutorialBox.y = tutorialLabel.y+tutorialLabel.height;
        tutorialList.width = gui.helper.displayWidth()-tutorialList.x;
        tutorialList.height = gui.helper.displayHeight()-tutorialList.y;
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
//        Core.applyAverageColor(Core.theme.getDarkButtonColor(),Core.theme.getBackgroundColor());
//        drawRect(0, 0, done.width, gui.helper.displayHeight(), 0);
        super.renderBackground();
    }
}