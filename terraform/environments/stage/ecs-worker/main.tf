# ========================================
# ECS Service: worker (Stage)
# ========================================
# SQS-based inspection pipeline worker
# No ALB, No Service Discovery (internal SQS consumer)
# Using Infrastructure modules
# ========================================

# ========================================
# Common Tags (for governance)
# ========================================
locals {
  common_tags = {
    environment  = var.environment
    service_name = "${var.project_name}-worker"
    team         = "platform-team"
    owner        = "platform@ryuqqq.com"
    cost_center  = "engineering"
    project      = var.project_name
    data_class   = "internal"
  }
}

# ========================================
# ECR Repository Reference
# ========================================
data "aws_ecr_repository" "worker" {
  name = "${var.project_name}-worker-stage"
}

# ========================================
# ECS Cluster Reference (from ecs-cluster-stage)
# ========================================
data "aws_ecs_cluster" "main" {
  cluster_name = "${var.project_name}-cluster-${var.environment}"
}

data "aws_caller_identity" "current" {}

# VPC data source for internal communication
data "aws_vpc" "main" {
  id = local.vpc_id
}

# ========================================
# KMS Key for CloudWatch Logs Encryption
# ========================================
resource "aws_kms_key" "logs" {
  description             = "KMS key for MarketPlace worker CloudWatch logs encryption (stage)"
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
        Sid    = "Allow CloudWatch Logs"
        Effect = "Allow"
        Principal = {
          Service = "logs.${var.aws_region}.amazonaws.com"
        }
        Action = [
          "kms:Encrypt*",
          "kms:Decrypt*",
          "kms:ReEncrypt*",
          "kms:GenerateDataKey*",
          "kms:Describe*"
        ]
        Resource = "*"
        Condition = {
          ArnLike = {
            "kms:EncryptionContext:aws:logs:arn" = "arn:aws:logs:${var.aws_region}:${data.aws_caller_identity.current.account_id}:log-group:/aws/ecs/${var.project_name}-worker-${var.environment}/*"
          }
        }
      }
    ]
  })

  tags = merge(local.common_tags, {
    Name      = "${var.project_name}-worker-logs-kms-${var.environment}"
    Lifecycle = "staging"
    ManagedBy = "terraform"
  })
}

resource "aws_kms_alias" "logs" {
  name          = "alias/${var.project_name}-worker-logs-${var.environment}"
  target_key_id = aws_kms_key.logs.key_id
}

# ========================================
# CloudWatch Log Group (using Infrastructure module)
# ========================================
module "worker_logs" {
  source = "git::https://github.com/ryu-qqq/Infrastructure.git//terraform/modules/cloudwatch-log-group?ref=main"

  name              = "/aws/ecs/${var.project_name}-worker-${var.environment}/application"
  retention_in_days = 14
  kms_key_id        = aws_kms_key.logs.arn

  environment  = local.common_tags.environment
  service_name = local.common_tags.service_name
  team         = local.common_tags.team
  owner        = local.common_tags.owner
  cost_center  = local.common_tags.cost_center
  project      = local.common_tags.project
  data_class   = local.common_tags.data_class
}

# ========================================
# Security Groups
# ========================================
module "ecs_security_group" {
  source = "git::https://github.com/ryu-qqq/Infrastructure.git//terraform/modules/security-group?ref=main"

  name        = "${var.project_name}-worker-sg-${var.environment}"
  description = "Security group for worker ECS tasks (stage)"
  vpc_id      = local.vpc_id

  type = "custom"

  custom_ingress_rules = [
    {
      from_port   = 8082
      to_port     = 8082
      protocol    = "tcp"
      cidr_block  = data.aws_vpc.main.cidr_block
      description = "VPC internal traffic only (Health check)"
    }
  ]

  environment  = local.common_tags.environment
  service_name = local.common_tags.service_name
  team         = local.common_tags.team
  owner        = local.common_tags.owner
  cost_center  = local.common_tags.cost_center
  project      = local.common_tags.project
  data_class   = local.common_tags.data_class
}

# ========================================
# RDS Access: ECS → RDS Security Group Ingress
# ========================================
data "aws_db_instance" "staging_rds" {
  db_instance_identifier = "staging-shared-mysql"
}

resource "aws_security_group_rule" "ecs_to_rds" {
  type                     = "ingress"
  from_port                = 3306
  to_port                  = 3306
  protocol                 = "tcp"
  security_group_id        = tolist(data.aws_db_instance.staging_rds.vpc_security_groups)[0]
  source_security_group_id = module.ecs_security_group.security_group_id
  description              = "Allow marketplace-worker-stage ECS to access staging RDS"
}

# ========================================
# IAM Roles (using Infrastructure module)
# ========================================

# ECS Task Execution Role
module "worker_task_execution_role" {
  source = "git::https://github.com/ryu-qqq/Infrastructure.git//terraform/modules/iam-role-policy?ref=main"

  role_name = "${var.project_name}-worker-execution-role-${var.environment}"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
      }
    ]
  })

  attach_aws_managed_policies = [
    "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
  ]

  enable_secrets_manager_policy = true
  secrets_manager_secret_arns   = [data.aws_secretsmanager_secret.rds.arn]

  custom_inline_policies = {
    ssm-access = {
      policy = jsonencode({
        Version = "2012-10-17"
        Statement = [
          {
            Effect = "Allow"
            Action = [
              "ssm:GetParameters",
              "ssm:GetParameter"
            ]
            Resource = [
              "arn:aws:ssm:${var.aws_region}:*:parameter/shared/*",
              "arn:aws:ssm:${var.aws_region}:*:parameter/${var.project_name}/*",
              "arn:aws:ssm:${var.aws_region}:*:parameter/authhub/*",
              "arn:aws:ssm:${var.aws_region}:*:parameter/naver-commerce/*"
            ]
          },
          {
            Effect = "Allow"
            Action = [
              "kms:Decrypt"
            ]
            Resource = [
              aws_kms_key.logs.arn
            ]
          }
        ]
      })
    }
  }

  environment  = local.common_tags.environment
  service_name = local.common_tags.service_name
  team         = local.common_tags.team
  owner        = local.common_tags.owner
  cost_center  = local.common_tags.cost_center
  project      = local.common_tags.project
  data_class   = local.common_tags.data_class
}

# ECS Task Role
module "worker_task_role" {
  source = "git::https://github.com/ryu-qqq/Infrastructure.git//terraform/modules/iam-role-policy?ref=main"

  role_name = "${var.project_name}-worker-task-role-${var.environment}"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
      }
    ]
  })

  attach_aws_managed_policies = [
    local.sqs_access_policy_arn
  ]

  custom_inline_policies = {
    adot-amp-access = {
      policy = jsonencode({
        Version = "2012-10-17"
        Statement = [
          {
            Sid    = "AMPRemoteWrite"
            Effect = "Allow"
            Action = [
              "aps:RemoteWrite"
            ]
            Resource = "arn:aws:aps:${var.aws_region}:*:workspace/*"
          },
          {
            Sid    = "XRayTracing"
            Effect = "Allow"
            Action = [
              "xray:PutTraceSegments",
              "xray:PutTelemetryRecords",
              "xray:GetSamplingRules",
              "xray:GetSamplingTargets",
              "xray:GetSamplingStatisticSummaries"
            ]
            Resource = "*"
          },
          {
            Sid    = "CloudWatchLogsAccess"
            Effect = "Allow"
            Action = [
              "logs:CreateLogGroup",
              "logs:CreateLogStream",
              "logs:PutLogEvents",
              "logs:DescribeLogStreams"
            ]
            Resource = [
              "arn:aws:logs:${var.aws_region}:${data.aws_caller_identity.current.account_id}:log-group:/ecs/marketplace/otel:*",
              "arn:aws:logs:${var.aws_region}:${data.aws_caller_identity.current.account_id}:log-group:/ecs/marketplace/otel-collector:*"
            ]
          },
          {
            Sid    = "CloudWatchMetricsAccess"
            Effect = "Allow"
            Action = [
              "cloudwatch:PutMetricData"
            ]
            Resource = "*"
            Condition = {
              StringEquals = {
                "cloudwatch:namespace" = "MarketPlace"
              }
            }
          },
          {
            Sid    = "S3ConfigAccess"
            Effect = "Allow"
            Action = [
              "s3:GetObject",
              "s3:ListBucket"
            ]
            Resource = [
              "arn:aws:s3:::prod-connectly",
              "arn:aws:s3:::prod-connectly/*"
            ]
          },
          {
            Sid    = "KMSDecryptForS3"
            Effect = "Allow"
            Action = [
              "kms:Decrypt"
            ]
            Resource = "arn:aws:kms:${var.aws_region}:${data.aws_caller_identity.current.account_id}:key/086b1677-614f-46ba-863e-23c215fb5010"
          }
        ]
      })
    }
  }

  environment  = local.common_tags.environment
  service_name = local.common_tags.service_name
  team         = local.common_tags.team
  owner        = local.common_tags.owner
  cost_center  = local.common_tags.cost_center
  project      = local.common_tags.project
  data_class   = local.common_tags.data_class
}

# ========================================
# ADOT Sidecar (using Infrastructure module)
# ========================================
module "adot_sidecar" {
  source = "git::https://github.com/ryu-qqq/Infrastructure.git//terraform/modules/adot-sidecar?ref=main"

  project_name              = var.project_name
  service_name              = "worker"
  aws_region                = var.aws_region
  amp_workspace_arn         = local.amp_workspace_arn
  amp_remote_write_endpoint = local.amp_remote_write_url
  log_group_name            = module.worker_logs.log_group_name
  app_port                  = 8082
  cluster_name              = data.aws_ecs_cluster.main.cluster_name
  environment               = var.environment
  config_bucket             = "prod-connectly"
  config_version            = "20251223"
}

# ========================================
# ECS Service (using Infrastructure module)
# ========================================
module "ecs_service" {
  source = "git::https://github.com/ryu-qqq/Infrastructure.git//terraform/modules/ecs-service?ref=main"

  # Service Configuration
  # No ALB, No Service Discovery - SQS-based internal worker
  name            = "${var.project_name}-worker-${var.environment}"
  cluster_id      = data.aws_ecs_cluster.main.arn
  container_name  = "worker"
  container_image = "${data.aws_ecr_repository.worker.repository_url}:${var.image_tag}"
  container_port  = 8082
  cpu             = var.worker_cpu
  memory          = var.worker_memory
  desired_count   = var.worker_desired_count

  # IAM Roles
  execution_role_arn = module.worker_task_execution_role.role_arn
  task_role_arn      = module.worker_task_role.role_arn

  # Network Configuration
  subnet_ids         = local.private_subnets
  security_group_ids = [module.ecs_security_group.security_group_id]
  assign_public_ip   = false

  # No Load Balancer - internal SQS worker

  # Container Environment Variables
  container_environment = [
    { name = "SPRING_PROFILES_ACTIVE", value = "stage" },
    { name = "DB_HOST", value = local.rds_host },
    { name = "DB_PORT", value = local.rds_port },
    { name = "DB_NAME", value = local.rds_dbname },
    { name = "DB_USERNAME", value = local.rds_username },
    { name = "REDIS_HOST", value = local.redis_host },
    { name = "REDIS_PORT", value = tostring(local.redis_port) },
    { name = "REDIS_PASSWORD", value = "" },
    { name = "REDIS_SSL_ENABLED", value = "false" },
    # SQS Queue URLs
    { name = "SQS_INSPECTION_SCORING_URL", value = local.sqs_scoring_queue_url },
    { name = "SQS_INSPECTION_ENHANCEMENT_URL", value = local.sqs_enhancement_queue_url },
    { name = "SQS_INSPECTION_VERIFICATION_URL", value = local.sqs_verification_queue_url },
    # Intelligence SQS Queue URLs
    { name = "SQS_INTELLIGENCE_ORCHESTRATION_URL", value = local.sqs_intelligence_orchestration_queue_url },
    { name = "SQS_INTELLIGENCE_DESCRIPTION_ANALYSIS_URL", value = local.sqs_intelligence_description_analysis_queue_url },
    { name = "SQS_INTELLIGENCE_OPTION_ANALYSIS_URL", value = local.sqs_intelligence_option_analysis_queue_url },
    { name = "SQS_INTELLIGENCE_NOTICE_ANALYSIS_URL", value = local.sqs_intelligence_notice_analysis_queue_url },
    { name = "SQS_INTELLIGENCE_AGGREGATION_URL", value = local.sqs_intelligence_aggregation_queue_url },
    # QnA Outbox SQS
    { name = "SQS_QNA_OUTBOX_URL", value = local.sqs_qna_outbox_queue_url },
    # FileFlow
    { name = "FILEFLOW_BASE_URL", value = "http://fileflow-web-api-stage.connectly.local:8080" },
    # Sentry
    { name = "SENTRY_DSN", value = local.sentry_dsn },
    # Setof Commerce
    { name = "SETOF_COMMERCE_BASE_URL", value = "http://setof-commerce-web-api-admin-stage.connectly.local:8081" },
    # Sellic Commerce
    { name = "SELLIC_COMMERCE_BASE_URL", value = "http://api.sellic.co.kr" },
    { name = "SELLIC_COMMERCE_CUSTOMER_ID", value = "1012" },
    # Naver Commerce
    { name = "NAVER_COMMERCE_CLIENT_ID", value = data.aws_ssm_parameter.naver_commerce_client_id.value },
    # Legacy DB
    { name = "LEGACY_DB_NAME", value = "luxurydb" },
    { name = "LEGACY_DB_USERNAME", value = "admin" }
  ]

  # Container Secrets
  container_secrets = [
    { name = "DB_PASSWORD", valueFrom = "${data.aws_secretsmanager_secret.rds.arn}:password::" },
    { name = "AUTHHUB_SERVICE_TOKEN", valueFrom = data.aws_ssm_parameter.authhub_service_token.arn },
    { name = "FILEFLOW_SERVICE_TOKEN", valueFrom = data.aws_ssm_parameter.fileflow_service_token.arn },
    { name = "SETOF_COMMERCE_SERVICE_TOKEN", valueFrom = data.aws_ssm_parameter.fileflow_service_token.arn },
    { name = "NAVER_COMMERCE_CLIENT_SECRET", valueFrom = data.aws_ssm_parameter.naver_commerce_client_secret.arn },
    { name = "OPENAI_API_KEY", valueFrom = data.aws_ssm_parameter.openai_api_key.arn },
    { name = "ANTHROPIC_API_KEY", valueFrom = data.aws_ssm_parameter.anthropic_api_key.arn },
    { name = "LEGACY_DB_PASSWORD", valueFrom = data.aws_ssm_parameter.legacy_db_password.arn },
    { name = "SELLIC_COMMERCE_API_KEY", valueFrom = data.aws_ssm_parameter.sellic_api_key.arn }
  ]

  # Health Check
  health_check_command      = ["CMD-SHELL", "wget --no-verbose --tries=1 --spider http://localhost:8082/actuator/health || exit 1"]
  health_check_interval     = 30
  health_check_timeout      = 5
  health_check_retries      = 3
  health_check_start_period = 120

  # Logging
  log_configuration = {
    log_driver = "awslogs"
    options = {
      "awslogs-group"         = module.worker_logs.log_group_name
      "awslogs-region"        = var.aws_region
      "awslogs-stream-prefix" = "worker"
    }
  }

  # ADOT Sidecar
  sidecars = [module.adot_sidecar.container_definition]

  # No Auto Scaling
  enable_autoscaling = false

  # Enable ECS Exec for debugging
  enable_execute_command = true

  # Deployment Configuration
  deployment_circuit_breaker_enable   = true
  deployment_circuit_breaker_rollback = true

  # No Service Discovery - internal SQS worker
  enable_service_discovery = false

  # Tagging
  environment  = local.common_tags.environment
  service_name = local.common_tags.service_name
  team         = local.common_tags.team
  owner        = local.common_tags.owner
  cost_center  = local.common_tags.cost_center
  project      = local.common_tags.project
  data_class   = local.common_tags.data_class
}

# ========================================
# Outputs
# ========================================
output "service_name" {
  description = "ECS Service name"
  value       = module.ecs_service.service_name
}

output "task_definition_arn" {
  description = "Task definition ARN"
  value       = module.ecs_service.task_definition_arn
}
