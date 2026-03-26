# ========================================
# SQS Queues for MarketPlace Inspection Pipeline (Prod)
# ========================================
# 3-stage inspection pipeline:
# 1. Scoring: AI-based product quality scoring
# 2. Enhancement: LLM-powered content enhancement
# 3. Verification: Final quality verification
# ========================================

# ========================================
# Common Tags (for governance)
# ========================================
locals {
  common_tags = {
    environment  = var.environment
    service_name = "${var.project_name}-sqs"
    team         = "platform-team"
    owner        = "platform@ryuqqq.com"
    cost_center  = "engineering"
    project      = var.project_name
    data_class   = "internal"
  }

  queue_names = {
    scoring      = "${var.environment}-${var.project_name}-inspection-scoring"
    enhancement  = "${var.environment}-${var.project_name}-inspection-enhancement"
    verification = "${var.environment}-${var.project_name}-inspection-verification"
  }

  # OutboundSync queue
  outbound_sync_queue_name = "${var.environment}-${var.project_name}-outbound-sync"

  # Claim Outbox queues
  claim_outbox_queue_names = {
    cancel   = "${var.environment}-${var.project_name}-cancel-outbox"
    refund   = "${var.environment}-${var.project_name}-refund-outbox"
    exchange = "${var.environment}-${var.project_name}-exchange-outbox"
    qna      = "${var.environment}-${var.project_name}-qna-outbox"
  }

  # Intelligence Pipeline queues
  intelligence_queue_names = {
    orchestration        = "${var.environment}-${var.project_name}-intelligence-orchestration"
    description_analysis = "${var.environment}-${var.project_name}-intelligence-description-analysis"
    option_analysis      = "${var.environment}-${var.project_name}-intelligence-option-analysis"
    notice_analysis      = "${var.environment}-${var.project_name}-intelligence-notice-analysis"
    aggregation          = "${var.environment}-${var.project_name}-intelligence-aggregation"
  }
}

data "aws_caller_identity" "current" {}

# ========================================
# KMS Key for SQS Encryption
# ========================================
resource "aws_kms_key" "sqs" {
  description             = "KMS key for MarketPlace SQS queue encryption"
  deletion_window_in_days = 30
  enable_key_rotation     = true

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid    = "Enable IAM User Permissions"
        Effect = "Allow"
        Principal = {
          AWS = "arn:aws:iam::${data.aws_caller_identity.current.account_id}:root"
        }
        Action   = "kms:*"
        Resource = "*"
      },
      {
        Sid    = "Allow SQS Service"
        Effect = "Allow"
        Principal = {
          Service = "sqs.amazonaws.com"
        }
        Action = [
          "kms:Decrypt",
          "kms:GenerateDataKey*"
        ]
        Resource = "*"
      }
    ]
  })

  tags = merge(local.common_tags, {
    Name      = "${var.project_name}-sqs-kms-${var.environment}"
    Lifecycle = "production"
    ManagedBy = "terraform"
  })
}

resource "aws_kms_alias" "sqs" {
  name          = "alias/${var.project_name}-sqs-${var.environment}"
  target_key_id = aws_kms_key.sqs.key_id
}

# ========================================
# DLQ: Inspection Scoring
# ========================================
resource "aws_sqs_queue" "inspection_scoring_dlq" {
  name                      = "${local.queue_names.scoring}-dlq"
  message_retention_seconds = 1209600 # 14 days
  kms_master_key_id         = aws_kms_key.sqs.arn

  tags = merge(local.common_tags, {
    Name    = "${local.queue_names.scoring}-dlq"
    Purpose = "Dead letter queue for inspection scoring"
  })
}

# ========================================
# Queue: Inspection Scoring
# ========================================
resource "aws_sqs_queue" "inspection_scoring" {
  name                       = local.queue_names.scoring
  visibility_timeout_seconds = 300    # 5 minutes
  message_retention_seconds  = 345600 # 4 days
  receive_wait_time_seconds  = 20     # Long polling
  kms_master_key_id          = aws_kms_key.sqs.arn

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.inspection_scoring_dlq.arn
    maxReceiveCount     = 3
  })

  tags = merge(local.common_tags, {
    Name    = local.queue_names.scoring
    Purpose = "AI-based product quality scoring"
  })
}

# ========================================
# DLQ: Inspection Enhancement
# ========================================
resource "aws_sqs_queue" "inspection_enhancement_dlq" {
  name                      = "${local.queue_names.enhancement}-dlq"
  message_retention_seconds = 1209600 # 14 days
  kms_master_key_id         = aws_kms_key.sqs.arn

  tags = merge(local.common_tags, {
    Name    = "${local.queue_names.enhancement}-dlq"
    Purpose = "Dead letter queue for inspection enhancement"
  })
}

# ========================================
# Queue: Inspection Enhancement
# ========================================
resource "aws_sqs_queue" "inspection_enhancement" {
  name                       = local.queue_names.enhancement
  visibility_timeout_seconds = 600    # 10 minutes
  message_retention_seconds  = 345600 # 4 days
  receive_wait_time_seconds  = 20     # Long polling
  kms_master_key_id          = aws_kms_key.sqs.arn

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.inspection_enhancement_dlq.arn
    maxReceiveCount     = 3
  })

  tags = merge(local.common_tags, {
    Name    = local.queue_names.enhancement
    Purpose = "LLM-powered content enhancement"
  })
}

# ========================================
# DLQ: Inspection Verification
# ========================================
resource "aws_sqs_queue" "inspection_verification_dlq" {
  name                      = "${local.queue_names.verification}-dlq"
  message_retention_seconds = 1209600 # 14 days
  kms_master_key_id         = aws_kms_key.sqs.arn

  tags = merge(local.common_tags, {
    Name    = "${local.queue_names.verification}-dlq"
    Purpose = "Dead letter queue for inspection verification"
  })
}

# ========================================
# Queue: Inspection Verification
# ========================================
resource "aws_sqs_queue" "inspection_verification" {
  name                       = local.queue_names.verification
  visibility_timeout_seconds = 600    # 10 minutes
  message_retention_seconds  = 345600 # 4 days
  receive_wait_time_seconds  = 20     # Long polling
  kms_master_key_id          = aws_kms_key.sqs.arn

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.inspection_verification_dlq.arn
    maxReceiveCount     = 3
  })

  tags = merge(local.common_tags, {
    Name    = local.queue_names.verification
    Purpose = "Final quality verification"
  })
}

# ========================================
# OutboundSync Queue (DLQ)
# ========================================
resource "aws_sqs_queue" "outbound_sync_dlq" {
  name                      = "${local.outbound_sync_queue_name}-dlq"
  message_retention_seconds = 1209600 # 14 days
  kms_master_key_id         = aws_kms_key.sqs.arn

  tags = merge(local.common_tags, {
    Name    = "${local.outbound_sync_queue_name}-dlq"
    Purpose = "Dead letter queue for outbound sync"
  })
}

# ========================================
# OutboundSync Queue
# ========================================
resource "aws_sqs_queue" "outbound_sync" {
  name                       = local.outbound_sync_queue_name
  visibility_timeout_seconds = 300    # 5 minutes
  message_retention_seconds  = 345600 # 4 days
  receive_wait_time_seconds  = 20     # Long polling
  kms_master_key_id          = aws_kms_key.sqs.arn

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.outbound_sync_dlq.arn
    maxReceiveCount     = 3
  })

  tags = merge(local.common_tags, {
    Name    = local.outbound_sync_queue_name
    Purpose = "Outbound sync message relay to external channels"
  })
}

# ========================================
# Intelligence Pipeline: Orchestration Queue
# ========================================
resource "aws_sqs_queue" "intelligence_orchestration_dlq" {
  name                      = "${local.intelligence_queue_names.orchestration}-dlq"
  message_retention_seconds = 1209600 # 14 days
  kms_master_key_id         = aws_kms_key.sqs.arn

  tags = merge(local.common_tags, {
    Name    = "${local.intelligence_queue_names.orchestration}-dlq"
    Purpose = "DLQ for intelligence orchestration"
  })
}

resource "aws_sqs_queue" "intelligence_orchestration" {
  name                       = local.intelligence_queue_names.orchestration
  visibility_timeout_seconds = 120    # 2 minutes
  message_retention_seconds  = 345600 # 4 days
  receive_wait_time_seconds  = 20
  kms_master_key_id          = aws_kms_key.sqs.arn

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.intelligence_orchestration_dlq.arn
    maxReceiveCount     = 3
  })

  tags = merge(local.common_tags, {
    Name    = local.intelligence_queue_names.orchestration
    Purpose = "ProductProfile creation and analyzer dispatch"
  })
}

# ========================================
# Intelligence Pipeline: Description Analysis Queue
# ========================================
resource "aws_sqs_queue" "intelligence_description_analysis_dlq" {
  name                      = "${local.intelligence_queue_names.description_analysis}-dlq"
  message_retention_seconds = 1209600
  kms_master_key_id         = aws_kms_key.sqs.arn

  tags = merge(local.common_tags, {
    Name    = "${local.intelligence_queue_names.description_analysis}-dlq"
    Purpose = "DLQ for description analysis"
  })
}

resource "aws_sqs_queue" "intelligence_description_analysis" {
  name                       = local.intelligence_queue_names.description_analysis
  visibility_timeout_seconds = 600    # 10 minutes (LLM call)
  message_retention_seconds  = 345600
  receive_wait_time_seconds  = 20
  kms_master_key_id          = aws_kms_key.sqs.arn

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.intelligence_description_analysis_dlq.arn
    maxReceiveCount     = 3
  })

  tags = merge(local.common_tags, {
    Name    = local.intelligence_queue_names.description_analysis
    Purpose = "Description text AI analysis - Sonnet"
  })
}

# ========================================
# Intelligence Pipeline: Option Analysis Queue
# ========================================
resource "aws_sqs_queue" "intelligence_option_analysis_dlq" {
  name                      = "${local.intelligence_queue_names.option_analysis}-dlq"
  message_retention_seconds = 1209600
  kms_master_key_id         = aws_kms_key.sqs.arn

  tags = merge(local.common_tags, {
    Name    = "${local.intelligence_queue_names.option_analysis}-dlq"
    Purpose = "DLQ for option analysis"
  })
}

resource "aws_sqs_queue" "intelligence_option_analysis" {
  name                       = local.intelligence_queue_names.option_analysis
  visibility_timeout_seconds = 300    # 5 minutes
  message_retention_seconds  = 345600
  receive_wait_time_seconds  = 20
  kms_master_key_id          = aws_kms_key.sqs.arn

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.intelligence_option_analysis_dlq.arn
    maxReceiveCount     = 3
  })

  tags = merge(local.common_tags, {
    Name    = local.intelligence_queue_names.option_analysis
    Purpose = "Canonical option mapping analysis - Haiku"
  })
}

# ========================================
# Intelligence Pipeline: Notice Analysis Queue
# ========================================
resource "aws_sqs_queue" "intelligence_notice_analysis_dlq" {
  name                      = "${local.intelligence_queue_names.notice_analysis}-dlq"
  message_retention_seconds = 1209600
  kms_master_key_id         = aws_kms_key.sqs.arn

  tags = merge(local.common_tags, {
    Name    = "${local.intelligence_queue_names.notice_analysis}-dlq"
    Purpose = "DLQ for notice analysis"
  })
}

resource "aws_sqs_queue" "intelligence_notice_analysis" {
  name                       = local.intelligence_queue_names.notice_analysis
  visibility_timeout_seconds = 300    # 5 minutes
  message_retention_seconds  = 345600
  receive_wait_time_seconds  = 20
  kms_master_key_id          = aws_kms_key.sqs.arn

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.intelligence_notice_analysis_dlq.arn
    maxReceiveCount     = 3
  })

  tags = merge(local.common_tags, {
    Name    = local.intelligence_queue_names.notice_analysis
    Purpose = "Notice completion analysis - Haiku"
  })
}

# ========================================
# Intelligence Pipeline: Aggregation Queue
# ========================================
resource "aws_sqs_queue" "intelligence_aggregation_dlq" {
  name                      = "${local.intelligence_queue_names.aggregation}-dlq"
  message_retention_seconds = 1209600
  kms_master_key_id         = aws_kms_key.sqs.arn

  tags = merge(local.common_tags, {
    Name    = "${local.intelligence_queue_names.aggregation}-dlq"
    Purpose = "DLQ for intelligence aggregation"
  })
}

resource "aws_sqs_queue" "intelligence_aggregation" {
  name                       = local.intelligence_queue_names.aggregation
  visibility_timeout_seconds = 300    # 5 minutes
  message_retention_seconds  = 345600
  receive_wait_time_seconds  = 20
  kms_master_key_id          = aws_kms_key.sqs.arn

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.intelligence_aggregation_dlq.arn
    maxReceiveCount     = 3
  })

  tags = merge(local.common_tags, {
    Name    = local.intelligence_queue_names.aggregation
    Purpose = "Final analysis aggregation and decision"
  })
}

# ========================================
# Claim Outbox: Cancel
# ========================================
resource "aws_sqs_queue" "cancel_outbox_dlq" {
  name                      = "${local.claim_outbox_queue_names.cancel}-dlq"
  message_retention_seconds = 1209600
  kms_master_key_id         = aws_kms_key.sqs.arn
  tags = merge(local.common_tags, { Name = "${local.claim_outbox_queue_names.cancel}-dlq", Purpose = "DLQ for cancel outbox" })
}

resource "aws_sqs_queue" "cancel_outbox" {
  name                       = local.claim_outbox_queue_names.cancel
  visibility_timeout_seconds = 300
  message_retention_seconds  = 345600
  receive_wait_time_seconds  = 20
  kms_master_key_id          = aws_kms_key.sqs.arn
  redrive_policy = jsonencode({ deadLetterTargetArn = aws_sqs_queue.cancel_outbox_dlq.arn, maxReceiveCount = 3 })
  tags = merge(local.common_tags, { Name = local.claim_outbox_queue_names.cancel, Purpose = "Cancel claim outbox relay" })
}

# ========================================
# Claim Outbox: Refund
# ========================================
resource "aws_sqs_queue" "refund_outbox_dlq" {
  name                      = "${local.claim_outbox_queue_names.refund}-dlq"
  message_retention_seconds = 1209600
  kms_master_key_id         = aws_kms_key.sqs.arn
  tags = merge(local.common_tags, { Name = "${local.claim_outbox_queue_names.refund}-dlq", Purpose = "DLQ for refund outbox" })
}

resource "aws_sqs_queue" "refund_outbox" {
  name                       = local.claim_outbox_queue_names.refund
  visibility_timeout_seconds = 300
  message_retention_seconds  = 345600
  receive_wait_time_seconds  = 20
  kms_master_key_id          = aws_kms_key.sqs.arn
  redrive_policy = jsonencode({ deadLetterTargetArn = aws_sqs_queue.refund_outbox_dlq.arn, maxReceiveCount = 3 })
  tags = merge(local.common_tags, { Name = local.claim_outbox_queue_names.refund, Purpose = "Refund claim outbox relay" })
}

# ========================================
# Claim Outbox: Exchange
# ========================================
resource "aws_sqs_queue" "exchange_outbox_dlq" {
  name                      = "${local.claim_outbox_queue_names.exchange}-dlq"
  message_retention_seconds = 1209600
  kms_master_key_id         = aws_kms_key.sqs.arn
  tags = merge(local.common_tags, { Name = "${local.claim_outbox_queue_names.exchange}-dlq", Purpose = "DLQ for exchange outbox" })
}

resource "aws_sqs_queue" "exchange_outbox" {
  name                       = local.claim_outbox_queue_names.exchange
  visibility_timeout_seconds = 300
  message_retention_seconds  = 345600
  receive_wait_time_seconds  = 20
  kms_master_key_id          = aws_kms_key.sqs.arn
  redrive_policy = jsonencode({ deadLetterTargetArn = aws_sqs_queue.exchange_outbox_dlq.arn, maxReceiveCount = 3 })
  tags = merge(local.common_tags, { Name = local.claim_outbox_queue_names.exchange, Purpose = "Exchange claim outbox relay" })
}

# ========================================
# Claim Outbox: QnA
# ========================================
resource "aws_sqs_queue" "qna_outbox_dlq" {
  name                      = "${local.claim_outbox_queue_names.qna}-dlq"
  message_retention_seconds = 1209600
  kms_master_key_id         = aws_kms_key.sqs.arn
  tags = merge(local.common_tags, { Name = "${local.claim_outbox_queue_names.qna}-dlq", Purpose = "DLQ for QnA outbox" })
}

resource "aws_sqs_queue" "qna_outbox" {
  name                       = local.claim_outbox_queue_names.qna
  visibility_timeout_seconds = 300
  message_retention_seconds  = 345600
  receive_wait_time_seconds  = 20
  kms_master_key_id          = aws_kms_key.sqs.arn
  redrive_policy = jsonencode({ deadLetterTargetArn = aws_sqs_queue.qna_outbox_dlq.arn, maxReceiveCount = 3 })
  tags = merge(local.common_tags, { Name = local.claim_outbox_queue_names.qna, Purpose = "QnA outbox relay" })
}

# ========================================
# IAM Policy: SQS Access for ECS Tasks
# ========================================
resource "aws_iam_policy" "sqs_access" {
  name        = "${var.project_name}-sqs-access-${var.environment}"
  description = "IAM policy for MarketPlace SQS queue access"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid    = "SQSAccess"
        Effect = "Allow"
        Action = [
          "sqs:SendMessage",
          "sqs:ReceiveMessage",
          "sqs:DeleteMessage",
          "sqs:GetQueueAttributes",
          "sqs:GetQueueUrl",
          "sqs:ChangeMessageVisibility"
        ]
        Resource = [
          aws_sqs_queue.inspection_scoring.arn,
          aws_sqs_queue.inspection_scoring_dlq.arn,
          aws_sqs_queue.inspection_enhancement.arn,
          aws_sqs_queue.inspection_enhancement_dlq.arn,
          aws_sqs_queue.inspection_verification.arn,
          aws_sqs_queue.inspection_verification_dlq.arn,
          aws_sqs_queue.outbound_sync.arn,
          aws_sqs_queue.outbound_sync_dlq.arn,
          # Intelligence pipeline queues
          aws_sqs_queue.intelligence_orchestration.arn,
          aws_sqs_queue.intelligence_orchestration_dlq.arn,
          aws_sqs_queue.intelligence_description_analysis.arn,
          aws_sqs_queue.intelligence_description_analysis_dlq.arn,
          aws_sqs_queue.intelligence_option_analysis.arn,
          aws_sqs_queue.intelligence_option_analysis_dlq.arn,
          aws_sqs_queue.intelligence_notice_analysis.arn,
          aws_sqs_queue.intelligence_notice_analysis_dlq.arn,
          aws_sqs_queue.intelligence_aggregation.arn,
          aws_sqs_queue.intelligence_aggregation_dlq.arn,
          # Claim outbox queues
          aws_sqs_queue.cancel_outbox.arn,
          aws_sqs_queue.cancel_outbox_dlq.arn,
          aws_sqs_queue.refund_outbox.arn,
          aws_sqs_queue.refund_outbox_dlq.arn,
          aws_sqs_queue.exchange_outbox.arn,
          aws_sqs_queue.exchange_outbox_dlq.arn,
          aws_sqs_queue.qna_outbox.arn,
          aws_sqs_queue.qna_outbox_dlq.arn
        ]
      },
      {
        Sid    = "KMSDecrypt"
        Effect = "Allow"
        Action = [
          "kms:Decrypt",
          "kms:GenerateDataKey"
        ]
        Resource = [
          aws_kms_key.sqs.arn
        ]
      }
    ]
  })

  tags = local.common_tags
}

# ========================================
# SSM Parameters (for cross-stack reference)
# ========================================

# Scoring Queue
resource "aws_ssm_parameter" "inspection_scoring_queue_url" {
  name  = "/${var.project_name}/sqs/inspection-scoring-queue-url"
  type  = "String"
  value = aws_sqs_queue.inspection_scoring.url
  tags  = local.common_tags
}

resource "aws_ssm_parameter" "inspection_scoring_queue_arn" {
  name  = "/${var.project_name}/sqs/inspection-scoring-queue-arn"
  type  = "String"
  value = aws_sqs_queue.inspection_scoring.arn
  tags  = local.common_tags
}

resource "aws_ssm_parameter" "inspection_scoring_dlq_url" {
  name  = "/${var.project_name}/sqs/inspection-scoring-dlq-url"
  type  = "String"
  value = aws_sqs_queue.inspection_scoring_dlq.url
  tags  = local.common_tags
}

# Enhancement Queue
resource "aws_ssm_parameter" "inspection_enhancement_queue_url" {
  name  = "/${var.project_name}/sqs/inspection-enhancement-queue-url"
  type  = "String"
  value = aws_sqs_queue.inspection_enhancement.url
  tags  = local.common_tags
}

resource "aws_ssm_parameter" "inspection_enhancement_queue_arn" {
  name  = "/${var.project_name}/sqs/inspection-enhancement-queue-arn"
  type  = "String"
  value = aws_sqs_queue.inspection_enhancement.arn
  tags  = local.common_tags
}

resource "aws_ssm_parameter" "inspection_enhancement_dlq_url" {
  name  = "/${var.project_name}/sqs/inspection-enhancement-dlq-url"
  type  = "String"
  value = aws_sqs_queue.inspection_enhancement_dlq.url
  tags  = local.common_tags
}

# Verification Queue
resource "aws_ssm_parameter" "inspection_verification_queue_url" {
  name  = "/${var.project_name}/sqs/inspection-verification-queue-url"
  type  = "String"
  value = aws_sqs_queue.inspection_verification.url
  tags  = local.common_tags
}

resource "aws_ssm_parameter" "inspection_verification_queue_arn" {
  name  = "/${var.project_name}/sqs/inspection-verification-queue-arn"
  type  = "String"
  value = aws_sqs_queue.inspection_verification.arn
  tags  = local.common_tags
}

resource "aws_ssm_parameter" "inspection_verification_dlq_url" {
  name  = "/${var.project_name}/sqs/inspection-verification-dlq-url"
  type  = "String"
  value = aws_sqs_queue.inspection_verification_dlq.url
  tags  = local.common_tags
}

# OutboundSync Queue
resource "aws_ssm_parameter" "outbound_sync_queue_url" {
  name  = "/${var.project_name}/sqs/outbound-sync-queue-url"
  type  = "String"
  value = aws_sqs_queue.outbound_sync.url
  tags  = local.common_tags
}

resource "aws_ssm_parameter" "outbound_sync_queue_arn" {
  name  = "/${var.project_name}/sqs/outbound-sync-queue-arn"
  type  = "String"
  value = aws_sqs_queue.outbound_sync.arn
  tags  = local.common_tags
}

resource "aws_ssm_parameter" "outbound_sync_dlq_url" {
  name  = "/${var.project_name}/sqs/outbound-sync-dlq-url"
  type  = "String"
  value = aws_sqs_queue.outbound_sync_dlq.url
  tags  = local.common_tags
}

# ========================================
# SSM Parameters: Intelligence Pipeline
# ========================================

# Orchestration Queue
resource "aws_ssm_parameter" "intelligence_orchestration_queue_url" {
  name  = "/${var.project_name}/sqs/intelligence-orchestration-queue-url"
  type  = "String"
  value = aws_sqs_queue.intelligence_orchestration.url
  tags  = local.common_tags
}

# Description Analysis Queue
resource "aws_ssm_parameter" "intelligence_description_analysis_queue_url" {
  name  = "/${var.project_name}/sqs/intelligence-description-analysis-queue-url"
  type  = "String"
  value = aws_sqs_queue.intelligence_description_analysis.url
  tags  = local.common_tags
}

# Option Analysis Queue
resource "aws_ssm_parameter" "intelligence_option_analysis_queue_url" {
  name  = "/${var.project_name}/sqs/intelligence-option-analysis-queue-url"
  type  = "String"
  value = aws_sqs_queue.intelligence_option_analysis.url
  tags  = local.common_tags
}

# Notice Analysis Queue
resource "aws_ssm_parameter" "intelligence_notice_analysis_queue_url" {
  name  = "/${var.project_name}/sqs/intelligence-notice-analysis-queue-url"
  type  = "String"
  value = aws_sqs_queue.intelligence_notice_analysis.url
  tags  = local.common_tags
}

# Aggregation Queue
resource "aws_ssm_parameter" "intelligence_aggregation_queue_url" {
  name  = "/${var.project_name}/sqs/intelligence-aggregation-queue-url"
  type  = "String"
  value = aws_sqs_queue.intelligence_aggregation.url
  tags  = local.common_tags
}

# Claim Outbox Queues
resource "aws_ssm_parameter" "cancel_outbox_queue_url" {
  name  = "/${var.project_name}/sqs/cancel-outbox-queue-url"
  type  = "String"
  value = aws_sqs_queue.cancel_outbox.url
  tags  = local.common_tags
}

resource "aws_ssm_parameter" "refund_outbox_queue_url" {
  name  = "/${var.project_name}/sqs/refund-outbox-queue-url"
  type  = "String"
  value = aws_sqs_queue.refund_outbox.url
  tags  = local.common_tags
}

resource "aws_ssm_parameter" "exchange_outbox_queue_url" {
  name  = "/${var.project_name}/sqs/exchange-outbox-queue-url"
  type  = "String"
  value = aws_sqs_queue.exchange_outbox.url
  tags  = local.common_tags
}

resource "aws_ssm_parameter" "qna_outbox_queue_url" {
  name  = "/${var.project_name}/sqs/qna-outbox-queue-url"
  type  = "String"
  value = aws_sqs_queue.qna_outbox.url
  tags  = local.common_tags
}

# SQS Access Policy ARN
resource "aws_ssm_parameter" "sqs_access_policy_arn" {
  name  = "/${var.project_name}/sqs/access-policy-arn"
  type  = "String"
  value = aws_iam_policy.sqs_access.arn
  tags  = local.common_tags
}

# ========================================
# CloudWatch Alarms
# ========================================

# Scoring DLQ Alarm
resource "aws_cloudwatch_metric_alarm" "scoring_dlq_messages" {
  alarm_name          = "${var.project_name}-inspection-scoring-dlq-${var.environment}"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 1
  metric_name         = "ApproximateNumberOfMessagesVisible"
  namespace           = "AWS/SQS"
  period              = 300
  statistic           = "Sum"
  threshold           = 0
  alarm_description   = "Messages in inspection scoring DLQ"
  treat_missing_data  = "notBreaching"

  dimensions = {
    QueueName = aws_sqs_queue.inspection_scoring_dlq.name
  }

  tags = local.common_tags
}

# Enhancement DLQ Alarm
resource "aws_cloudwatch_metric_alarm" "enhancement_dlq_messages" {
  alarm_name          = "${var.project_name}-inspection-enhancement-dlq-${var.environment}"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 1
  metric_name         = "ApproximateNumberOfMessagesVisible"
  namespace           = "AWS/SQS"
  period              = 300
  statistic           = "Sum"
  threshold           = 0
  alarm_description   = "Messages in inspection enhancement DLQ"
  treat_missing_data  = "notBreaching"

  dimensions = {
    QueueName = aws_sqs_queue.inspection_enhancement_dlq.name
  }

  tags = local.common_tags
}

# Verification DLQ Alarm
resource "aws_cloudwatch_metric_alarm" "verification_dlq_messages" {
  alarm_name          = "${var.project_name}-inspection-verification-dlq-${var.environment}"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 1
  metric_name         = "ApproximateNumberOfMessagesVisible"
  namespace           = "AWS/SQS"
  period              = 300
  statistic           = "Sum"
  threshold           = 0
  alarm_description   = "Messages in inspection verification DLQ"
  treat_missing_data  = "notBreaching"

  dimensions = {
    QueueName = aws_sqs_queue.inspection_verification_dlq.name
  }

  tags = local.common_tags
}

# OutboundSync DLQ Alarm
resource "aws_cloudwatch_metric_alarm" "outbound_sync_dlq_messages" {
  alarm_name          = "${var.project_name}-outbound-sync-dlq-${var.environment}"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 1
  metric_name         = "ApproximateNumberOfMessagesVisible"
  namespace           = "AWS/SQS"
  period              = 300
  statistic           = "Sum"
  threshold           = 0
  alarm_description   = "Messages in outbound sync DLQ"
  treat_missing_data  = "notBreaching"

  dimensions = {
    QueueName = aws_sqs_queue.outbound_sync_dlq.name
  }

  tags = local.common_tags
}

# ========================================
# Outputs
# ========================================
output "outbound_sync_queue_url" {
  description = "OutboundSync queue URL"
  value       = aws_sqs_queue.outbound_sync.url
}

output "outbound_sync_queue_arn" {
  description = "OutboundSync queue ARN"
  value       = aws_sqs_queue.outbound_sync.arn
}

output "inspection_scoring_queue_url" {
  description = "Inspection scoring queue URL"
  value       = aws_sqs_queue.inspection_scoring.url
}

output "inspection_scoring_queue_arn" {
  description = "Inspection scoring queue ARN"
  value       = aws_sqs_queue.inspection_scoring.arn
}

output "inspection_enhancement_queue_url" {
  description = "Inspection enhancement queue URL"
  value       = aws_sqs_queue.inspection_enhancement.url
}

output "inspection_enhancement_queue_arn" {
  description = "Inspection enhancement queue ARN"
  value       = aws_sqs_queue.inspection_enhancement.arn
}

output "inspection_verification_queue_url" {
  description = "Inspection verification queue URL"
  value       = aws_sqs_queue.inspection_verification.url
}

output "inspection_verification_queue_arn" {
  description = "Inspection verification queue ARN"
  value       = aws_sqs_queue.inspection_verification.arn
}

output "sqs_access_policy_arn" {
  description = "IAM policy ARN for SQS access"
  value       = aws_iam_policy.sqs_access.arn
}

output "sqs_kms_key_arn" {
  description = "KMS key ARN for SQS encryption"
  value       = aws_kms_key.sqs.arn
}

# Intelligence Pipeline Outputs
output "intelligence_orchestration_queue_url" {
  description = "Intelligence orchestration queue URL"
  value       = aws_sqs_queue.intelligence_orchestration.url
}

output "intelligence_description_analysis_queue_url" {
  description = "Intelligence description analysis queue URL"
  value       = aws_sqs_queue.intelligence_description_analysis.url
}

output "intelligence_option_analysis_queue_url" {
  description = "Intelligence option analysis queue URL"
  value       = aws_sqs_queue.intelligence_option_analysis.url
}

output "intelligence_notice_analysis_queue_url" {
  description = "Intelligence notice analysis queue URL"
  value       = aws_sqs_queue.intelligence_notice_analysis.url
}

output "intelligence_aggregation_queue_url" {
  description = "Intelligence aggregation queue URL"
  value       = aws_sqs_queue.intelligence_aggregation.url
}

# Claim Outbox Outputs
output "cancel_outbox_queue_url" {
  description = "Cancel outbox queue URL"
  value       = aws_sqs_queue.cancel_outbox.url
}

output "refund_outbox_queue_url" {
  description = "Refund outbox queue URL"
  value       = aws_sqs_queue.refund_outbox.url
}

output "exchange_outbox_queue_url" {
  description = "Exchange outbox queue URL"
  value       = aws_sqs_queue.exchange_outbox.url
}

output "qna_outbox_queue_url" {
  description = "QnA outbox queue URL"
  value       = aws_sqs_queue.qna_outbox.url
}
