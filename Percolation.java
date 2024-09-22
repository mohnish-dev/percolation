import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private final int gridSize;
    private final int gridSqared;
    private final boolean[][] grid;
    private final WeightedQuickUnionUF ufGrid;
    private final WeightedQuickUnionUF ufFull;
    private final int virtualTop;
    private final int virtualBottom;
    private int openSitesCount;

    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be greater than 0");
        }
        this.gridSize = n;
        this.gridSqared = n * n;
        this.grid = new boolean[gridSize][gridSize];
        this.ufGrid = new WeightedQuickUnionUF(gridSqared + 2); // includes virtual top and bottom
        this.ufFull = new WeightedQuickUnionUF(gridSqared + 1); // includes virtual top
        this.virtualTop = gridSqared;
        this.virtualBottom = gridSqared + 1;
        this.openSitesCount = 0;
    }

    public void open(int row, int col) {
        validate(row, col);
        int current = xyTo1D(row, col) - 1;
        // If site is already open, don't proceed
        if (isOpen(row, col)) {
            return;
        }
        // Open site
        grid[row-1][col-1] = true;
        openSitesCount++;
        if (row == 1) { // Top row
            ufGrid.union(virtualTop, current);
            ufFull.union(virtualTop, current);
        }
        if (row == gridSize) { // Bottom row
            ufGrid.union(virtualBottom, current);
        }
        // Check and open left site
        if (isOnGrid(row, col-1) && isOpen(row, col-1)) {
            ufGrid.union(current, xyTo1D(row, col - 1) - 1);
            ufFull.union(current, xyTo1D(row, col - 1) - 1);
        }
        // Check and open right site
        if (isOnGrid(row, col + 1) && isOpen(row, col + 1)) {
            ufGrid.union(current, xyTo1D(row, col + 1) - 1);
            ufFull.union(current, xyTo1D(row, col + 1) - 1);
        }
        // Check and open upper site
        if (isOnGrid(row - 1, col) && isOpen(row - 1, col)) {
            ufGrid.union(current, xyTo1D(row - 1, col) - 1);
            ufFull.union(current, xyTo1D(row - 1, col) - 1);
        }
        // Check and open lower site
        if (isOnGrid(row + 1, col) && isOpen(row + 1, col)) {
            ufGrid.union(current, xyTo1D(row + 1, col) - 1);
            ufFull.union(current, xyTo1D(row + 1, col) - 1);
        }
        
    }
    // Is the given site open?
    public boolean isOpen(int row, int col){
        validate(row, col);
        return grid[row-1][col-1];
    }
    // Returns number of open sites
    public int numberOfOpenSites(){
        return openSitesCount;
    }
    // Does the system percolates?
    public boolean percolates() {
        return ufGrid.find(virtualTop) == ufGrid.find(virtualBottom);
    }
    // Is the give site(row, col) full?
    public boolean isFull(int row, int col){
        validate(row, col);
        return ufFull.find(xyTo1D(row, col) - 1) == ufFull.find(virtualTop);
    }
    // Converts 2D index(row, col) into 1D index
    private int xyTo1D(int row, int col) {
        return gridSize * (row - 1) + col;
    }
    // Is row and col within range?
    private void validate(int row, int col){
        if (!isOnGrid(row, col)) {
            throw new IndexOutOfBoundsException("Index is out of bounds");
        }
    }
    // Is row and col within range?
    private boolean isOnGrid(int row, int col) {
        int shiftRow = row - 1;
        int shiftCol = col - 1;
        return (shiftRow >= 0 && shiftRow < gridSize && shiftCol >= 0 && shiftCol < gridSize);
    }
    // test client
    public static void main(String[] args) {
        int size = Integer.parseInt(args[0]);
        Percolation percolation = new Percolation(size);
        int argCount = args.length;
        for (int i = 1; argCount >= 2; i += 2) {
            int row = Integer.parseInt(args[i]);
            int col = Integer.parseInt(args[i + 1]);
            StdOut.printf("Adding row: %d col: %d %n", row, col);
            percolation.open(row, col);
            if (percolation.percolates()) {
                StdOut.printf("%nThe system percolates %n");
            }
            argCount -= 2;
        }
        if (!percolation.percolates()) {
            StdOut.print("Does not percolate %n");
        }
    }
}