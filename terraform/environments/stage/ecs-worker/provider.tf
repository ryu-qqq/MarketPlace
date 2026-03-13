# ========================================
# Terraform Provider Configuration (Stage)
# ========================================

terraform {
  required_version = ">= 1.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }

  backend "s3" {
    bucket         = "prod-connectly"
    key            = "marketplace/ecs-worker-stage/terraform.tfstate"
    region         = "ap-northeast-2"
    dynamodb_table = "prod-connectly-tf-lock"
    encrypt        = true
    kms_key_id     = "arn:aws:kms:ap-northeast-2:646886795421:key/086b1677-614f-46ba-863e-23c215fb5010"
  }
}

provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      Project     = var.project_name
      Environment = var.environment
      ManagedBy   = "terraform"
    }
  }
}

# ========================================
# Common Variables
# ========================================
variable "project_name" {
  description = "Project name"
  type        = string
  default     = "marketplace"
}

variable "environment" {
  description = "Environment name"
  type        = string
  default     = "stage"
}

variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "ap-northeast-2"
}

variable "worker_cpu" {
  description = "CPU units for worker task"
  type        = number
  default     = 512
}

variable "worker_memory" {
  description = "Memory for worker task"
  type        = number
  default     = 1024
}

variable "worker_desired_count" {
  description = "Desired count for worker service"
  type        = number
  default     = 1
}

variable "image_tag" {
  description = "Docker image tag to deploy. Format: worker-{build-number}-{git-sha}"
  type        = string
  default     = "worker-1-initial"

  validation {
    condition     = can(regex("^worker-[0-9]+-[a-zA-Z0-9]+$", var.image_tag))
    error_message = "Image tag must follow format: worker-{build-number}-{git-sha} (e.g., worker-4-c0e0811)"
  }
}

# ========================================
# Shared Resource References (SSM)
# ========================================
data "aws_ssm_parameter" "vpc_id" {
  name = "/shared/network/vpc-id"
}

data "aws_ssm_parameter" "private_subnets" {
  name = "/shared/network/private-subnets"
}

# ========================================
# RDS Configuration (MySQL - Shared Staging)
# ========================================
data "aws_secretsmanager_secret" "rds" {
  name = "setof-commerce/rds/staging-credentials"
}

data "aws_secretsmanager_secret_version" "rds" {
  secret_id = data.aws_secretsmanager_secret.rds.id
}

# ========================================
# Monitoring Configuration (AMP)
# ========================================
data "aws_ssm_parameter" "amp_workspace_arn" {
  name = "/shared/monitoring/amp-workspace-arn"
}

data "aws_ssm_parameter" "amp_remote_write_url" {
  name = "/shared/monitoring/amp-remote-write-url"
}

# ========================================
# Sentry Configuration
# ========================================
data "aws_ssm_parameter" "sentry_dsn" {
  name = "/${var.project_name}/sentry/dsn"
}

# ========================================
# SQS Queue References (from SQS module)
# ========================================
data "aws_ssm_parameter" "sqs_scoring_queue_url" {
  name = "/${var.project_name}/sqs/inspection-scoring-queue-url"
}

data "aws_ssm_parameter" "sqs_enhancement_queue_url" {
  name = "/${var.project_name}/sqs/inspection-enhancement-queue-url"
}

data "aws_ssm_parameter" "sqs_verification_queue_url" {
  name = "/${var.project_name}/sqs/inspection-verification-queue-url"
}

data "aws_ssm_parameter" "sqs_access_policy_arn" {
  name = "/${var.project_name}/sqs/access-policy-arn"
}

# ========================================
# Intelligence SQS Queue References (from SQS module)
# ========================================
data "aws_ssm_parameter" "sqs_intelligence_orchestration_queue_url" {
  name = "/${var.project_name}/sqs/intelligence-orchestration-queue-url"
}

data "aws_ssm_parameter" "sqs_intelligence_description_analysis_queue_url" {
  name = "/${var.project_name}/sqs/intelligence-description-analysis-queue-url"
}

data "aws_ssm_parameter" "sqs_intelligence_option_analysis_queue_url" {
  name = "/${var.project_name}/sqs/intelligence-option-analysis-queue-url"
}

data "aws_ssm_parameter" "sqs_intelligence_notice_analysis_queue_url" {
  name = "/${var.project_name}/sqs/intelligence-notice-analysis-queue-url"
}

data "aws_ssm_parameter" "sqs_intelligence_aggregation_queue_url" {
  name = "/${var.project_name}/sqs/intelligence-aggregation-queue-url"
}

# ========================================
# AuthHub Configuration
# ========================================
data "aws_ssm_parameter" "authhub_service_token" {
  name = "/authhub/stage/security/service-token-secret"
}

# ========================================
# OpenAI Configuration
# ========================================
data "aws_ssm_parameter" "openai_api_key" {
  name = "/${var.project_name}/openai/api-key"
}

# ========================================
# Anthropic Configuration
# ========================================
data "aws_ssm_parameter" "anthropic_api_key" {
  name = "/${var.project_name}/anthropic/api-key"
}

# ========================================
# FileFlow Configuration (Shared Service Token)
# ========================================
data "aws_ssm_parameter" "fileflow_service_token" {
  name = "/shared/security/service-token-secret"
}

# ========================================
# Naver Commerce Configuration
# ========================================
data "aws_ssm_parameter" "naver_commerce_client_id" {
  name = "/naver-commerce/stage/client-id"
}

data "aws_ssm_parameter" "naver_commerce_client_secret" {
  name = "/naver-commerce/stage/client-secret"
}

# ========================================
# Legacy DB Configuration
# ========================================
data "aws_ssm_parameter" "legacy_db_password" {
  name = "/${var.project_name}/stage/legacy-db-password"
}

# ========================================
# Locals
# ========================================
locals {
  vpc_id          = data.aws_ssm_parameter.vpc_id.value
  private_subnets = split(",", data.aws_ssm_parameter.private_subnets.value)

  # RDS Configuration (MySQL - Shared Staging)
  rds_credentials = jsondecode(data.aws_secretsmanager_secret_version.rds.secret_string)
  rds_host        = local.rds_credentials.host
  rds_port        = tostring(local.rds_credentials.port)
  rds_dbname      = "market"
  rds_username    = local.rds_credentials.username

  # Redis Configuration (Shared Stage Redis - No Auth, No TLS)
  redis_host = "stage-shared-redis.j9czrc.0001.apn2.cache.amazonaws.com"
  redis_port = 6379

  # AMP Configuration
  amp_workspace_arn    = data.aws_ssm_parameter.amp_workspace_arn.value
  amp_remote_write_url = data.aws_ssm_parameter.amp_remote_write_url.value

  # Sentry Configuration
  sentry_dsn = data.aws_ssm_parameter.sentry_dsn.value

  # SQS Queue URLs
  sqs_scoring_queue_url      = data.aws_ssm_parameter.sqs_scoring_queue_url.value
  sqs_enhancement_queue_url  = data.aws_ssm_parameter.sqs_enhancement_queue_url.value
  sqs_verification_queue_url = data.aws_ssm_parameter.sqs_verification_queue_url.value
  sqs_access_policy_arn      = nonsensitive(data.aws_ssm_parameter.sqs_access_policy_arn.value)

  # Intelligence SQS Queue URLs
  sqs_intelligence_orchestration_queue_url          = data.aws_ssm_parameter.sqs_intelligence_orchestration_queue_url.value
  sqs_intelligence_description_analysis_queue_url   = data.aws_ssm_parameter.sqs_intelligence_description_analysis_queue_url.value
  sqs_intelligence_option_analysis_queue_url        = data.aws_ssm_parameter.sqs_intelligence_option_analysis_queue_url.value
  sqs_intelligence_notice_analysis_queue_url        = data.aws_ssm_parameter.sqs_intelligence_notice_analysis_queue_url.value
  sqs_intelligence_aggregation_queue_url            = data.aws_ssm_parameter.sqs_intelligence_aggregation_queue_url.value
}
