package lazy.test.ui.controls;

import lazy.test.ui.beans.TableSize;
import lazy.test.ui.interfaces.TableContent;

import java.util.List;

/**
 * Created by sushidong on 2016/4/28.
 */
public class Table extends AbstractControl implements TableContent {
	/** 
	 * 进入iframe，找到控件
	 * 得到table中所有单元格的文本值
	 * 可设置等待时间，以便table完全加载完成
	 * @param xpath  用于得到table对象
	 * @return 单元格中的文本值列表
	 */
	public List<List<String>> getTableContents(int waitTime) {
    	return be.getTableList(getValidXpath());
    }
	
	/** 
	 * 进入iframe，找到控件
	 * 得到table中所有单元格的文本值
	 * @param xpath  用于得到table对象
	 * @return 单元格中的文本值列表
	 */
	public List<List<String>> getTableContents() {
        return getTableContents(0);
    }
	/** 
	 * 进入iframe，找到控件
	 * 从table中指定的的单元格中得到文本值, 行列从1开始.
	 * 可设置等待时间，以便table完全加载完成
	 * @param row,col 为了使用者便于
	 * @return 单元格中的文本值
	 */
	public String getTableCellText(int row, int col, int waitTime) {
		return be.getTableCellText(getValidXpath(), row, col);
	}
	
	/** 
	 * 进入iframe，找到控件
	 * 从table中指定的的单元格中得到文本值, 行列从1开始.
	 * @param row,col 为了使用者便于
	 * @return 单元格中的文本值
	 */
    public String getTableCellText(int row, int col) {
    	return getTableCellText(row, col, 0);
    }
    
    /** 
     * 进入iframe，找到控件
	 * 得到table的大小、行数、列数
	 * 可设置等待时间，以便table完全加载完成
	 * @param xpath  用于得到table对象
	 * @return 行数、列数
	 */
    public TableSize getTableSize(int waitTime) {
        TableSize tableSize = new TableSize();

        List<List<String>> table = be.getTableList(getValidXpath(), waitTime);

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

    /** 
     * 进入iframe，找到控件
	 * 得到table的大小、行数、列数
	 * @param xpath  用于得到table对象
	 * @return 行数、列数
	 */
	public TableSize getTableSize() {
		return getTableSize(0);
	}
}
