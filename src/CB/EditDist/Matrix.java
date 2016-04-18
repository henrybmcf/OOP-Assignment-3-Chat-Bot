package CB.EditDist;

class Matrix {
    private float[][] elements;

    Matrix(int rows, int cols) {
        elements = new float[rows][cols];
    }

    void setElement(int row, int col, float value) {
        elements[row][col] = value;
    }

    float getElement(int row, int col) {
        return elements[row][col];
    }
}