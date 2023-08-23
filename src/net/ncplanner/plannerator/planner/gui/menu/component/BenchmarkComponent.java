package net.ncplanner.plannerator.planner.gui.menu.component;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.Component;
public class BenchmarkComponent extends Component{
    public ArrayList<String> names = new ArrayList<>();
    public ArrayList<ArrayList<Long>> data = new ArrayList<>();
    private ArrayList<Long> current;
    long max = 0;
    long maxLen = 0;
    long currentCount = 0;
    public void addBenchmark(String name){
        names.add(name);
        data.add(current = new ArrayList<>());
        currentCount = 0;
    }
    public void log(long count){
        long c = count-currentCount;
        current.add(c);
        currentCount = count;
        max = Math.max(max,c);
        maxLen = Math.max(maxLen, current.size());
    }
    @Override
    public void draw(double deltaTime){
        Renderer renderer = new Renderer();
        for(int i = 0; i<data.size(); i++){
            ArrayList<Long> nums = data.get(i);
            if(nums.isEmpty())continue;
            renderer.setColor(Core.theme.getRecoveryModeColor(i));
            renderer.drawText(x, y+i*40, names.get(i), 40);
            renderer.bindTexture(null);
            float w = width/maxLen;
            double average = nums.get(0);
            for(int X = 1; X<nums.size(); X++){
                average+=nums.get(X);
                float X0 = nums.get(X-1)/(float)max;
                float X1 = nums.get(X)/(float)max;
                float x0 = x+(X-1)*w;
                float x1 = x+X*w;
                float y0 = y+height*(1-X0);
                float y1 = y+height*(1-X1);
                float h = 3;
                renderer.fillQuad(x0, y0-h/2, x0, y0+h/2, x1, y1-h/2, x1, y1+h/2);
                renderer.fillQuad(x0-h/2, y0, x1-h/2, y1, x0+h/2, y0, x1+h/2, y1);
            }
            average/=nums.size();
            average/=max;
            float Y = (1-(float)average)*height;
            renderer.fillRect(x, y+Y, x+width, y+Y+1);
        }
    }
}