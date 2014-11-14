package com.floreantpos.model;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.floreantpos.model.base.BaseMenuItem;

@XmlRootElement(name="menu-item")
public class MenuItem extends BaseMenuItem {
	private static final long serialVersionUID = 1L;

	/*[CONSTRUCTOR MARKER BEGIN]*/
	public MenuItem () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public MenuItem (java.lang.Integer id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public MenuItem (
		java.lang.Integer id,
		java.lang.String name,
		java.lang.Double buyPrice,
		java.lang.Double price) {

		super (
			id,
			name,
			buyPrice,
			price);
	}

	/*[CONSTRUCTOR MARKER END]*/
	
	@Override
	@XmlTransient
	public Date getModifiedTime() {
		return super.getModifiedTime();
	}
	
	public double getPrice(Shift currentShift) {
		List<MenuItemShift> shifts = getShifts();
		double price = super.getPrice();
		
		if(currentShift == null) {
			return price;
		}
		if(shifts == null || shifts.size() == 0) {
			return price;
		}
		
//		Date formattedTicketTime = ShiftUtil.formatShiftTime(ticketCreateTime);
//		Calendar calendar = Calendar.getInstance();
//		calendar.setTime(formattedTicketTime);
//		formattedTicketTime = calendar.getTime();
//		
		for (MenuItemShift shift : shifts) {
			if(shift.getShift().equals(currentShift)) {
				return shift.getShiftPrice();
			}
//			Date startTime = shift.getShift().getStartTime();
//			Date endTime = shift.getShift().getEndTime();
//			if(startTime.after(currentShift.getStartTime()) && endTime.before(currentShift.getEndTime())) {
//				return shift.getShiftPrice();
//			}
		}
		return price;
	}

	@Override
	public String toString() {
		return getName();
	}

	public String getUniqueId() {
		return ("menu_item_" + getName() + "_" + getId()).replaceAll("\\s+", "_");
	}
}