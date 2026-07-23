package com.symptom.service;

import com.symptom.entity.SyndromeConfig;
import com.symptom.mapper.SyndromeConfigMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SyndromeConfigService {

    private final SyndromeConfigMapper syndromeConfigMapper;

    public SyndromeConfigService(SyndromeConfigMapper syndromeConfigMapper) {
        this.syndromeConfigMapper = syndromeConfigMapper;
    }

    public List<SyndromeConfig> findAll() {
        return syndromeConfigMapper.findAll();
    }

    public SyndromeConfig findByName(String syndromeName) {
        return syndromeConfigMapper.findByName(syndromeName);
    }
}
