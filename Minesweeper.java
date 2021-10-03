import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class Minesweeper extends JFrame {
  public static final int WIDTH = 400;
  public static final int HEIGHT = 500;
  public static final int[][] DIR = {{0,1},{0,-1},{1,0},{-1,0},{1,1},{1,-1},{-1,1},{-1,-1}};
  public static final String[] DIGIT = {"0️⃣ ","1️⃣ ","2️⃣ ","3️⃣ ","4️⃣ ","5️⃣ ","6️⃣ ","7️⃣ ","8️⃣ ","🔯"};
  public static final String UNKNOWN = "⏹ ";
  private Image[] numbers;
  private int emptyCells;
  private boolean showSolution;

  public Minesweeper(int rows, int cols, int mines) 
      throws InvalidNumberOfMines {
    loadImages();
    Matrix<Cell> board = new Matrix<>();
    fillBoard(board, rows, cols, mines);
    printBoard(board, rows, cols);
    emptyCells = rows * cols - mines;
    showSolution = false;
    this.setTitle("Minesweeper");
    this.setSize(WIDTH,HEIGHT);
    this.setLayout(null);
    this.setVisible(true);
  }

  public void loadImages() {
    try {
      numbers = new Image[10];
      numbers[9] = ImageIO.read(getClass().getResource("images/calendario.png"));
      numbers[9] = numbers[9].getScaledInstance(25, 25, Image.SCALE_SMOOTH);
      
      BufferedImage nums = ImageIO.read(getClass().getResource("images/mayan.png"));
      numbers[0] = nums.getSubimage(0, 0, 86, 108);
      numbers[1] = nums.getSubimage(117, 0, 86, 108);
      numbers[2] = nums.getSubimage(232, 0, 86, 108);
      numbers[3] = nums.getSubimage(347, 0, 86, 108);
      numbers[4] = nums.getSubimage(465, 0, 86, 108);
      numbers[5] = nums.getSubimage(0, 108, 86, 108);
      numbers[6] = nums.getSubimage(117, 108, 86, 108);
      numbers[7] = nums.getSubimage(232, 108, 86, 108);
      numbers[8] = nums.getSubimage(347, 108, 86, 108);
      for (int i = 0; i < 9; ++i) {
        numbers[i] = numbers[i].getScaledInstance(25, 25, Image.SCALE_SMOOTH);
      }
    } catch (Exception ex) {
      System.err.println(ex);
    }
  }

  public void fillBoard(Matrix<Cell> board,
      int rows, int cols, int mines)
      throws InvalidNumberOfMines {
    if (mines >= rows * cols)
      throw new InvalidNumberOfMines();
    
    String mayority;
    String minority;
    int inc;
    if (2 * mines < rows * cols) {
      mayority = "0";
      minority = "9";
      inc = 1;
    } else {
      mayority = "9";
      minority = "0";
      inc = -1;
      mines = (rows * cols) - mines; 
    }

    //Cell button;
    for (int i = 0; i < rows; ++i) {
      for (int j = 0; j < cols; ++j) {
        final Cell button = new Cell(mayority,i,j);
        if (mayority.equals("9")) {
          button.setHiddenIcon(new ImageIcon(numbers[9]));
        } else {
          button.setHiddenIcon(new ImageIcon(numbers[0]));
        }
        button.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent ae) {
            if (button.getName().equals("9")) {
              gameOver(board, rows, cols);
            }
            floodBoard(board, rows, cols, button);
            printBoard(board, rows, cols);
          }
        });
        //button.addListener();
        board.set(i, j, button);
        this.add(button);
      }
    }

    int value;
    int randRow;
    int randCol;
    int minesAround;
    int droppedMines = 0;
    Cell nextToMe;
    while (droppedMines < mines) {
      randRow = (int)(Math.random() * rows);
      randCol = (int)(Math.random() * cols);
      Cell button = board.get(randRow, randCol);
      if (button.getName().equals(mayority)) {
        ++droppedMines;
        minesAround = 0;
        for (int[] d : DIR) {
          if (randRow + d[0] < 0) continue;
          if (randCol + d[1] < 0) continue;
          if (randRow + d[0] >= rows) continue;
          if (randCol + d[1] >= cols) continue;
          nextToMe = board.get(randRow + d[0],
                               randCol + d[1]);
          value = Integer.parseInt(nextToMe.getName());
          if (value != 9) {
            value += inc;
            nextToMe.setName(Integer.toString(value));
            nextToMe.setHiddenIcon(new ImageIcon(numbers[value]));
          } else {
            minesAround++;
          }
        }
        if (minority.equals("0")) {
          button.setName(Integer.toString(minesAround));
          button.setHiddenIcon(new ImageIcon(numbers[minesAround]));
        } else {
          button.setName(minority);
          button.setHiddenIcon(new ImageIcon(numbers[9]));
        }
      }
    }
  }

  public void printBoard(Matrix<Cell> board, int rows, int cols) {
    Cell cell;
    System.out.print("\033[H\033[2J");  
    System.out.flush();  
    for (int i = 0; i < rows; ++i) {
      for (int j = 0; j < cols; ++j) {
        cell = board.get(i,j);
        if (cell == null) continue;
        if (!showSolution && cell.isEnabled()) {
          System.out.print(UNKNOWN);
        } else {
          int d = Integer.parseInt(cell.getName());
          System.out.print(DIGIT[d]);
        }
      }
      System.out.println();
    }
  }

  public void floodBoard(Matrix<Cell> board, int rows, int cols, Cell s) {
    if (s.getName().equals("9") || !s.isEnabled()) {
      return;
    }
    s.setEnabled(false);
    s.setIcon(s.getHiddenIcon());
    s.setDisabledIcon(s.getHiddenIcon());
    --emptyCells;
    if (emptyCells == 0) {
      youWin(board, rows, cols);
    }
    if (!s.getName().equals("0")) {
      return;
    }
    for (int[] d : DIR) {
      if (s.y + d[1] < 0 || s.y + d[1] >= cols) continue;
      if (s.x + d[0] < 0 || s.x + d[0] >= rows) continue;
      floodBoard(board, rows, cols, 
          board.get(s.x + d[0], s.y + d[1]));
    }
  }

  public void gameOver(Matrix<Cell> board, int rows, int cols) {
    for (int i = 0; i < rows; ++i) {
      for (int j = 0; j < cols; ++j) {
        board.get(i,j).setEnabled(false);
        if (board.get(i,j).getName().equals("9")) {
          board.get(i,j).setIcon(board.get(i,j).getHiddenIcon());
          board.get(i,j).setDisabledIcon(board.get(i,j).getHiddenIcon());
          board.get(i,j).setBackground(Color.RED);
          board.get(i,j).setOpaque(true);
        }
      }
    }
    System.out.println("Game Over");
  }

  public void youWin(Matrix<Cell> board, int rows, int cols) {
    for (int i = 0; i < rows; ++i) {
      for (int j = 0; j < cols; ++j) {
        board.get(i,j).setEnabled(false);
        if (board.get(i,j).getName().equals("9")) {
          board.get(i,j).setIcon(board.get(i,j).getHiddenIcon());
          board.get(i,j).setDisabledIcon(board.get(i,j).getHiddenIcon());
          board.get(i,j).setBackground(Color.GREEN);
          board.get(i,j).setOpaque(true);
        }
      }
    }
    System.out.println("Congratulations");
  }

  public class InvalidNumberOfMines extends Exception{
  }
}