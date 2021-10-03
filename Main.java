class Main {
  public static void main(String[] args) {  
    try {
      Minesweeper ms = new Minesweeper(10, 20, 10);
    } catch (Minesweeper.InvalidNumberOfMines ex) {
      System.err.println("Invalid number of mines");
    }
  }
}