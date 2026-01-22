package com.sep.sep_backend.referral.dto;

public class ReferralLinkResponse {

    private String referralCode;
    private String referralUrl;

    public ReferralLinkResponse() {
    }

    public ReferralLinkResponse(String referralCode, String referralUrl) {
        this.referralCode = referralCode;
        this.referralUrl = referralUrl;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public String getReferralUrl() {
        return referralUrl;
    }

    public void setReferralUrl(String referralUrl) {
        this.referralUrl = referralUrl;
    }
}
