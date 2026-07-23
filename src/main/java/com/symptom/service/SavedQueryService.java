package com.symptom.service;

import com.symptom.entity.SavedQuery;
import com.symptom.mapper.SavedQueryMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SavedQueryService {

    private final SavedQueryMapper savedQueryMapper;

    public SavedQueryService(SavedQueryMapper savedQueryMapper) {
        this.savedQueryMapper = savedQueryMapper;
    }

    public List<SavedQuery> findAll() {
        return savedQueryMapper.findAll();
    }

    public SavedQuery findById(Integer id) {
        return savedQueryMapper.findById(id);
    }

    public void save(SavedQuery query) {
        savedQueryMapper.insert(query);
    }

    public void delete(Integer id) {
        savedQueryMapper.delete(id);
    }
}
