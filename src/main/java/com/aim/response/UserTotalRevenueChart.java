package com.aim.response;

import java.util.List;

public class UserTotalRevenueChart {
	List<Double> revenue;
	List<Double> grossMargin;
	List<Double> netMargin;
	List<Double> expenseTotal;
	List<Double> commissionTotal;
	
	List<Double> commissionsBDM;
	List<Double> commissionsACM;
	List<Double> commissionsREC;
	
	List<Double> expenseCon;
	List<Double> expenseW2p;
	List<Double> expenseOther;
	
	public List<Double> getRevenue() {
		return revenue;
	}
	public void setRevenue(List<Double> revenue) {
		this.revenue = revenue;
	}
	public List<Double> getGrossMargin() {
		return grossMargin;
	}
	public void setGrossMargin(List<Double> grossMargin) {
		this.grossMargin = grossMargin;
	}
	public List<Double> getNetMargin() {
		return netMargin;
	}
	public void setNetMargin(List<Double> netMargin) {
		this.netMargin = netMargin;
	}
	public List<Double> getExpenseTotal() {
		return expenseTotal;
	}
	public void setExpenseTotal(List<Double> expenseTotal) {
		this.expenseTotal = expenseTotal;
	}
	public List<Double> getCommissionTotal() {
		return commissionTotal;
	}
	public void setCommissionTotal(List<Double> commissionTotal) {
		this.commissionTotal = commissionTotal;
	}
	public List<Double> getCommissionsBDM() {
		return commissionsBDM;
	}
	public void setCommissionsBDM(List<Double> commissionsBDM) {
		this.commissionsBDM = commissionsBDM;
	}
	public List<Double> getCommissionsACM() {
		return commissionsACM;
	}
	public void setCommissionsACM(List<Double> commissionsACM) {
		this.commissionsACM = commissionsACM;
	}
	public List<Double> getCommissionsREC() {
		return commissionsREC;
	}
	public void setCommissionsREC(List<Double> commissionsREC) {
		this.commissionsREC = commissionsREC;
	}
	public List<Double> getExpenseCon() {
		return expenseCon;
	}
	public void setExpenseCon(List<Double> expenseCon) {
		this.expenseCon = expenseCon;
	}
	public List<Double> getExpenseW2p() {
		return expenseW2p;
	}
	public void setExpenseW2p(List<Double> expenseW2p) {
		this.expenseW2p = expenseW2p;
	}
	public List<Double> getExpenseOther() {
		return expenseOther;
	}
	public void setExpenseOther(List<Double> expenseOther) {
		this.expenseOther = expenseOther;
	}
	
}
