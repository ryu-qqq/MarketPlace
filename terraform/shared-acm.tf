# ============================================================================
# Shared Infrastructure Reference: ACM Certificate
# ============================================================================
# References centrally managed ACM certificate from Infrastructure repository
# Used for API Gateway custom domain (if needed)
# ============================================================================

# ACM Certificate ARN
data "aws_ssm_parameter" "acm_certificate_arn" {
  name = "/shared/acm/certificate-arn"
}

# ============================================================================
# Local Variables
# ============================================================================

locals {
  certificate_arn = data.aws_ssm_parameter.acm_certificate_arn.value
}
