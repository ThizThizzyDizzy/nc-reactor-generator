package overhaul;
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
        if(reactor==null)return;
        g.drawImage(reactor.getImage(), 0, 0, null);
    }
    public BufferedImage getImage(){
        int blockSize = 16;
        BufferedImage image = new BufferedImage(blockSize*reactor.x, blockSize*(((reactor.z+1)*reactor.y)-1), BufferedImage.TYPE_INT_ARGB);
        draw(image.createGraphics(),image.getWidth(),image.getHeight());
        return image;
    }
}