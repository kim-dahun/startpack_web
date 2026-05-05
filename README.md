# Web Application StartPack

본 프로젝트는 Gateway 기반 MSA 구조에서 ERP / Groupware / Trade 시스템을 통합 운영하기 위한 Web Application StartPack이다.

## 핵심 특징
- Gateway + Auth 기반 인증 구조
- 공통 User 서버를 통한 사용자/권한 관리
- 도메인별 서비스 분리 (ERP / Groupware / Trade)
- Vue3 + Composition API 기반 Frontend
- Backend/Frontend 표준 구조 및 문서 기반 개발

## 서버 구성

- gateway: 인증 검증 및 라우팅
- auth: JWT 발급 및 인증 처리
- user: 사용자 및 공통 서비스 관리
- erp: ERP 도메인
- groupware: 그룹웨어 도메인
- trade: 트레이딩 도메인

## Frontend 구성

- frontend-trade (기준 프로젝트)
- frontend-erp
- frontend-groupware
