package lazy.test.ui.controls;

import lazy.test.ui.beans.TableSize;
import lazy.test.ui.interfaces.TableContent;

import java.util.List;

/**
 * Created by sushidong on 2016/4/28.
 */
public class Table extends AbstractControl implements TableContent {
    public List<List<String>> getTableContents() {
        return be.getTableList(getValidXpath());
    }

    public String getTableCellText(int row, int col) {
        return be.getTableCellText(getValidXpath(), row, col);
    }

    public TableSize getTableSize() {
        TableSize tableSize = new TableSize();

        List<List<String>> table = be.getTableList(getValidXpath());

        tableSize.setRowCount(table.size());

        int colCount = 0;

        for(List<String> col : table) {
            if (col.size() > colCount) {
                colCount = col.size();
            }
        }

        tableSize.setColCount(colCount);

        return tableSize;
    }
}
