package com.quotation.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "employees")
public class Employee {

    @Id
    private String id;

    private String empId;
    private String name;
    private String email;
    private String phone;
    private String joinDate;
    private String password;

    public Employee() {}

    public Employee(String empId, String name, String email,
                    String phone, String joinDate, String password) {
        this.empId = empId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.joinDate = joinDate;
        this.password = password;
    }

    public String getEmpId() { return empId; }
    public String getPassword() { return password; }
    public String getname() { return name; }
    public String getemail() { return email; }
    public String getphone() { return phone; }
    public String getjoinDate() { return joinDate; }


}
