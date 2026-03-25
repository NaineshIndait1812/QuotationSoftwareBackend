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
    private String actionDate;

	private Double gstPercent;    //GST Part
	private Double gstAmount;       //GST Part
	private Double finalAmount;        //GST Part

    private String status;

	private String aboutProject;
private String scopeOfWork;

private List<Map<String, String>> techStack;

    private List<CostItem> costBreakdown;

    private List<String> includes;
    private List<Map<String, String>> timeline;
    private String totalTimeline;

    private Map<String, String> terms;

    private List<Map<String, Object>> paymentTerms;

	private List<Map<String, Object>> maintenancePlans;

	// 🔥 SIGNATURE & AUTHORITY FIELDS

private String signature;
private String signatureUpload;
private String companyStamp;

private String authorizedName;
private String authorizedRole;
private String companyName;

private String contactNumber;
private String contactEmail;
private String location;

private String signatureNote;
    
    private String approvalToken;
    private boolean approvalUsed = false;
    private String rejectionReason;

	
    
    
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

	public List<Map<String, Object>> getPaymentTerms() {
		return paymentTerms;
	}
	public void setPaymentTerms(List<Map<String, Object>> paymentTerms) {
		this.paymentTerms = paymentTerms;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getApprovalToken() {
	    return approvalToken;
	}

	public void setApprovalToken(String approvalToken) {
	    this.approvalToken = approvalToken;
	}

	public boolean isApprovalUsed() {
	    return approvalUsed;
	}

	public void setApprovalUsed(boolean approvalUsed) {
	    this.approvalUsed = approvalUsed;
	}

	public String getRejectionReason() {
	    return rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
	    this.rejectionReason = rejectionReason;
	}

	public Double getGstPercent() {
    return gstPercent;
}

public void setGstPercent(Double gstPercent) {
    this.gstPercent = gstPercent;
}

public Double getGstAmount() {
    return gstAmount;
}

public void setGstAmount(Double gstAmount) {
    this.gstAmount = gstAmount;
}

public Double getFinalAmount() {
    return finalAmount;
}

public void setFinalAmount(Double finalAmount) {
    this.finalAmount = finalAmount;
}

public String getActionDate() {
	return actionDate;
}

public void setActionDate(String actionDate) {
	this.actionDate = actionDate;
}

public String getAboutProject() {
    return aboutProject;
}

public void setAboutProject(String aboutProject) {
    this.aboutProject = aboutProject;
}

public String getScopeOfWork() {
    return scopeOfWork;
}

public void setScopeOfWork(String scopeOfWork) {
    this.scopeOfWork = scopeOfWork;
}

public List<Map<String, String>> getTechStack() {
    return techStack;
}

public void setTechStack(List<Map<String, String>> techStack) {
    this.techStack = techStack;
}

public List<Map<String, Object>> getMaintenancePlans() {
    return maintenancePlans;
}

public void setMaintenancePlans(List<Map<String, Object>> maintenancePlans) {
    this.maintenancePlans = maintenancePlans;
}

public String getSignature() {
    return signature;
}

public void setSignature(String signature) {
    this.signature = signature;
}

public String getCompanyStamp() {
    return companyStamp;
}

public void setCompanyStamp(String companyStamp) {
    this.companyStamp = companyStamp;
}

public String getAuthorizedName() {
    return authorizedName;
}

public void setAuthorizedName(String authorizedName) {
    this.authorizedName = authorizedName;
}

public String getAuthorizedRole() {
    return authorizedRole;
}

public void setAuthorizedRole(String authorizedRole) {
    this.authorizedRole = authorizedRole;
}

public String getCompanyName() {
    return companyName;
}

public void setCompanyName(String companyName) {
    this.companyName = companyName;
}

public String getContactNumber() {
    return contactNumber;
}

public void setContactNumber(String contactNumber) {
    this.contactNumber = contactNumber;
}

public String getContactEmail() {
    return contactEmail;
}

public void setContactEmail(String contactEmail) {
    this.contactEmail = contactEmail;
}

public String getLocation() {
    return location;
}

public void setLocation(String location) {
    this.location = location;
}

public String getSignatureNote() {
    return signatureNote;
}

public void setSignatureNote(String signatureNote) {
    this.signatureNote = signatureNote;
}

public String getSignatureUpload() {
	return signatureUpload;
}

public void setSignatureUpload(String signatureUpload) {
	this.signatureUpload = signatureUpload;
}
    
}
