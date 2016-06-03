package lazy.test.ui.interfaces;

import lazy.test.ui.beans.TableSize;

import java.util.List;

/**
 * Created by sushidong on 2016/4/28.
 */
public interface TableContent {

    public List<List<String>> getTableContents(int waitTime);

    public List<List<String>> getTableContents();
    
    public String getTableCellText(int row, int col, int waitTime);
    
    public String getTableCellText(int row, int col);

    public TableSize getTableSize(int waitTime);
    
    public TableSize getTableSize();

}
