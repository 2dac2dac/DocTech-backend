# 👋 ABOUT Doc'Tech

<div align="center">  
  
  ![로고](https://github.com/user-attachments/assets/d4a9290a-5879-4797-8f0b-a6db2c98819c)
  
<img width="417" alt="image" src="https://github.com/user-attachments/assets/85b3d706-b2bd-446b-badc-9c99ca76f584">

기존 비대면 진료에서는 과도한 개인 진료 서류 제출, 신속한 처방의 어려움, 불필요한 개인정보 노출 등의 문제가 있었습니다. <br>
이를 해결하기 위해 의료 마이데이터를 활용하여 환자가 자신의 건강 상태를 효율적으로 관리하고, <br>이를 바탕으로 비대면 진료 서비스에서 정확한 진단을 받을 수 있도록 지원합니다.

</div>
<br>

## 🎬 Doc'tech 시연영상
<div align="center">
    
| [😷 환자 시연영상 ](https://github.com/user-attachments/assets/7925b290-da76-494f-97a3-a65a3decab5f) |  [👨‍⚕️ 의사 시연영상 ](https://github.com/user-attachments/assets/7925b290-da76-494f-97a3-a65a3decab5f) |
|--|--|

</div>
<br>


## 🔍 Main Features
### 1️⃣ 의료 마이데이터 연동
마이데이터 API 서버를 통해 내 의료 마이데이터(건강검진/예방접종/진료이력/투약이력)를 한 곳에서 모아볼 수 있습니다.

### 2️⃣ 비대면 진료
언제 어디서나 쉽고 간편하게 예약하고 비대면 진료를 받을 수 있습니다.<br>
이때, 비식별화된 마이데이터가 사용자 동의 하에 의사와 공유되어 보다 정확한 진료가 이루어집니다.

### 3️⃣ 빠른 지도 검색
인근 병원 · 약국 · 응급실을 지도를 통해 빠르게 찾아볼 수 있습니다.

<br>

## 👥 Developers
<div align="center">
  
| [곽수찬](https://github.com/soooochan) | [김지윤](https://github.com/jyjyjy25) |
|--------|------- |
| <img height="200px" src= "https://github.com/user-attachments/assets/beac5c0a-c58c-4374-9a2c-35e29f3f5552"> | <img width="190px" src="https://github.com/user-attachments/assets/5bd214d5-ae98-4111-a9ac-4c8c4deb929b">  | 
| ✔️ 회원가입/로그인 <br> ✔️ 메일 인증 API <br> | ✔️ DB 설계 <br> ✔️ 기관(병원, 약국) 검색 API <br> ✔️ 비대면 진료 예약 API <br> ✔️ 마이데이터 연동 API <br> ✔️ 결제  API <br> ✔️ 보안키패드 구현 <br> ✔️ CI/CD 구축 <br>|
</div>

## ⚒️ Stacks

| Skill | Name |
|--------|------- |
| Language | `JAVA 17` |
| Framework | `Spring Boot 3.2.3` |
| Dependencies | `Spring Validation`, `Spring Data JPA`, `Spring Security + JWT` |
| Database | `Mysql 8.0`, `Redis 7.4`|
| DevOps | `Docker`, `docker-compose`, `Github Actions` |

<br>

## 🏗️ Architecture
![doc'tech-Architecture](https://github.com/user-attachments/assets/ca3e1e3a-bd3b-4f97-8b42-12c808d63535)

<br>

## 📍 ERD
![doc'tech-ERD](https://github.com/user-attachments/assets/d44bd482-4fb8-4ca8-b95d-13f8f2985ee2)

<br>

## 📜 Covention
### Code Convetion
| 항목                | 규칙                                                                                  |
|---------------------|---------------------------------------------------------------------------------------|
| `Class`         | **PascalCase**          |
| `Function`              | **camelCase**                                                                         |
| `Variable`                | **camelCase**                                                                         |
| `DB Table`           | **snake_case**                                                                        |

### Git Convention
### Prefix

| type         | what                  |
|--------------|-----------------------|
| `Feat`       | 기능 구현               |
| `Fix`        | 버그 수정               |
| `Remove`     | 파일, 코드, 기능 삭제     |
| `Refactor`   | 리팩토링                |
| `Chore`      | 패키지 구조 수정          |
| `Docs`       | 문서 수정               |
| `Infra`      | 인프라 관련 작업          |
| `Hotfix`     | 운영 서버 핫픽스 작업      |
| `Setting`    | 환경 설정               |

### Branch Naming
`<Prefix>/#<Issue_Number>-<Description>`

### Commit Message
`<Prefix>: <Description>`

<br>

## 🗂️ Package
```
├── 🗂️ .gitignore
├── 🗂️ .gitmodules
├── 🗂️ Dockerfile
├── 🗂️ docker-compose.yml
├── 🗂️ submodule-config
└── 🗂️ src.main.java.dac2dac
│   └── 🗂️ doctech
│       ├── 💽 Application
│       │   ├── 🗂️ agency
│       │   │   ├── 📂 controller
│       │   │   ├── 📂 dto
│       │   │   │   ├── request
│       │   │   │   └── response
│       │   │   ├── 📂 entity
│       │   │   ├── 📂 repository
│       │   │   ├── 📂 service
│       │   │   └── 📂 vo
│       │   ├── 🗂️ bootpay
│       │   ├── 🗂️ common
│       │   │   ├── 📂 component
│       │   │   ├── 📂 config
│       │   │   ├── 📂 constant
│       │   │   │   ├── ErrorCode
│       │   │   │   └── SuccessCode
│       │   │   ├── 📂 entity
│       │   │   ├── 📂 error
│       │   │   │   ├── 📂 exception
│       │   │   │   └── RestExceptionHandler
│       │   │   ├── 📂 response
│       │   │   └── 📂 utils
│       │   ├── 🗂️ doctor
│       │   ├── 🗂️ health_list
│       │   ├── 🗂️ keypad
│       │   ├── 🗂️ mydata
│       │   ├── 🗂️ noncontact_diag
│       │   ├── 🗂️ user
│       │   └── 🗂️ external
```
<br><br>
