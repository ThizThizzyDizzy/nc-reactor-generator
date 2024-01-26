package net.ncplanner.plannerator.planner.gui.menu.component.config2;
import java.util.Objects;
import java.util.function.Consumer;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.SplitLayout;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuInputDialog;
public class C2ConfigEntryComponent extends SplitLayout{
    public C2ConfigEntryComponent(String key, Object value, Consumer<Boolean> onExpand, Consumer<Object> onSet){
        super(SplitLayout.X_AXIS, 0.3f, 256, 256);
        fitContent();
        add(new Label(key));
        String type = value==null?"null":value.getClass().getSimpleName();
        if(value instanceof String)type = "str";
        if(value instanceof Integer)type = "int";
        if(value instanceof Float)type = "float";
        if(value instanceof Boolean)type = "bool";
        if(value instanceof Long)type = "long";
        if(value instanceof Double)type = "double";
        if(value instanceof Byte)type = "byte";
        if(value instanceof Short)type = "short";
        if(type.contains("Config")){
            String typ = " "+type;
            add(new Button(typ, true){
                private boolean expanded = true;
                {
                    text = (expanded?"V":">")+typ;
                    addAction(() -> {
                        expanded = !expanded;
                        onExpand.accept(expanded);
                        text = (expanded?"V":">")+typ;
                    });
                }
            });
        }else{
            Runnable editAction = null;
            final String typ = type;
            switch(typ){
                case "str":
                    editAction = ()->{
                        new MenuInputDialog(Core.gui, Core.gui.menu, "", "Set "+typ+" value: "+key).addButton("Set", (str)->onSet.accept(str), true).addButton("Cancel", true).open();
                    };
                    break;
                case "int":
                    editAction = ()->{
                        new MenuInputDialog(Core.gui, Core.gui.menu, "", "Set "+typ+" value: "+key).addButton("Set", (str)->onSet.accept(Integer.valueOf(str)), true).addButton("Cancel", true).open();
                    };
                    break;
                case "float":
                    editAction = ()->{
                        new MenuInputDialog(Core.gui, Core.gui.menu, "", "Set "+typ+" value: "+key).addButton("Set", (str)->onSet.accept(Float.valueOf(str)), true).addButton("Cancel", true).open();
                    };
                    break;
                case "bool":
                    editAction = ()->{
                        new MenuInputDialog(Core.gui, Core.gui.menu, "", "Set "+typ+" value: "+key).addButton("True", ()->onSet.accept(true), true).addButton("False", ()->onSet.accept(false), true).addButton("Cancel", true).open();
                    };
                    break;
                case "long":
                    editAction = ()->{
                        new MenuInputDialog(Core.gui, Core.gui.menu, "", "Set "+typ+" value: "+key).addButton("Set", (str)->onSet.accept(Long.valueOf(str)), true).addButton("Cancel", true).open();
                    };
                    break;
                case "double":
                    editAction = ()->{
                        new MenuInputDialog(Core.gui, Core.gui.menu, "", "Set "+typ+" value: "+key).addButton("Set", (str)->onSet.accept(Double.valueOf(str)), true).addButton("Cancel", true).open();
                    };
                    break;
                case "byte":
                    editAction = ()->{
                        new MenuInputDialog(Core.gui, Core.gui.menu, "", "Set "+typ+" value: "+key).addButton("Set", (str)->onSet.accept(Byte.valueOf(str)), true).addButton("Cancel", true).open();
                    };
                    break;
                case "short":
                    editAction = ()->{
                        new MenuInputDialog(Core.gui, Core.gui.menu, "", "Set "+typ+" value: "+key).addButton("Set", (str)->onSet.accept(Short.valueOf(str)), true).addButton("Cancel", true).open();
                    };
                    break;
            }
            if(editAction!=null)add(new Button(typ+": "+Objects.toString(value), true).addAction(editAction));
            else add(new Label(typ+": "+Objects.toString(value)));
        }
    }
}