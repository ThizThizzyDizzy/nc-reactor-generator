package pre_overhaul;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
public class ReactorPanel extends JPanel{
    private final Reactor reactor;
    public ReactorPanel(Reactor reactor){
        this.reactor = reactor;
    }
    @Override
    protected void paintComponent(Graphics g) {
        draw(g, getWidth(), getHeight());
    }
    protected void draw(Graphics g, int w, int h) {
        g.setColor(new Color(240,240,240));
        g.fillRect(0, 0, w, h);
        int yOff = 0;
        if(reactor==null)return;
        int blockSize = Math.min(w/reactor.x,h/(((reactor.z+1)*reactor.y)-1));
        for(int y = reactor.y-1; y>=0; y--){
            for(int z = 0; z<reactor.z; z++){
                for(int x = 0; x<reactor.x; x++){
                    g.drawImage(reactor.parts[x][y][z].getImage(), x*blockSize, yOff, blockSize, blockSize, null);
//                    int id = reactor.getClusterID(x,y,z);
//                    if(id>-1){
//                        g.setColor(new Color(id*80, 0, 0, 127));
//                        g.fillRect(x*blockSize, yOff, blockSize, blockSize);
//                    }
                }
                yOff+=blockSize;
            }
            yOff+=blockSize;
        }
    }
    public BufferedImage getImage(){
        int blockSize = 16;
        BufferedImage image = new BufferedImage(blockSize*reactor.x, blockSize*(((reactor.z+1)*reactor.y)-1), BufferedImage.TYPE_INT_ARGB);
        draw(image.createGraphics(),image.getWidth(),image.getHeight());
        return image;
    }
}