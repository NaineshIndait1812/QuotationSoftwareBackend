package com.quotation.service;

import com.quotation.model.Notification;
import com.quotation.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NotificationService {
    @Autowired
    private NotificationRepository repository;

    public void createNotification(String msg, String recipient, String type) {
        Notification notification = new Notification(msg, recipient, type);
        repository.save(notification);
    }

    public List<Notification> getNotifications(String recipientId) {
        return repository.findByRecipientIdOrderByTimestampDesc(recipientId);
    }
    
    public void markAllAsRead(String recipientId) {
        List<Notification> list = repository.findByRecipientIdOrderByTimestampDesc(recipientId);
        list.forEach(n -> n.setRead(true));
        repository.saveAll(list);
    }
    
    
    public void deleteNotification(String id) {
        // repository.deleteById(id) looks for the @Id field in your Model
        repository.deleteById(id);
    }


    // DELETE ALL FOR EMPLOYEE
    public void deleteAllForRecipient(String recipientId) {
        repository.deleteByRecipientId(recipientId);
    }
}