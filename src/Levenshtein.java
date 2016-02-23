/**
 * Created by HenryBallingerMcFarlane on 23/02/2016.
 */

public class Levenshtein {
    public static float min3 (float f1, float f2, float f3) {
        return (Math.min(f1, Math.min(f2, f3)));
    }

    public static float MinimumEditDistance (String needle, String haystack) {
        int needLen = needle.length() + 1;
        int hayLen = haystack.length() + 1;
        boolean [] counter = new boolean[hayLen];
        int count = 0;

        needle = needle.toLowerCase();
        haystack = haystack.toLowerCase();

        Matrix med = new Matrix(needLen, hayLen);

        for (int i = 0; i < needLen; i++)
            med.setElement(i, 0, i);

        for (int i = 0; i < hayLen; i++)
            med.setElement(0, i, i);

        for (int row = 1; row < needLen; row++)
        {
            for (int col = 1; col < hayLen; col++)
            {
                int bRow = row - 1;
                int bCol = col - 1;

                if (needle.charAt(bRow) == haystack.charAt(bCol) && !counter[col - 1] && count == 0) {
                    med.setElement(row, col, med.getElement(bRow, bCol));
                    counter[col - 1] = true;
                    count = 1;
                }
                else {
                    med.setElement(row, col, min3(med.getElement(bRow, col), med.getElement(bRow, bCol), med.getElement(row, bCol)) + 1);
                }
            }
            count = 0;
        }

        return med.getElement(needLen - 1, hayLen - 1);
    }
}
