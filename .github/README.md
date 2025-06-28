# 🔐 GitHub Secrets 설정 가이드

본 프로젝트에서는 다음과 같은 GitHub Secrets가 필요합니다:

| 이름 | 설명 |
|------|------|
| `SLACK_WEBHOOK_URL` | 슬랙 알림용 Webhook 주소 (PR 알림) |
| `AWS_ACCOUNT_ID` | AWS 계정 ID (OIDC 인증용) |
| `ENV_FILE` | 애플리케이션 환경변수 파일 내용 |

Secrets는 [Settings > Secrets and variables > Actions](../../settings/secrets/actions) 에서 추가할 수 있습니다.

---

# 🧪 GitHub Actions 요약

| 이름 | 트리거 | 설명 |
|------|--------|------|
| `dev-pr.yml` | `pull_request` to `dev` | 빌드 & 자코코 테스트 |
| `dev-push.yml` | `push` to `dev` | 빌드 & 자코코 테스트 |
| `main-pr.yml` | `pull_request` to `main` | 빌드, 자코코, SonarCloud, CodeQL |
| `main-push.yml` | `pull_request` to `main` (merged) | 버전 태깅, Docker 빌드, AWS 배포 |

---

# 🏷️ 버전 관리

프로젝트는 자동 버전 태깅을 지원합니다. PR에 다음 라벨을 추가하면 자동으로 버전이 업데이트됩니다:

- `major`: 메이저 버전 업데이트 (1.0.0 → 2.0.0)
- `minor`: 마이너 버전 업데이트 (1.0.0 → 1.1.0)  
- `patch`: 패치 버전 업데이트 (1.0.0 → 1.0.1)

---

# 🌍 환경별 배포 전략

## 현재 상황
- **2단계 환경**: dev(개발) → main(운영)
- **dev 브랜치**: 개발자 테스트용 (배포 없음)
- **main 브랜치**: 운영 환경 배포

## 현재 브랜치별 환경 매핑

| 브랜치 | 환경 | 목적           |
|--------|------|----------------|
| dev    | 개발 | 개발자 테스트   |
| main   | 운영 | 실제 서비스     |

## 현재 배포 파이프라인
```
dev 브랜치 → 개발 테스트 (배포 없음)
    ↓
main 브랜치 → 운영 환경 (자동 배포)
```

---

# 📁 디렉토리 구조

```
.github/
├── workflows/
│   ├── dev-pr.yml          # dev 브랜치 PR 검증
│   ├── dev-push.yml        # dev 브랜치 push 검증
│   ├── main-pr.yml         # main 브랜치 PR 검증
│   └── main-push.yml       # main 브랜치 배포 자동화
├── ISSUE_TEMPLATE/
│   ├── bugfix_task.md      # 🐛 버그 수정 작업
│   ├── chore_task.md       # 🛠️ 잡일 작업
│   ├── feature_task.md     # ✨ 기능 개발 작업
│   └── refactor_task.md    # 🛠 코드 리팩토링 작업
├── scripts/
│   └── init-labels.sh      # 라벨 초기화 스크립트
├── labels.yml              # 버전 라벨 정의
├── PULL_REQUEST_TEMPLATE.md
└── README.md
```

---

# 🚀 배포 프로세스

1. **PR 생성**: main 브랜치로 PR 생성
2. **검증**: main-pr.yml 워크플로우로 빌드, 테스트, 코드 분석
3. **머지**: PR 머지 시 main-push.yml 워크플로우 자동 실행
4. **버전 태깅**: PR 라벨에 따라 자동 버전 업데이트
5. **Docker 빌드**: GitHub Container Registry에 이미지 푸시
6. **AWS 배포**: OIDC를 통한 안전한 AWS 배포
