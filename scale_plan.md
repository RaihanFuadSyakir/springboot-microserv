# Microservices Scale-Up Plan with AWS Free Tier & Terraform

## 📋 Overview

This document outlines a budget-friendly scale-up plan for the microservices architecture using AWS free tier services and Infrastructure as Code (Terraform). The plan focuses on keeping costs under $5/month while demonstrating scaling concepts. The microservices are implemented using Java (Spring Boot) with Gradle as the build system.

## 🏗️ Architecture Overview

### Simplified Architecture for Experimentation
- 7 Microservices (containerized)
- Lightweight message queue (Amazon SQS instead of MSK)
- Shared database (RDS with minimal configuration)
- No separate Redis caching for cost reduction
- Single AZ deployment for experimentation

## 🚀 AWS Free Tier Services

### 1. Compute Services

#### AWS EC2 (Free Tier Eligible)
- **Instance Type**: t2.micro (1 vCPU, 1GB RAM)
- **Free Tier**: 750 hours/month for 12 months
- **Usage**: Run multiple containers with Docker Compose
- **Cost**: $0.00 (Free Tier) / $0.0116 per hour after free tier

#### AWS Lightsail (Alternative)
- **Instance Type**: $5/month plan (1 vCPU, 1GB RAM)
- **Free Tier**: 750 hours/month for first month
- **Usage**: Run containerized services
- **Cost**: $0.00 (First month) / $5.00/month

### 2. Database Services

#### Amazon RDS (Free Tier Eligible)
- **Instance Type**: db.t2.micro (1 vCPU, 1GB RAM)
- **Free Tier**: 750 hours/month + 20GB storage for 12 months
- **Usage**: Shared database for all services
- **Cost**: $0.00 (Free Tier) / ~$0.017 per hour after free tier

### 3. Messaging Services

#### Amazon SQS (Free Tier Eligible)
- **Free Tier**: 1 million requests/month
- **Usage**: Simple message queuing between services
- **Cost**: $0.00 (within limits) / $0.40 per million requests

#### Amazon SNS (Free Tier Eligible)
- **Free Tier**: 1 million publishes/month
- **Usage**: Notification service messaging
- **Cost**: $0.00 (within limits) / $0.50 per million publishes

### 4. Storage

#### Amazon S3 (Free Tier Eligible)
- **Free Tier**: 5GB storage + 20,000 GET requests + 2,000 PUT requests
- **Usage**: Static assets, logs, configuration files
- **Cost**: $0.00 (within limits) / $0.023 per GB

### 5. Container Registry

#### Amazon ECR (Free Tier Eligible)
- **Free Tier**: 500MB storage
- **Usage**: Store Docker images for services
- **Cost**: $0.00 (within limits) / $0.10 per GB

## 💰 Budget-Friendly Cost Estimation

### Assumptions
- Experimental environment (not production)
- Minimal usage within free tier limits
- Single instance deployment
- Shared database for all services
- Limited messaging requirements

### Monthly Cost Estimation (Within Free Tier)

#### Free Tier Services (No Cost)
| Service | Free Tier Benefit | Monthly Cost |
|---------|-------------------|--------------|
| EC2 (1 t2.micro) | 750 hours/month | $0.00 |
| RDS (1 db.t2.micro) | 750 hours/month + 20GB | $0.00 |
| SQS | 1M requests/month | $0.00 |
| S3 | 5GB + requests | $0.00 |
| ECR | 500MB storage | $0.00 |
| SNS | 1M publishes/month | $0.00 |

#### Post-Free Tier Minimal Costs
| Service | Configuration | Hourly Rate | Monthly Cost |
|---------|---------------|-------------|--------------|
| EC2 (1 t2.micro) | On-demand | $0.0116 | $8.47 |
| RDS (1 db.t2.micro) | Single-AZ | $0.017 | $12.41 |
| SQS | Within limits | N/A | $0.00 |
| S3 | Minimal usage | N/A | $0.00 |
| ECR | Minimal usage | N/A | $0.00 |

**Estimated Monthly Cost After Free Tier**: ~$3.00-5.00/month

## 🛠️ Terraform Implementation Plan

### Infrastructure as Code Structure
```
terraform/
│
├── main.tf              # Provider configuration
├── variables.tf         # Input variables
├── outputs.tf           # Output values
├── terraform.tfvars     # Variable values
│
├── ec2/
│   ├── main.tf          # EC2 instance configuration
│   ├── variables.tf
│   └── outputs.tf
│
├── rds/
│   ├── main.tf          # RDS instance configuration
│   ├── variables.tf
│   └── outputs.tf
│
├── sqs/
│   ├── main.tf          # SQS queues configuration
│   ├── variables.tf
│   └── outputs.tf
│
├── s3/
│   ├── main.tf          # S3 buckets configuration
│   ├── variables.tf
│   └── outputs.tf
│
└── ecr/
    ├── main.tf          # ECR repositories configuration
    ├── variables.tf
    └── outputs.tf
```

### Sample Terraform Configuration

#### main.tf (Provider Configuration)
```hcl
provider "aws" {
  region = var.aws_region
}

terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}
```

#### variables.tf (Global Variables)
```hcl
variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "us-east-1"
}

variable "project_name" {
  description = "Project name prefix"
  type        = string
  default     = "microservices-demo"
}

variable "environment" {
  description = "Environment (dev, staging, prod)"
  type        = string
  default     = "dev"
}
```

#### ec2/main.tf (EC2 Instance)
```hcl
resource "aws_instance" "microservices_server" {
  ami           = "ami-0c02fb55956c7d316" # Amazon Linux 2
  instance_type = "t2.micro"
  key_name      = var.key_pair_name

  vpc_security_group_ids = [aws_security_group.microservices_sg.id]
  
  user_data = <<-EOF
              #!/bin/bash
              yum update -y
              amazon-linux-extras install docker -y
              systemctl start docker
              systemctl enable docker
              usermod -a -G docker ec2-user
              EOF

  tags = {
    Name = "${var.project_name}-server-${var.environment}"
  }
}

resource "aws_security_group" "microservices_sg" {
  name        = "${var.project_name}-sg-${var.environment}"
  description = "Security group for microservices"

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 8080
    to_port     = 8086
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}
```

#### rds/main.tf (RDS Database)
```hcl
resource "aws_db_instance" "microservices_db" {
  allocated_storage    = 20
  engine               = "postgres"
  engine_version       = "14"
  instance_class       = "db.t2.micro"
  db_name              = "microservices"
  username             = var.db_username
  password             = var.db_password
  skip_final_snapshot  = true
  publicly_accessible  = true
  
  tags = {
    Name = "${var.project_name}-db-${var.environment}"
  }
}
```

#### sqs/main.tf (SQS Queues)
```hcl
resource "aws_sqs_queue" "user_events" {
  name                      = "${var.project_name}-user-events-${var.environment}"
  delay_seconds             = 0
  max_message_size          = 2048
  message_retention_seconds = 86400
  receive_wait_time_seconds = 10
}

resource "aws_sqs_queue" "order_events" {
  name                      = "${var.project_name}-order-events-${var.environment}"
  delay_seconds             = 0
  max_message_size          = 2048
  message_retention_seconds = 86400
  receive_wait_time_seconds = 10
}

resource "aws_sqs_queue" "payment_events" {
  name                      = "${var.project_name}-payment-events-${var.environment}"
  delay_seconds             = 0
  max_message_size          = 2048
  message_retention_seconds = 86400
  receive_wait_time_seconds = 10
}
```

## 🔧 Simplified Scaling Strategies

### Horizontal Scaling (Limited)
- Single EC2 instance for all services (using Docker Compose)
- Scale services by increasing container resources
- Use process-based concurrency within containers

### Vertical Scaling
- Increase container memory allocation
- Optimize JVM heap sizes for services
- Use lightweight frameworks (Micronaut, Quarkus)

### Database Optimization
- Connection pooling
- Query optimization
- Indexing strategies
- Shared database with schema separation

## 📊 Traffic Management

### Simple Load Distribution
- Single entry point (API Gateway alternative)
- Round-robin within Docker Compose
- Manual scaling of containers

### Health Checks
- Basic HTTP health endpoints
- Container restart policies
- Manual intervention for failures

## 🛡️ Security Implementation

### Basic Security Measures
- Security groups for network isolation
- IAM roles with minimal permissions
- Environment variables for secrets
- HTTPS termination at load balancer (when using ALB)

## 📈 Performance Optimization

### Resource Optimization
- JVM tuning for container environments
- Memory-efficient data structures
- Connection pooling for databases
- Asynchronous processing where possible

### Caching Strategies
- In-memory caching with simple maps
- HTTP caching headers
- Database query result caching

## 🧪 Testing Strategy for Scaling

### Load Testing (Budget-Friendly)
- Use open-source tools like k6 or Artillery
- Simulate 100-1000 concurrent users
- Monitor resource utilization
- Validate scaling triggers

### Chaos Engineering
- Manual service restarts
- Network partition simulations
- Resource exhaustion tests
- Failure recovery validation

## 🚀 Deployment Strategy

### Simple Deployment Process
1. Build Docker images locally
2. Push to ECR repository
3. Pull images on EC2 instance
4. Deploy with Docker Compose
5. Health check services
6. Manual rollback capability

### CI/CD Pipeline (Basic)
- GitHub Actions for build automation
- Docker image building
- ECR push
- SSH deployment to EC2
- Health verification

## 📊 Monitoring & Alerting (Minimal)

### Basic Monitoring
- Docker stats for container metrics
- Application logs to CloudWatch
- Health endpoint checks
- Manual alerting via SNS

### Log Management
- Centralized logging with CloudWatch Logs
- Log retention policies
- Basic log search capabilities
- Export logs to S3 for archival

## 📋 Implementation Roadmap

### Phase 1 (Week 1): Infrastructure Setup
- Set up Terraform project structure
- Configure AWS provider and variables
- Deploy basic EC2 instance with Docker
- Set up RDS database
- Configure SQS queues

### Phase 2 (Week 2): Application Deployment
- Containerize microservices
- Push images to ECR
- Deploy services on EC2 with Docker Compose
- Configure service communication
- Set up basic monitoring

### Phase 3 (Week 3): Optimization & Testing
- Optimize resource allocation
- Implement health checks
- Set up logging and monitoring
- Conduct load testing
- Validate scaling behavior

### Phase 4 (Week 4): Documentation & Validation
- Document deployment process
- Create runbooks for common operations
- Validate cost estimation
- Prepare for scaling experiments

## 🔄 Cost Optimization Recommendations

### Free Tier Maximization
1. **Stay within limits**: Monitor usage to remain within free tier
2. **Use spot instances**: Consider EC2 Spot for experimental workloads
3. **Right-size resources**: Allocate minimum necessary resources
4. **Scheduled shutdowns**: Automate shutdown during non-working hours

### Alternative Approaches
1. **Local development**: Use Docker Desktop for initial experiments
2. **AWS Cloud9**: Integrated development environment in the cloud
3. **GitHub Codespaces**: Cloud-based development environments
4. **Hybrid approach**: Mix of local and cloud resources

## 📅 Cost Calculation Using AWS Free Tier

### Free Tier Benefits Utilized:
1. **EC2**: 750 hours of t2.micro instances per month
2. **RDS**: 750 hours of db.t2.micro instances + 20GB storage
3. **S3**: 5GB storage + 20K GET + 2K PUT requests
4. **SQS**: 1 million requests per month
5. **ECR**: 500MB storage

### Estimated Monthly Costs:
- **During Free Tier**: $0.00 (first 12 months)
- **After Free Tier**: $3.00-5.00/month (minimal usage)

### Breakdown:
1. **EC2 (t2.micro)**: $8.47/month (but free with 750 hours)
2. **RDS (db.t2.micro)**: $12.41/month (but free with 750 hours)
3. **Other services**: Within free tier limits

## 📝 Conclusion

This budget-friendly scaling plan enables experimentation with microservices scaling concepts on AWS while keeping costs under $5/month. By leveraging AWS free tier benefits and focusing on essential services, you can:

1. Learn infrastructure as code with Terraform
2. Understand container orchestration basics
3. Experiment with service communication patterns
4. Practice scaling strategies in a controlled environment
5. Gain experience with AWS services without significant costs

The plan emphasizes learning and experimentation over production readiness, making it ideal for educational purposes and proof-of-concept projects. Once familiar with these concepts, you can graduate to more sophisticated architectures and services.