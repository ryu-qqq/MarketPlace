# ============================================================================
# Shared SSM Parameters: Application Configuration
# ============================================================================
# 서비스별 설정값을 SSM Parameter Store로 중앙 관리합니다.
# 하드코딩 방지, 환경별 오버라이드 가능, 감사 추적 지원.
#
# 네이밍 규칙: /{project_name}/{service}/{key}
#   - /marketplace/ses/sender-email
#   - /marketplace/sentry/dsn
# ============================================================================

# ========================================
# SES Configuration
# ========================================
resource "aws_ssm_parameter" "ses_sender_email" {
  name        = "/marketplace/ses/sender-email"
  description = "AWS SES verified sender email address"
  type        = "String"
  value       = "master@connectly.co.kr"

  tags = {
    Project     = "marketplace"
    ManagedBy   = "terraform"
    Service     = "ses"
    Description = "SES verified identity for transactional emails"
  }
}

# ========================================
# Sentry Configuration
# ========================================
resource "aws_ssm_parameter" "sentry_dsn" {
  name        = "/marketplace/sentry/dsn"
  description = "Sentry DSN for error tracking"
  type        = "SecureString"
  value       = "REPLACE_WITH_ACTUAL_DSN"

  tags = {
    Project     = "marketplace"
    ManagedBy   = "terraform"
    Service     = "sentry"
    Description = "Sentry DSN for error tracking and performance monitoring"
  }

  lifecycle {
    ignore_changes = [value]
  }
}
