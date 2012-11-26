package ekaiser.nzlov.util;

import java.util.Arrays;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
public class SortableTableModel extends AbstractTableModel{
	
	private TableModel model;
	private int sortColumn;
	private Row[] rows;
	public SortableTableModel(TableModel m){
		this.model = m;
		rows = new Row[model.getRowCount()];
		for(int i =0;i<rows.length;i++){
			rows[i] = new Row(i);
		}
	}
	public void sort(int i){
		sortColumn = i;
		Arrays.sort(rows);
		fireTableDataChanged();
	}
	public Object getValueAt(int r,int c){
		return model.getValueAt(rows[r].index,c);
	}
	public boolean isCellEditable(int r,int c){
		return false;
	} 
	public void setValueAt(Object aValue ,int r,int c){
		model.setValueAt(aValue,rows[r].index,c);
	}
	public int getRowCount(){
		return model.getRowCount();
	}
	public int getColumnCount(){
		return model.getColumnCount();
	}
	public String getColumnName(int c){
		return model.getColumnName(c);
	}
	public Class getColumnClass(int c){
		return model.getColumnClass(c);
	}
	private class Row implements Comparable<Row>{
		public int index;
		public Row(int index){
			this.index = index;
		}
		public int compareTo(Row other){
			Object a = model.getValueAt(index, sortColumn);
			Object b = model.getValueAt(other.index, sortColumn);
			if(a instanceof Comparable){
				return ((Comparable)a).compareTo(b);
			}else{
				return a.toString().compareTo(b.toString());
			}
		}
	}
}
