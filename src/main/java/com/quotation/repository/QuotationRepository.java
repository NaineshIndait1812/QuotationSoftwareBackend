package com.quotation.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.quotation.model.Quotation;

public interface QuotationRepository extends MongoRepository<Quotation, String> {
}
