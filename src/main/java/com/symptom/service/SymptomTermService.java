package com.symptom.service;

import com.symptom.entity.SymptomTerm;
import com.symptom.mapper.SymptomTermMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SymptomTermService {

    private final SymptomTermMapper symptomTermMapper;

    public SymptomTermService(SymptomTermMapper symptomTermMapper) {
        this.symptomTermMapper = symptomTermMapper;
    }

    public List<SymptomTerm> findAll() {
        return symptomTermMapper.findAll();
    }

    public SymptomTerm findById(Integer id) {
        return symptomTermMapper.findById(id);
    }

    public void save(SymptomTerm term) {
        if (term.getId() == null) {
            symptomTermMapper.insert(term);
        } else {
            symptomTermMapper.update(term);
        }
    }

    public void delete(Integer id) {
        symptomTermMapper.delete(id);
    }
}
