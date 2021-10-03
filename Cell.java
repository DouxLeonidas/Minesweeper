import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.ImageIcon;

public class Cell extends JButton {
  
  public static final int SIZE = 30;
  private ImageIcon hiddenIcon;
  public int x, y;

  public Cell(String val, int i, int j) {
    x = i;
    y = j;
    this.setName(val);
    this.setMargin(new Insets(0, 0, 0, 0));
    this.setBounds(SIZE * (j + 1),
                   SIZE * (i + 1),
                   SIZE, SIZE);
  }

  public void setHiddenIcon(ImageIcon hiddenIcon) {
    this.hiddenIcon = hiddenIcon;
  }

  public ImageIcon getHiddenIcon() {
    return hiddenIcon;
  }

}