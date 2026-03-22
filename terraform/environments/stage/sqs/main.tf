# ========================================
# SQS Queues for MarketPlace Inspection Pipeline (Stage)
# ========================================
# Legacy 3-stage pipeline (scoring, enhancement, verification)
# + New 5-stage Intelligence Pipeline:
#   1. Orchestration: ProductProfile 생성 + Analyzer 분배
#   2. Description Analysis: 상세설명 텍스트 AI 분석 (Sonnet)
#   3. Option Analysis: 캐노니컬 옵션 매핑 분석 (Haiku)
#   4. Notice Analysis: 고시정보 보강 분석 (Haiku)
#   5. Aggregation: 전체 분석 결과 집계 + 최종 판정
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

  # Legacy pipeline queues
  queue_names = {
    scoring      = "${var.environment}-${var.project_name}-inspection-scoring"
    enhancement  = "${var.environment}-${var.project_name}-inspection-enhancement"
    verification = "${var.environment}-${var.project_name}-inspection-verification"
  }

  # OutboundSync queue
  outbound_sync_queue_name = "${var.environment}-${var.project_name}-outbound-sync"

  # Shipment Outbox queue
  shipment_outbox_queue_name = "${var.environment}-${var.project_name}-shipment-outbox"

  # New Intelligence Pipeline queues
  intelligence_queue_names = {
    orchestration       = "${var.environment}-${var.project_name}-intelligence-orchestration"
    description_analysis = "${var.environment}-${var.project_name}-intelligence-description-analysis"
    option_analysis     = "${var.environment}-${var.project_name}-intelligence-option-analysis"
    notice_analysis     = "${var.environment}-${var.project_name}-intelligence-notice-analysis"
    aggregation         = "${var.environment}-${var.project_name}-intelligence-aggregation"
  }
}

data "aws_caller_identity" "current" {}

# ========================================
# KMS Key for SQS Encryption
# ========================================
resource "aws_kms_key" "sqs" {
  description             = "KMS key for MarketPlace SQS queue encryption (stage)"
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
    Lifecycle = "staging"
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
# Shipment Outbox Queue (DLQ)
# ========================================
resource "aws_sqs_queue" "shipment_outbox_dlq" {
  name                      = "${local.shipment_outbox_queue_name}-dlq"
  message_retention_seconds = 1209600 # 14 days
  kms_master_key_id         = aws_kms_key.sqs.arn

  tags = merge(local.common_tags, {
    Name    = "${local.shipment_outbox_queue_name}-dlq"
    Purpose = "Dead letter queue for shipment outbox"
  })
}

# ========================================
# Shipment Outbox Queue
# ========================================
resource "aws_sqs_queue" "shipment_outbox" {
  name                       = local.shipment_outbox_queue_name
  visibility_timeout_seconds = 300    # 5 minutes
  message_retention_seconds  = 345600 # 4 days
  receive_wait_time_seconds  = 20     # Long polling
  kms_master_key_id          = aws_kms_key.sqs.arn

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.shipment_outbox_dlq.arn
    maxReceiveCount     = 3
  })

  tags = merge(local.common_tags, {
    Name    = local.shipment_outbox_queue_name
    Purpose = "Shipment outbox message relay"
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
# IAM Policy: SQS Access for ECS Tasks
# ========================================
resource "aws_iam_policy" "sqs_access" {
  name        = "${var.project_name}-sqs-access-${var.environment}"
  description = "IAM policy for MarketPlace SQS queue access (stage)"

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
          # Legacy inspection queues
          aws_sqs_queue.inspection_scoring.arn,
          aws_sqs_queue.inspection_scoring_dlq.arn,
          aws_sqs_queue.inspection_enhancement.arn,
          aws_sqs_queue.inspection_enhancement_dlq.arn,
          aws_sqs_queue.inspection_verification.arn,
          aws_sqs_queue.inspection_verification_dlq.arn,
          # OutboundSync queue
          aws_sqs_queue.outbound_sync.arn,
          aws_sqs_queue.outbound_sync_dlq.arn,
          # Shipment Outbox queue
          aws_sqs_queue.shipment_outbox.arn,
          aws_sqs_queue.shipment_outbox_dlq.arn,
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
          aws_sqs_queue.intelligence_aggregation_dlq.arn
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

# SQS Access Policy ARN
resource "aws_ssm_parameter" "sqs_access_policy_arn" {
  name  = "/${var.project_name}/sqs/access-policy-arn"
  type  = "String"
  value = aws_iam_policy.sqs_access.arn
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

# Shipment Outbox Queue
resource "aws_ssm_parameter" "shipment_outbox_queue_url" {
  name  = "/${var.project_name}/sqs/shipment-outbox-queue-url"
  type  = "String"
  value = aws_sqs_queue.shipment_outbox.url
  tags  = local.common_tags
}

resource "aws_ssm_parameter" "shipment_outbox_queue_arn" {
  name  = "/${var.project_name}/sqs/shipment-outbox-queue-arn"
  type  = "String"
  value = aws_sqs_queue.shipment_outbox.arn
  tags  = local.common_tags
}

resource "aws_ssm_parameter" "shipment_outbox_dlq_url" {
  name  = "/${var.project_name}/sqs/shipment-outbox-dlq-url"
  type  = "String"
  value = aws_sqs_queue.shipment_outbox_dlq.url
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
  alarm_description   = "Messages in inspection scoring DLQ (stage)"
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
  alarm_description   = "Messages in inspection enhancement DLQ (stage)"
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
  alarm_description   = "Messages in inspection verification DLQ (stage)"
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
  alarm_description   = "Messages in outbound sync DLQ (stage)"
  treat_missing_data  = "notBreaching"

  dimensions = {
    QueueName = aws_sqs_queue.outbound_sync_dlq.name
  }

  tags = local.common_tags
}

# Shipment Outbox DLQ Alarm
resource "aws_cloudwatch_metric_alarm" "shipment_outbox_dlq_messages" {
  alarm_name          = "${var.project_name}-shipment-outbox-dlq-${var.environment}"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 1
  metric_name         = "ApproximateNumberOfMessagesVisible"
  namespace           = "AWS/SQS"
  period              = 300
  statistic           = "Sum"
  threshold           = 0
  alarm_description   = "Messages in shipment outbox DLQ (stage)"
  treat_missing_data  = "notBreaching"

  dimensions = {
    QueueName = aws_sqs_queue.shipment_outbox_dlq.name
  }

  tags = local.common_tags
}

# ========================================
# CloudWatch Alarms: Intelligence Pipeline DLQs
# ========================================

resource "aws_cloudwatch_metric_alarm" "intelligence_orchestration_dlq_messages" {
  alarm_name          = "${var.project_name}-intelligence-orchestration-dlq-${var.environment}"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 1
  metric_name         = "ApproximateNumberOfMessagesVisible"
  namespace           = "AWS/SQS"
  period              = 300
  statistic           = "Sum"
  threshold           = 0
  alarm_description   = "Messages in intelligence orchestration DLQ (stage)"
  treat_missing_data  = "notBreaching"

  dimensions = {
    QueueName = aws_sqs_queue.intelligence_orchestration_dlq.name
  }

  tags = local.common_tags
}

resource "aws_cloudwatch_metric_alarm" "intelligence_description_analysis_dlq_messages" {
  alarm_name          = "${var.project_name}-intelligence-desc-analysis-dlq-${var.environment}"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 1
  metric_name         = "ApproximateNumberOfMessagesVisible"
  namespace           = "AWS/SQS"
  period              = 300
  statistic           = "Sum"
  threshold           = 0
  alarm_description   = "Messages in intelligence description analysis DLQ (stage)"
  treat_missing_data  = "notBreaching"

  dimensions = {
    QueueName = aws_sqs_queue.intelligence_description_analysis_dlq.name
  }

  tags = local.common_tags
}

resource "aws_cloudwatch_metric_alarm" "intelligence_option_analysis_dlq_messages" {
  alarm_name          = "${var.project_name}-intelligence-option-analysis-dlq-${var.environment}"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 1
  metric_name         = "ApproximateNumberOfMessagesVisible"
  namespace           = "AWS/SQS"
  period              = 300
  statistic           = "Sum"
  threshold           = 0
  alarm_description   = "Messages in intelligence option analysis DLQ (stage)"
  treat_missing_data  = "notBreaching"

  dimensions = {
    QueueName = aws_sqs_queue.intelligence_option_analysis_dlq.name
  }

  tags = local.common_tags
}

resource "aws_cloudwatch_metric_alarm" "intelligence_notice_analysis_dlq_messages" {
  alarm_name          = "${var.project_name}-intelligence-notice-analysis-dlq-${var.environment}"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 1
  metric_name         = "ApproximateNumberOfMessagesVisible"
  namespace           = "AWS/SQS"
  period              = 300
  statistic           = "Sum"
  threshold           = 0
  alarm_description   = "Messages in intelligence notice analysis DLQ (stage)"
  treat_missing_data  = "notBreaching"

  dimensions = {
    QueueName = aws_sqs_queue.intelligence_notice_analysis_dlq.name
  }

  tags = local.common_tags
}

resource "aws_cloudwatch_metric_alarm" "intelligence_aggregation_dlq_messages" {
  alarm_name          = "${var.project_name}-intelligence-aggregation-dlq-${var.environment}"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 1
  metric_name         = "ApproximateNumberOfMessagesVisible"
  namespace           = "AWS/SQS"
  period              = 300
  statistic           = "Sum"
  threshold           = 0
  alarm_description   = "Messages in intelligence aggregation DLQ (stage)"
  treat_missing_data  = "notBreaching"

  dimensions = {
    QueueName = aws_sqs_queue.intelligence_aggregation_dlq.name
  }

  tags = local.common_tags
}

# ========================================
# Outputs
# ========================================
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

output "outbound_sync_queue_url" {
  description = "OutboundSync queue URL"
  value       = aws_sqs_queue.outbound_sync.url
}

output "outbound_sync_queue_arn" {
  description = "OutboundSync queue ARN"
  value       = aws_sqs_queue.outbound_sync.arn
}

output "shipment_outbox_queue_url" {
  description = "Shipment outbox queue URL"
  value       = aws_sqs_queue.shipment_outbox.url
}

output "shipment_outbox_queue_arn" {
  description = "Shipment outbox queue ARN"
  value       = aws_sqs_queue.shipment_outbox.arn
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
