public class Matrix {
    private float[][] elements;
    int rows;
    int cols;

    public Matrix(int rows, int cols)
    {
        this.rows = rows;
        this.cols = cols;
        elements = new float[rows][cols];
    }

    public void setElement(int row, int col, float value)
    {
        elements[row][col] = value;
    }

    public float getElement(int row, int col)
    {
        return elements[row][col];
    }
}
