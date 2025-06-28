#!/bin/bash

# 민감한 데이터 생성 스크립트
echo "🔐 민감한 데이터 생성 중..."

# keygroup 디렉토리 생성
KEYGROUP_DIR="keygroup"
mkdir -p $KEYGROUP_DIR

# CloudFront 서명된 쿠키용 키 쌍 생성
if [ ! -f "$KEYGROUP_DIR/cloudfront_key.pem" ]; then
    echo "📝 CloudFront 키 쌍 생성 중..."
    openssl genrsa -out $KEYGROUP_DIR/cloudfront_key.pem 2048
    openssl rsa -pubout -in $KEYGROUP_DIR/cloudfront_key.pem -out $KEYGROUP_DIR/cloudfront_public.pem
    echo "✅ CloudFront 키 쌍 생성 완료"
fi

# 공개키 PEM 전체를 읽어서 tfvars에 입력
PUBLIC_KEY=$(cat $KEYGROUP_DIR/cloudfront_public.pem)

# 기존 public_key 줄 삭제 (macOS 호환)
sed -i '' "/^public_key *=/,\$d" terraform.tfvars
# PEM 전체를 public_key에 입력
{
  echo "public_key = <<EOF"
  cat $KEYGROUP_DIR/cloudfront_public.pem
  echo "EOF"
} >> terraform.tfvars

echo "✅ 민감한 데이터 생성 완료!"
echo ""
echo "📋 생성된 정보:"
echo "   - CloudFront 공개키: $KEYGROUP_DIR/cloudfront_public.pem"
echo "   - CloudFront 개인키: $KEYGROUP_DIR/cloudfront_key.pem"
echo ""
echo "📁 키 파일 위치: $KEYGROUP_DIR/"
echo ""
echo "⚠️  주의사항:"
echo "   - $KEYGROUP_DIR/cloudfront_key.pem 파일은 안전하게 보관하세요"
echo "   - terraform.tfvars 파일의 public_key가 PEM 전체로 업데이트되었습니다" 