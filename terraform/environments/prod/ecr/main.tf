# ========================================
# ECR Repositories for MarketPlace (Prod)
# ========================================
# Container registries using Infrastructure module
# - web-api: REST API server
# - scheduler: Outbox pattern message publisher
# ========================================

# ========================================
# Common Tags (for governance)
# ========================================
locals {
  common_tags = {
    environment  = var.environment
    service_name = "${var.project_name}-ecr"
    team         = "platform-team"
    owner        = "platform@ryuqqq.com"
    cost_center  = "engineering"
    project      = var.project_name
    data_class   = "internal"
  }
}

# ========================================
# ECR Repository: web-api
# ========================================
module "ecr_web_api" {
  source = "git::https://github.com/ryu-qqq/Infrastructure.git//terraform/modules/ecr?ref=main"

  name                 = "${var.project_name}-web-api-${var.environment}"
  image_tag_mutability = "IMMUTABLE"
  scan_on_push         = true

  # KMS encryption for ECR images
  kms_key_arn  = "arn:aws:kms:ap-northeast-2:646886795421:key/086b1677-614f-46ba-863e-23c215fb5010"
  force_delete = true

  # Lifecycle Policy
  enable_lifecycle_policy    = true
  max_image_count            = 30
  lifecycle_tag_prefixes     = ["v", "prod", "latest"]
  untagged_image_expiry_days = 7

  # SSM Parameter for cross-stack reference
  create_ssm_parameter = true

  # Required Tags (governance compliance)
  environment  = local.common_tags.environment
  service_name = "${var.project_name}-web-api"
  team         = local.common_tags.team
  owner        = local.common_tags.owner
  cost_center  = local.common_tags.cost_center
  project      = local.common_tags.project
  data_class   = local.common_tags.data_class
}

# ========================================
# ECR Repository: scheduler
# ========================================
module "ecr_scheduler" {
  source = "git::https://github.com/ryu-qqq/Infrastructure.git//terraform/modules/ecr?ref=main"

  name                 = "${var.project_name}-scheduler-${var.environment}"
  image_tag_mutability = "IMMUTABLE"
  scan_on_push         = true

  # KMS encryption for ECR images
  kms_key_arn  = "arn:aws:kms:ap-northeast-2:646886795421:key/086b1677-614f-46ba-863e-23c215fb5010"
  force_delete = true

  # Lifecycle Policy
  enable_lifecycle_policy    = true
  max_image_count            = 30
  lifecycle_tag_prefixes     = ["v", "prod", "latest"]
  untagged_image_expiry_days = 7

  # SSM Parameter for cross-stack reference
  create_ssm_parameter = true

  # Required Tags (governance compliance)
  environment  = local.common_tags.environment
  service_name = "${var.project_name}-scheduler"
  team         = local.common_tags.team
  owner        = local.common_tags.owner
  cost_center  = local.common_tags.cost_center
  project      = local.common_tags.project
  data_class   = local.common_tags.data_class
}

# ========================================
# ECR Repository: worker
# NOTE: Temporarily disabled - Atlantis role lacks kms:CreateGrant permission
# Re-enable after IAM policy update for atlantis-ecs-task-prod
# ========================================
# module "ecr_worker" {
#   source = "git::https://github.com/ryu-qqq/Infrastructure.git//terraform/modules/ecr?ref=main"
#
#   name                 = "${var.project_name}-worker-${var.environment}"
#   image_tag_mutability = "IMMUTABLE"
#   scan_on_push         = true
#
#   # KMS encryption for ECR images
#   kms_key_arn  = "arn:aws:kms:ap-northeast-2:646886795421:key/086b1677-614f-46ba-863e-23c215fb5010"
#   force_delete = true
#
#   # Lifecycle Policy
#   enable_lifecycle_policy    = true
#   max_image_count            = 30
#   lifecycle_tag_prefixes     = ["v", "prod", "latest"]
#   untagged_image_expiry_days = 7
#
#   # SSM Parameter for cross-stack reference
#   create_ssm_parameter = true
#
#   # Required Tags (governance compliance)
#   environment  = local.common_tags.environment
#   service_name = "${var.project_name}-worker"
#   team         = local.common_tags.team
#   owner        = local.common_tags.owner
#   cost_center  = local.common_tags.cost_center
#   project      = local.common_tags.project
#   data_class   = local.common_tags.data_class
# }

# ========================================
# Outputs
# ========================================
output "web_api_repository_url" {
  description = "ECR repository URL for web-api"
  value       = module.ecr_web_api.repository_url
}

output "web_api_repository_arn" {
  description = "ECR repository ARN for web-api"
  value       = module.ecr_web_api.repository_arn
}

output "scheduler_repository_url" {
  description = "ECR repository URL for scheduler"
  value       = module.ecr_scheduler.repository_url
}

output "scheduler_repository_arn" {
  description = "ECR repository ARN for scheduler"
  value       = module.ecr_scheduler.repository_arn
}

# output "worker_repository_url" {
#   description = "ECR repository URL for worker"
#   value       = module.ecr_worker.repository_url
# }
#
# output "worker_repository_arn" {
#   description = "ECR repository ARN for worker"
#   value       = module.ecr_worker.repository_arn
# }
