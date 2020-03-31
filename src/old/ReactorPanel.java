package old;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;
public class ReactorPanel extends JPanel{
    private final Reactor reactor;
    public ReactorPanel(Reactor reactor){
        this.reactor = reactor;
    }
    @Override
    protected void paintComponent(Graphics g) {
        if(reactor.color!=null){
            g.setColor(reactor.color);
        }else g.setColor(new Color(240,240,240));
        g.fillRect(0, 0, getWidth(), getHeight());
        int yOff = 0;
        int blockSize = getWidth()/reactor.parts.length;
        for(int y = reactor.parts[0].length-1; y>=0; y--){
            for(int z = 0; z<reactor.parts[0][0].length; z++){
                for(int x = 0; x<reactor.parts.length; x++){
                    if(reactor.efficiency[x][y][z]!=0){
                        g.drawImage(reactor.parts[x][y][z].getImage(), x*blockSize, yOff, blockSize, blockSize, null);
                        //draw reactor.parts[x][y][z].c;
                    }
                }
                yOff+=blockSize;
            }
            yOff+=blockSize;
        }
    }
}