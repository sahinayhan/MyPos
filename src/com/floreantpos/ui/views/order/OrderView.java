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
/*
 * OrderView.java
 *
 * Created on August 4, 2006, 6:58 PM
 */

package com.floreantpos.ui.views.order;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;

import com.floreantpos.IconFactory;
import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.PosException;
import com.floreantpos.customer.CustomerSelectionDialog;
import com.floreantpos.extension.ExtensionManager;
import com.floreantpos.extension.FloorLayoutPlugin;
import com.floreantpos.main.Application;
import com.floreantpos.model.CookingInstruction;
import com.floreantpos.model.ITicketItem;
import com.floreantpos.model.MenuCategory;
import com.floreantpos.model.MenuGroup;
import com.floreantpos.model.MenuItem;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.ShopTable;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.TicketItem;
import com.floreantpos.model.TicketItemCookingInstruction;
import com.floreantpos.model.dao.CookingInstructionDAO;
import com.floreantpos.model.dao.MenuItemDAO;
import com.floreantpos.model.dao.ShopTableDAO;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.swing.PosButton;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.floreantpos.ui.dialog.MiscTicketItemDialog;
import com.floreantpos.ui.dialog.NumberSelectionDialog2;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.views.CookingInstructionSelectionView;
import com.floreantpos.util.PosGuiUtil;

/**
 *
 * @author  MShahriar
 */
public class OrderView extends ViewPanel {
	private HashMap<String, JComponent> views = new HashMap<String, JComponent>();

	public final static String VIEW_NAME = "ORDER_VIEW"; //$NON-NLS-1$
	private static OrderView instance;

	private Ticket currentTicket;

	private CardLayout cardLayout = new CardLayout();
	private com.floreantpos.ui.views.order.CategoryView categoryView = new com.floreantpos.ui.views.order.CategoryView();
	private com.floreantpos.swing.TransparentPanel midContainer = new com.floreantpos.swing.TransparentPanel(cardLayout);
	private com.floreantpos.ui.views.order.TicketView ticketView = new com.floreantpos.ui.views.order.TicketView();

	private GroupView groupView = new GroupView();
	private MenuItemView itemView = new MenuItemView();
	private OrderController orderController = new OrderController(this);

	private JPanel actionButtonPanel = new JPanel(new MigLayout("fill, ins 2", "sg, fill", ""));

	private com.floreantpos.swing.PosButton btnDone = new com.floreantpos.swing.PosButton(com.floreantpos.POSConstants.SAVE_BUTTON_TEXT);
	private com.floreantpos.swing.PosButton btnSend = new com.floreantpos.swing.PosButton(com.floreantpos.POSConstants.SEND_TO_KITCHEN);
	private com.floreantpos.swing.PosButton btnCancel = new com.floreantpos.swing.PosButton(POSConstants.CANCEL_BUTTON_TEXT);
	private com.floreantpos.swing.PosButton btnGuestNo = new com.floreantpos.swing.PosButton(POSConstants.GUEST_NO_BUTTON_TEXT);
	private com.floreantpos.swing.PosButton btnMisc = new com.floreantpos.swing.PosButton(POSConstants.MISC_BUTTON_TEXT);
	private com.floreantpos.swing.PosButton btnOrderType = new com.floreantpos.swing.PosButton(POSConstants.ORDER_TYPE_BUTTON_TEXT);
	private com.floreantpos.swing.PosButton btnTableNumber = new com.floreantpos.swing.PosButton(POSConstants.TABLE_NO_BUTTON_TEXT);
	private com.floreantpos.swing.PosButton btnCustomer = new PosButton(POSConstants.CUSTOMER_SELECTION_BUTTON_TEXT);
	private com.floreantpos.swing.PosButton btnSearchItem = new PosButton(POSConstants.SEARCH_ITEM_BUTTON_TEXT);
	private PosButton btnCookingInstruction = new PosButton(IconFactory.getIcon("/ui_icons/", "cooking-instruction.png"));
	private PosButton btnDiscount = new PosButton(Messages.getString("TicketView.43")); //$NON-NLS-1$
	/** Creates new form OrderView */
	private OrderView() {
		initComponents();
	}

	public void addView(final String viewName, final JComponent view) {
		JComponent oldView = views.get(viewName);
		if (oldView != null) {
			return;
		}

		midContainer.add(view, viewName);
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
	private void initComponents() {
		setOpaque(false);
		setLayout(new java.awt.BorderLayout(10, 10));

		midContainer.setOpaque(false);

		add(categoryView, java.awt.BorderLayout.EAST);
		add(ticketView, java.awt.BorderLayout.WEST);
		add(midContainer, java.awt.BorderLayout.CENTER);
		add(actionButtonPanel, java.awt.BorderLayout.SOUTH);

		addView(GroupView.VIEW_NAME, groupView);
		addView(MenuItemView.VIEW_NAME, itemView);
		addView("VIEW_EMPTY", new com.floreantpos.swing.TransparentPanel()); //$NON-NLS-1$

		addActionButtonPanel();

		showView("VIEW_EMPTY"); //$NON-NLS-1$
	}// </editor-fold>//GEN-END:initComponents

	
	public  void updateSelectionView() {
		
		Object selectedObject = ticketView.ticketViewerTable.getSelected();

		OrderView orderView = OrderView.getInstance();

		TicketItem selectedTicketItem = null;
		if (selectedObject instanceof TicketItem) {
			selectedTicketItem = (TicketItem) selectedObject;
			MenuItemDAO dao = new MenuItemDAO();
			MenuItem menuItem = dao.get(selectedTicketItem.getItemId());

			if (menuItem != null) {
				MenuGroup menuGroup = menuItem.getParent();
				MenuItemView itemView = OrderView.getInstance().getItemView();
				if (!menuGroup.equals(itemView.getMenuGroup())) {
					itemView.setMenuGroup(menuGroup);
				}

				orderView.showView(MenuItemView.VIEW_NAME);
				itemView.selectItem(menuItem);

				MenuCategory menuCategory = menuGroup.getParent();
				orderView.getCategoryView().setSelectedCategory(menuCategory);
			}
			//			}
		}
		
		actionUpdate(null);
		//		else if (selectedObject instanceof TicketItemModifier) {
		//			selectedTicketItem = ((TicketItemModifier) selectedObject).getParent().getParent();
		//			if (selectedTicketItem == null)
		//				return;
		//
		//			ModifierView modifierView = orderView.getModifierView();
		//
		//			if (selectedTicketItem.isHasModifiers()) {
		//				MenuItemDAO dao = new MenuItemDAO();
		//				MenuItem menuItem = dao.get(selectedTicketItem.getItemId());
		//				if (!menuItem.equals(modifierView.getMenuItem())) {
		//					menuItem = dao.initialize(menuItem);
		//					modifierView.setMenuItem(menuItem, selectedTicketItem);
		//				}
		//
		//				MenuCategory menuCategory = menuItem.getParent().getParent();
		//				orderView.getCategoryView().setSelectedCategory(menuCategory);
		//
		//				TicketItemModifier ticketItemModifier = (TicketItemModifier) selectedObject;
		//				ticketItemModifier.setSelected(true);
		//				modifierView.select(ticketItemModifier);
		//
		//				orderView.showView(ModifierView.VIEW_NAME);
		//			}
		//		}
	}
	private void addActionButtonPanel() {
		
		
		ticketView.ticketViewerTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					updateSelectionView();
				}
			}
		});
		
		
		
		
		btnDone.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {

					ticketView.doFinishOrder();

				} catch (StaleObjectStateException x) {
					POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("TicketView.22")); //$NON-NLS-1$
					return;
				} catch (PosException x) {
					POSMessageDialog.showError(x.getMessage());
				} catch (Exception x) {
					POSMessageDialog.showError(Application.getPosWindow(), POSConstants.ERROR_MESSAGE, x);
				}
			}
		});

		btnCancel.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				ticketView.doCancelOrder();
			}
		});

		btnSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {

					ticketView.sendTicketToKitchen();
					ticketView.updateView();
					POSMessageDialog.showMessage("Items sent to kitchen");

				} catch (StaleObjectStateException x) {
					POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("TicketView.22")); //$NON-NLS-1$
					return;
				} catch (PosException x) {
					POSMessageDialog.showError(x.getMessage());
				} catch (Exception x) {
					POSMessageDialog.showError(Application.getPosWindow(), POSConstants.ERROR_MESSAGE, x);
				}

			}
		});

		btnSearchItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				searchItem();
			}
		});

		btnOrderType.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				// doViewOrderInfo();
				doChangeOrderType();
			}
		});

		btnCustomer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doAddEditCustomer();
			}
		});

		btnMisc.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				doInsertMisc(evt);
			}
		});

		btnGuestNo.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnCustomerNumberActionPerformed();
			}
		});

		btnTableNumber.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				updateTableNumber();
			}
		});

		btnCookingInstruction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doAddCookingInstruction();
			}
		});

		btnDiscount.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addDiscount();
			}
		});

		actionButtonPanel.add(btnCancel);
		actionButtonPanel.add(btnDone);
		actionButtonPanel.add(btnSend);
		actionButtonPanel.add(btnCookingInstruction);
		actionButtonPanel.add(btnDiscount);
		actionButtonPanel.add(btnMisc);
		actionButtonPanel.add(btnSearchItem);
		actionButtonPanel.add(btnOrderType);
		actionButtonPanel.add(btnCustomer);
		actionButtonPanel.add(btnTableNumber);
		actionButtonPanel.add(btnGuestNo);

	}

	//

	public void searchItem() {
		int itemId = NumberSelectionDialog2.takeIntInput(Messages.getString("TicketView.44")); //$NON-NLS-1$

		if (itemId == -1) {
			return;
		}

		MenuItem menuItem = MenuItemDAO.getInstance().get(itemId);
		if (menuItem == null) {
			POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("TicketView.45")); //$NON-NLS-1$
			return;
		}
		orderController.itemSelected(menuItem);
	}

	public void updateTableNumber() {
		Session session = null;
		org.hibernate.Transaction transaction = null;

		try {

			Ticket thisTicket = currentTicket;

			FloorLayoutPlugin floorLayoutPlugin = (FloorLayoutPlugin) ExtensionManager.getPlugin(FloorLayoutPlugin.class);
			List<ShopTable> tables = null;

			if (floorLayoutPlugin != null) {
				tables = floorLayoutPlugin.captureTableNumbers(thisTicket);
			}
			else {
				tables = PosGuiUtil.captureTable(thisTicket);
			}

			if (tables == null) {
				return;
			}

			session = TicketDAO.getInstance().createNewSession();
			transaction = session.beginTransaction();

			clearShopTable(session, thisTicket);
			session.saveOrUpdate(thisTicket);

			for (ShopTable shopTable : tables) {
				shopTable.setServing(true);
				session.merge(shopTable);

				thisTicket.addTable(shopTable.getTableNumber());
			}

			session.merge(thisTicket);
			transaction.commit();

			actionUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			transaction.rollback();
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	private void clearShopTable(Session session, Ticket thisTicket) {
		ShopTableDAO shopTableDao = ShopTableDAO.getInstance();
		List<ShopTable> tables2 = shopTableDao.getTables(thisTicket);

		if (tables2 == null)
			return;

		shopTableDao.releaseAndDeleteTicketTables(thisTicket);

		tables2.clear();
	}

	protected void btnCustomerNumberActionPerformed() {// GEN-FIRST:event_btnCustomerNumberActionPerformed
		Ticket thisTicket = currentTicket;
		int guestNumber = thisTicket.getNumberOfGuests();

		NumberSelectionDialog2 dialog = new NumberSelectionDialog2();
		dialog.setTitle(com.floreantpos.POSConstants.NUMBER_OF_GUESTS);
		dialog.setValue(guestNumber);
		dialog.pack();
		dialog.open();

		if (dialog.isCanceled()) {
			return;
		}

		guestNumber = (int) dialog.getValue();
		if (guestNumber == 0) {
			POSMessageDialog.showError(Application.getPosWindow(), com.floreantpos.POSConstants.GUEST_NUMBER_CANNOT_BE_0);
			return;
		}

		thisTicket.setNumberOfGuests(guestNumber);
		actionUpdate();
	}// GEN-LAST:event_btnCustomerNumberActionPerformed

	protected void doInsertMisc(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_doInsertMisc
		MiscTicketItemDialog dialog = new MiscTicketItemDialog();
		dialog.setSize(900, 580);
		dialog.open();
		if (!dialog.isCanceled()) {
			TicketItem ticketItem = dialog.getTicketItem();
			ticketItem.setTicket(currentTicket);
			ticketItem.calculatePrice();
			ticketView.addTicketItem(ticketItem);
		}
	}// GEN-LAST:event_doInsertMisc

	public void setOrderType(OrderType orderType) {
		currentTicket.setType(orderType);

		btnGuestNo.setEnabled(orderType == OrderType.DINE_IN);
		btnTableNumber.setEnabled(orderType == OrderType.DINE_IN);
	}

	public void doChangeOrderType() {
		OrderTypeSelectionDialog dialog = new OrderTypeSelectionDialog();
		dialog.open();

		if (dialog.isCanceled())
			return;

		OrderType selectedOrderType = dialog.getSelectedOrderType();
		setOrderType(selectedOrderType);
	}

	protected void doAddEditCustomer() {
		CustomerSelectionDialog dialog = new CustomerSelectionDialog(currentTicket);
		dialog.setSize(800, 650);
		dialog.open();
	}

	protected void addDiscount() {
		ITicketItem selectedObject = (ITicketItem) ticketView.ticketViewerTable.getSelected();
		if (selectedObject == null || !selectedObject.canAddDiscount()) {
			return;
		}

		double d = NumberSelectionDialog2.takeDoubleInput(
				Messages.getString("TicketView.39"), Messages.getString("TicketView.40"), selectedObject.getDiscountAmount()); //$NON-NLS-1$ //$NON-NLS-2$
		if (Double.isNaN(d)) {
			return;
		}

		if (selectedObject instanceof TicketItem) {
			((TicketItem) selectedObject).setDiscountRate(-1.0);
		}

		selectedObject.setDiscountAmount(d);
		ticketView.ticketViewerTable.repaint();
		ticketView.updateView();
	}

	protected void doAddCookingInstruction() {

		try {
			Object object = ticketView.ticketViewerTable.getSelected();
			if (!(object instanceof TicketItem)) {
				POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("TicketView.20")); //$NON-NLS-1$
				return;
			}

			TicketItem ticketItem = (TicketItem) object;

			if (ticketItem.isPrintedToKitchen()) {
				POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("TicketView.21")); //$NON-NLS-1$
				return;
			}

			List<CookingInstruction> list = CookingInstructionDAO.getInstance().findAll();
			CookingInstructionSelectionView cookingInstructionSelectionView = new CookingInstructionSelectionView();
			BeanEditorDialog dialog = new BeanEditorDialog(cookingInstructionSelectionView);
			dialog.setBean(list);
			dialog.setSize(800, 600);
			dialog.setLocationRelativeTo(Application.getPosWindow());
			dialog.setVisible(true);

			if (dialog.isCanceled()) {
				return;
			}

			List<TicketItemCookingInstruction> instructions = cookingInstructionSelectionView.getTicketItemCookingInstructions();
			ticketItem.addCookingInstructions(instructions);

			ticketView.ticketViewerTable.updateView();
		} catch (Exception e) {
			e.printStackTrace();
			POSMessageDialog.showError(e.getMessage());
		}
	}

	public void actionUpdate() {

		if (currentTicket != null) {
			if (currentTicket.getType() != OrderType.DINE_IN) {
				btnGuestNo.setEnabled(false);
				btnTableNumber.setEnabled(false);
			}
			else {
				btnGuestNo.setEnabled(true);
				btnTableNumber.setEnabled(true);

				// btnGuestNo.setText(currentTicket.getNumberOfGuests() +
				// " " + POSConstants.GUEST + "s");
				// btnTableNumber.setText(POSConstants.RECEIPT_REPORT_TABLE_NO_LABEL
				// + ": " + currentTicket.getTableNumbers());
			}
		}
	}

	public void actionUpdate(ITicketItem item) {

		if (item == null) {
			btnCookingInstruction.setEnabled(false);
			btnDiscount.setEnabled(false);
			//				btnVoid.setEnabled(false);
			//				btnAddOn.setEnabled(false);
			return;
		}

		btnCookingInstruction.setEnabled(item.canAddCookingInstruction());
		btnDiscount.setEnabled(item.canAddDiscount());
		//			btnVoid.setEnabled(item.canAddAdOn());
		//			btnAddOn.setEnabled(item.canVoid());
	}

	///
	public void showView(final String viewName) {
		cardLayout.show(midContainer, viewName);
	}

	public com.floreantpos.ui.views.order.CategoryView getCategoryView() {
		return categoryView;
	}

	public void setCategoryView(com.floreantpos.ui.views.order.CategoryView categoryView) {
		this.categoryView = categoryView;
	}

	public GroupView getGroupView() {
		return groupView;
	}

	public void setGroupView(GroupView groupView) {
		this.groupView = groupView;
	}

	public MenuItemView getItemView() {
		return itemView;
	}

	public void setItemView(MenuItemView itemView) {
		this.itemView = itemView;
	}

	public com.floreantpos.ui.views.order.TicketView getTicketView() {
		return ticketView;
	}

	public void setTicketView(com.floreantpos.ui.views.order.TicketView ticketView) {
		this.ticketView = ticketView;
	}

	public OrderController getOrderController() {
		return orderController;
	}

	public Ticket getCurrentTicket() {
		return currentTicket;
	}

	public void setCurrentTicket(Ticket currentTicket) {
		this.currentTicket = currentTicket;

		ticketView.setTicket(currentTicket);
		resetView();
	}

	public synchronized static OrderView getInstance() {
		if (instance == null) {
			instance = new OrderView();
		}
		return instance;
	}

	public void resetView() {
	}

	@Override
	public void setVisible(boolean aFlag) {
		if (aFlag) {
			try {
				categoryView.initialize();
			} catch (Throwable t) {
				POSMessageDialog.showError(Application.getPosWindow(), com.floreantpos.POSConstants.ERROR_MESSAGE, t);
			}
		}
		else {
			categoryView.cleanup();
		}
		super.setVisible(aFlag);
	}

	@Override
	public String getViewName() {
		return VIEW_NAME;
	}
}