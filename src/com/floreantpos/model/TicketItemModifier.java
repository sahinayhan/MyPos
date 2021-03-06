/**
 * ************************************************************************
 * * The contents of this file are subject to the MRPL 1.2
 * * (the  "License"),  being   the  Mozilla   Public  License
 * * Version 1.1  with a permitted attribution clause; you may not  use this
 * * file except in compliance with the License. You  may  obtain  a copy of
 * * the License at http://www.floreantpos.org/license.html
 * * Software distributed under the License  is  distributed  on  an "AS IS"
 * * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * * License for the specific  language  governing  rights  and  limitations
 * * under the License.
 * * The Original Code is FLOREANT POS.
 * * The Initial Developer of the Original Code is OROCUBE LLC
 * * All portions are Copyright (C) 2015 OROCUBE LLC
 * * All Rights Reserved.
 * ************************************************************************
 */
package com.floreantpos.model;

import com.floreantpos.main.Application;
import com.floreantpos.model.base.BaseTicketItemModifier;
import com.floreantpos.util.NumberUtil;

public class TicketItemModifier extends BaseTicketItemModifier implements ITicketItem {
	private static final long serialVersionUID = 1L;

	//public final static int		MODIFIER_NOT_INITIALIZED	= 0;
	public final static int NORMAL_MODIFIER = 1;
	//public final static int		NO_MODIFIER					= 2;
	public final static int EXTRA_MODIFIER = 3;

	/*[CONSTRUCTOR MARKER BEGIN]*/
	public TicketItemModifier() {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public TicketItemModifier(java.lang.Integer id) {
		super(id);
	}

	/*[CONSTRUCTOR MARKER END]*/

	private boolean selected;

	boolean priceIncludesTax;

	private int tableRowNum;

	public int getTableRowNum() {
		return tableRowNum;
	}

	public void setTableRowNum(int tableRowNum) {
		this.tableRowNum = tableRowNum;
	}

	@Override
	public String toString() {
		return getName();
	}

	public boolean canAddCookingInstruction() {
		return false;
	}

//	private int getPreviousItemsCount() {
//		TicketItemModifierGroup ticketItemModifierGroup = getParent();
//		List<TicketItemModifier> ticketItemModifiers = ticketItemModifierGroup.getTicketItemModifiers();
//
//		int count = 0;
//		for (TicketItemModifier modifier : ticketItemModifiers) {
//			if (modifier == this) {
//				return count;
//			}
//			count += modifier.getItemCount();
//		}
//		return count;
//	}

	public void calculatePrice() {
		priceIncludesTax = Application.getInstance().isPriceIncludesTax();

		calculateSubTotal();
		calculateTax();
		setTotalAmount(NumberUtil.roundToTwoDigit(calculateTotal()));
	}

	private void calculateTax() {
		double tax = getSubTotalAmount() * (getTaxRate() / 100);
		double subtotal = getSubTotalAmount();
		double taxRate = getTaxRate();

		if (priceIncludesTax) {
			tax = getSubTotalAmount() * (getTaxRate() / 100);
		}
		else {
			tax = subtotal * (taxRate / 100);
		}

		setTaxAmount(NumberUtil.roundToTwoDigit(tax));
	}

	private double calculateTotal() {
		if (priceIncludesTax) {
			return getSubTotalAmount();
		}

		return getSubTotalAmount() + getTaxAmount();
	}

	private double calculateSubTotal() {
		double total = 0;

		TicketItemModifierGroup ticketItemModifierGroup = getParent();
		if (ticketItemModifierGroup == null) {
			setSubTotalAmount(total);
			return total;
		}

		//		int previousItemCount = getPreviousItemsCount();
		//		int maxItemCount = ticketItemModifierGroup.getMaxQuantity();
		//
		//		int normalItemCount = 0;
		//		int extraItemCount = 0;
		//
		//		if (previousItemCount == 0) {
		//			if (getItemCount() <= maxItemCount) {
		//				normalItemCount = getItemCount();
		//				extraItemCount = 0;
		//			}
		//			else {
		//				normalItemCount = maxItemCount;
		//				extraItemCount = getItemCount() - maxItemCount;
		//			}
		//		}
		//
		//		else {
		//			maxItemCount = maxItemCount - previousItemCount;
		//			if (maxItemCount < 0)
		//				maxItemCount = 0;
		//
		//			if (getItemCount() <= maxItemCount) {
		//				normalItemCount = getItemCount();
		//				extraItemCount = 0;
		//			}
		//			else {
		//				normalItemCount = maxItemCount;
		//				extraItemCount = getItemCount() - maxItemCount;
		//			}
		//		}
		//
		//		total = normalItemCount * getUnitPrice();
		//		total += extraItemCount * getExtraUnitPrice();
		//		total = NumberUtil.roundToTwoDigit(total);

		total = NumberUtil.roundToTwoDigit(getItemCount() * getUnitPrice());
		setSubTotalAmount(total);
		return total;
	}

	@Override
	public String getNameDisplay() {
		String display = " - " + getName();
		if(getModifierType() == NORMAL_MODIFIER) {
			display += "*";
		}

		return " - " + display; //$NON-NLS-1$
	}

	@Override
	public Double getUnitPriceDisplay() {
		return getUnitPrice();
	}

	@Override
	public Integer getItemCountDisplay() {
		return getItemCount();
	}

	@Override
	public Double getTaxAmountWithoutModifiersDisplay() {
		return getTaxAmount();
	}

	@Override
	public Double getTotalAmountWithoutModifiersDisplay() {
//		if (getModifierType() == TicketItemModifier.NO_MODIFIER) {
//			return null;
//		}

		return getTotalAmount();
	}

	@Override
	public Double getSubTotalAmountWithoutModifiersDisplay() {
//		if (getModifierType() == TicketItemModifier.NO_MODIFIER) {
//			return null;
//		}
		return getSubTotalAmount();
	}

	public boolean isPriceIncludesTax() {
		return priceIncludesTax;
	}

	public void setPriceIncludesTax(boolean priceIncludesTax) {
		this.priceIncludesTax = priceIncludesTax;
	}

	@Override
	public String getItemCode() {
		return ""; //$NON-NLS-1$
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public boolean canAddDiscount() {
		return false;
	}

	@Override
	public boolean canVoid() {
		return false;
	}

	@Override
	public boolean canAddAdOn() {
		return false;
	}

	@Override
	public void setDiscountAmount(Double amount) {
	}

	@Override
	public Double getDiscountAmount() {
		return null;
	}
}