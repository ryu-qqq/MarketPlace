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
    key            = "marketplace/ecs-web-api-stage/terraform.tfstate"
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

variable "web_api_cpu" {
  description = "CPU units for web-api task"
  type        = number
  default     = 1024
}

variable "web_api_memory" {
  description = "Memory for web-api task"
  type        = number
  default     = 2048
}

variable "web_api_desired_count" {
  description = "Desired count for web-api service"
  type        = number
  default     = 1
}

variable "image_tag" {
  description = "Docker image tag to deploy. Format: web-api-{build-number}-{git-sha}"
  type        = string
  default     = "web-api-1-initial"

  validation {
    condition     = can(regex("^web-api-[0-9]+-[a-zA-Z0-9]+$", var.image_tag))
    error_message = "Image tag must follow format: web-api-{build-number}-{git-sha} (e.g., web-api-4-c0e0811)"
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
# Sentry Configuration
# ========================================
data "aws_ssm_parameter" "sentry_dsn" {
  name = "/${var.project_name}/sentry/dsn"
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
# Locals
# ========================================
locals {
  vpc_id          = data.aws_ssm_parameter.vpc_id.value
  private_subnets = split(",", data.aws_ssm_parameter.private_subnets.value)

  # RDS Configuration (MySQL - Shared Staging)
  # Using RDS Proxy for connection pooling and failover resilience
  rds_credentials = jsondecode(data.aws_secretsmanager_secret_version.rds.secret_string)
  rds_host        = data.aws_ssm_parameter.rds_proxy_endpoint.value
  rds_port        = "3306"
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
}
