package com.sep.sep_backend.referral.controller;

import com.sep.sep_backend.referral.dto.ReferralLinkResponse;
import com.sep.sep_backend.referral.service.ReferralService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/me")
@CrossOrigin(origins = "http://localhost:4200")
public class ReferralController {

    private final ReferralService referralService;

    public ReferralController(ReferralService referralService) {
        this.referralService = referralService;
    }

    /**
     * Get or create the current user's referral link.
     * If the user doesn't have a referral code yet, one will be generated.
     */
    @GetMapping("/referral-link")
    public ResponseEntity<ReferralLinkResponse> getReferralLink() {
        ReferralLinkResponse response = referralService.getOrCreateReferralLink();
        return ResponseEntity.ok(response);
    }
}
