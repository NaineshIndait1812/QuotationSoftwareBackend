package com.quotation.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document(collection = "quotations")
public class Quotation {

    @Id
    private String id;

    private String quotationNumber;
    private String date;
    private String validUntil;
    private String project;

    private String client;
    private String clientAddress;
    
    private String clientPhone;
    private String clientEmail;


    private String preparedBy;
    private String documentType;
    private String version;
    private String currency;
    private Double totalCost;

    private List<CostItem> costBreakdown;

    private List<String> includes;
    private List<Map<String, String>> timeline;
    private String totalTimeline;

    private Map<String, String> terms;

    private String projectManager;
    private String operationManager;

    private String projectManagerSignature;
    private String operationManagerSignature;
    
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

	public String getQuotationNumber() {
		return quotationNumber;
	}
	public void setQuotationNumber(String quotationNumber) {
		this.quotationNumber = quotationNumber;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getValidUntil() {
		return validUntil;
	}
	public void setValidUntil(String validUntil) {
		this.validUntil = validUntil;
	}
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public String getClient() {
		return client;
	}
	public void setClient(String client) {
		this.client = client;
	}
	public String getClientAddress() {
		return clientAddress;
	}
	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}
	
	public String getClientPhone() {
		return clientPhone;
	}
	public void setClientPhone(String clientPhone) {
		this.clientPhone = clientPhone;
	}
	public String getPreparedBy() {
		return preparedBy;
	}
	public void setPreparedBy(String preparedBy) {
		this.preparedBy = preparedBy;
	}
	public String getDocumentType() {
		return documentType;
	}
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	public Double getTotalCost() {
		return totalCost;
	}
	public void setTotalCost(Double totalCost) {
		this.totalCost = totalCost;
	}
	public List<String> getIncludes() {
		return includes;
	}
	public void setIncludes(List<String> includes) {
		this.includes = includes;
	}
	public List<Map<String, String>> getTimeline() {
		return timeline;
	}
	public void setTimeline(List<Map<String, String>> timeline) {
		this.timeline = timeline;
	}
	public String getTotalTimeline() {
		return totalTimeline;
	}
	public void setTotalTimeline(String totalTimeline) {
		this.totalTimeline = totalTimeline;
	}
	public Map<String, String> getTerms() {
		return terms;
	}
	public void setTerms(Map<String, String> terms) {
		this.terms = terms;
	}
	public String getProjectManager() {
		return projectManager;
	}
	public void setProjectManager(String projectManager) {
		this.projectManager = projectManager;
	}
	public String getOperationManager() {
		return operationManager;
	}
	public void setOperationManager(String operationManager) {
		this.operationManager = operationManager;
	}
	public String getProjectManagerSignature() {
		return projectManagerSignature;
	}
	public void setProjectManagerSignature(String projectManagerSignature) {
		this.projectManagerSignature = projectManagerSignature;
	}
	public String getOperationManagerSignature() {
		return operationManagerSignature;
	}
	public void setOperationManagerSignature(String operationManagerSignature) {
		this.operationManagerSignature = operationManagerSignature;
	}

	public List<CostItem> getCostBreakdown() {
		return costBreakdown;
	}

	public void setCostBreakdown(List<CostItem> costBreakdown) {
		this.costBreakdown = costBreakdown;
	}

	public String getClientEmail() {
		return clientEmail;
	}

	public void setClientEmail(String clientEmail) {
		this.clientEmail = clientEmail;
	}

    
}
