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
    key            = "marketplace/ecs-legacy-api-stage/terraform.tfstate"
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

variable "legacy_api_cpu" {
  description = "CPU units for legacy-api task"
  type        = number
  default     = 512
}

variable "legacy_api_memory" {
  description = "Memory for legacy-api task"
  type        = number
  default     = 1024
}

variable "legacy_api_desired_count" {
  description = "Desired count for legacy-api service"
  type        = number
  default     = 1
}

variable "image_tag" {
  description = "Docker image tag to deploy. Format: legacy-api-{build-number}-{git-sha}"
  type        = string
  default     = "legacy-api-1-initial"

  validation {
    condition     = can(regex("^legacy-api-[0-9]+-[a-zA-Z0-9]+$", var.image_tag))
    error_message = "Image tag must follow format: legacy-api-{build-number}-{git-sha} (e.g., legacy-api-4-c0e0811)"
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
# RDS Configuration
# ========================================
data "aws_ssm_parameter" "rds_proxy_endpoint" {
  name = "/shared/rds/staging-proxy-endpoint"
}

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
# AuthHub Configuration
# ========================================
data "aws_ssm_parameter" "authhub_service_token" {
  name = "/authhub/stage/security/service-token-secret"
}

# ========================================
# FileFlow Configuration
# ========================================
data "aws_ssm_parameter" "fileflow_service_token" {
  name = "/shared/security/service-token-secret"
}

# ========================================
# Sentry Configuration
# ========================================
data "aws_ssm_parameter" "sentry_dsn" {
  name = "/${var.project_name}/sentry/dsn"
}

# ========================================
# Locals
# ========================================
locals {
  vpc_id          = data.aws_ssm_parameter.vpc_id.value
  private_subnets = split(",", data.aws_ssm_parameter.private_subnets.value)

  rds_credentials = jsondecode(data.aws_secretsmanager_secret_version.rds.secret_string)
  rds_host        = data.aws_ssm_parameter.rds_proxy_endpoint.value
  rds_port        = "3306"
  rds_dbname      = "market"
  rds_username    = local.rds_credentials.username

  redis_host = "stage-shared-redis.j9czrc.0001.apn2.cache.amazonaws.com"
  redis_port = 6379

  amp_workspace_arn    = data.aws_ssm_parameter.amp_workspace_arn.value
  amp_remote_write_url = data.aws_ssm_parameter.amp_remote_write_url.value

  sentry_dsn = data.aws_ssm_parameter.sentry_dsn.value
}
