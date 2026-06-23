package com.caobolun.infraai.model;

import com.caobolun.infraai.config.AIModelProperties;

public record ModelTarget(
        String id,
        AIModelProperties.ModelCandidate candidate,
        AIModelProperties.ProviderConfig provider) {}
