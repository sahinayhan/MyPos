package com.floreantpos.bo.ui;

import java.awt.Color;
import java.awt.Component;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class CustomCellRenderer extends DefaultTableCellRenderer {

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

		if(value instanceof Color) {
			JLabel backgroundLabel = new JLabel();
			backgroundLabel.setOpaque(true);
			backgroundLabel.setBackground((Color) value);
			return backgroundLabel;
		}
		if(value instanceof Date) {
			value = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT).format((Date) value);
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}
}
