import java.util.HashMap;

public class Matrix<T> {
  private class Row {
    HashMap<Integer,T> cells = new HashMap<>();
  }
  
  HashMap<Integer, Row> rows;
  public Matrix() {
    this.rows = new HashMap<>();
  }

  public T get(int row, int col) {
    try {
      return rows.get(row).cells.get(col);
    } catch(NullPointerException keyNotFound) {
      return null;
    }
  }

  public void set(int row, int col, T value) {
    if (!rows.containsKey(row))
      rows.put(row, new Row());
    rows.get(row).cells.put(col, value);
  }
}