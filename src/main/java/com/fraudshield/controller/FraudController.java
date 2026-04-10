package com.fraudshield.controller;

import com.fraudshield.dto.request.EvaluateTransactionRequest;
import com.fraudshield.dto.response.FraudEvaluationResponse;
import com.fraudshield.scoring.FraudAssessmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/fraud")
class FraudController {

    private final FraudAssessmentService assessmentService;

    FraudController(FraudAssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @PostMapping("/evaluate")
    ResponseEntity<FraudEvaluationResponse> evaluate(@Valid @RequestBody EvaluateTransactionRequest request) {
        return ResponseEntity.ok(FraudEvaluationResponse.from(assessmentService.assess(request)));
    }
}
