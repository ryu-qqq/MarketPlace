# ========================================
# SQS Queues for MarketPlace Inspection Pipeline (Stage)
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
          aws_sqs_queue.inspection_scoring.arn,
          aws_sqs_queue.inspection_scoring_dlq.arn,
          aws_sqs_queue.inspection_enhancement.arn,
          aws_sqs_queue.inspection_enhancement_dlq.arn,
          aws_sqs_queue.inspection_verification.arn,
          aws_sqs_queue.inspection_verification_dlq.arn
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
