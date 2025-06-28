# AWS Infrastructure as Code (IaC) 템플릿

이 프로젝트는 AWS 인프라를 Terraform으로 관리하는 모듈화된 구조입니다.  
**도메인/호스팅 존 생성은 AWS 콘솔에서 직접 1회만 진행**하고,  
Terraform에서는 오직 레코드 및 인프라 리소스만 관리합니다.

---

## 도메인 및 Route53 호스팅 존 관리

### 1. 도메인 등록 및 호스팅 존 생성 (최초 1회만)
- 도메인 등록기관(가비아 등)에서 도메인을 구매합니다.
- AWS 콘솔(Route53)에서 **서브도메인 형태로 호스팅 존을 생성**합니다.
  - 예: `your-project.your-domain.com` 형태로 생성
  - 이는 `project.domain_name` 변수 조합으로 구성됩니다.
- 생성된 네임서버(NS) 정보를 도메인 등록기관에 등록합니다.

2. **Terraform에서는 호스팅 존을 data로 참조**
   - data "aws_route53_zone"을 사용해 이미 생성된 호스팅 존을 참조합니다.
   - Terraform에서는 오직 레코드(A, CNAME 등)와 인프라 리소스만 관리합니다.

---

## 디렉토리 구조

```
aws_infra/
├── modules/                    # 재사용 가능한 Terraform 모듈들
│   ├── acm/                   # SSL 인증서 관리
│   ├── cloudfront-s3/         # CloudFront CDN 및 S3 버킷
│   ├── ec2/                   # EC2 인스턴스
│   ├── rds/                   # RDS 데이터베이스
│   ├── route53/               # Route53 레코드 관리 (존 생성 X)
│   └── vpc/                   # VPC 및 네트워크 인프라
└── environments/
    ├── dev/
    │   └── create_free_tier_arch/      # 메인 아키텍처 (레코드 추가만)
    │       ├── main.tf
    │       ├── variables.tf
    │       ├── outputs.tf
    │       ├── terraform.tfvars
    │       ├── terraform.tfvars.example
    │       ├── generate_secrets.sh
    │       └── keygroup/
    │           └── .gitkeep
    └── prod/
        └── create_free_tier_arch/
```

---

## 사용법

### 1. 도메인 및 호스팅 존 등록 (최초 1회만)
- 도메인 등록기관(가비아 등)에서 도메인을 구매합니다.
- AWS 콘솔(Route53)에서 **서브도메인 형태로 호스팅 존을 생성**합니다.
  - 예: `your-project.your-domain.com` 형태로 생성
  - 이는 `project.domain_name` 변수 조합으로 구성됩니다.
- 생성된 네임서버(NS) 정보를 도메인 등록기관에 등록합니다.

### 2. 민감한 데이터(CloudFront 공개키) 생성

```bash
cd environments/dev/create_free_tier_arch
./generate_secrets.sh
```
- CloudFront 서명된 쿠키용 RSA 키 쌍 생성 (`keygroup/` 디렉토리에 저장)
- `terraform.tfvars` 파일의 public_key를 PEM 전체로 업데이트

### 3. 메인 아키텍처 배포 (Route53 레코드 추가 포함)

```bash
terraform init
terraform plan
terraform apply
```

---

## 환경 변수 예시 (terraform.tfvars.example)

```hcl
region            = "ap-northeast-2"
project           = "your-project"
domain_name       = "your-domain.com"
ec2_ami           = "ami-xxxxxxxxxxxxxxxxx"
ec2_instance_type = "t3.micro"
rds_instance_class = "db.t3.micro"
db_name           = "yourdbname"
db_username       = "your-db-user"
db_password       = "your-db-password"
public_key        = "" # generate_secrets.sh로 자동 입력
```

**생성되는 도메인 구조:**
- 호스팅 존: `your-project.your-domain.com`
- API 서버: `api.your-project.your-domain.com`
- CDN: `static.your-project.your-domain.com`

- `project`와 `domain_name` 부분은 각 서비스/시스템에 맞게 자유롭게 변경하세요.
- 실제 도메인 정보는 terraform.tfvars에서만 입력하고, 예시/README에는 노출하지 마세요.

---

## 주요 패턴 및 주의사항

1. **Route53 호스팅 존은 AWS 콘솔에서 직접 생성(최초 1회만)**
   - 서브도메인 형태로 생성: `project.domain_name`
2. **네임서버(NS) 정보는 도메인 등록기관에 직접 등록**
3. **Terraform에서는 data로 호스팅 존을 참조하고, 레코드만 관리**
4. **terraform.tfvars는 .gitignore에 포함되어 있으므로 민감 정보가 노출되지 않음**
5. **CloudFront public_key는 PEM 전체(헤더/푸터 포함)로 입력**
6. **실제 도메인 정보는 README, tfvars.example 등 공개 파일에 절대 노출하지 마세요**

---

## 리소스 삭제

```bash
terraform destroy
```
**주의:** 이 명령은 모든 리소스를 삭제합니다.

---

## 참고

- hosted_zone_name은 `project.domain_name` 형태로 자동 구성됩니다.
- 각 프로젝트/환경에 맞는 하위 도메인 구조를 `project` 변수로 관리합니다.
- 도메인/호스팅 존 생성은 IaC로 반복 관리하지 않고, 최초 1회만 콘솔에서 직접 진행합니다.
- 서브도메인 구조: `project.domain_name` → `api.project.domain_name`, `static.project.domain_name` 