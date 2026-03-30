package com.hotel.management.service;

import com.hotel.management.entity.ServiceUsage;
import com.hotel.management.repository.ServiceUsageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ServiceUsageService {

    @Autowired
    private ServiceUsageRepository serviceUsageRepository;

    public List<ServiceUsage> getAllServiceUsages() {
        return serviceUsageRepository.findAll();
    }

    public ServiceUsage saveServiceUsage(ServiceUsage usage) {
        return serviceUsageRepository.save(usage);
    }
}
