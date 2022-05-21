# JongP2

#### 1. 안드로이드 어플의 기능

- 아두이노 보드와 통신
  - 아두이노의 블루투스 모듈과 통신
  - 아두이노에 시작/중지 신호를 송신
  - 아두이노에게 받은 위험도 레벨에 따라 처리
    - 1단계 : 진동
    - 2단계 : 119 소리 재생
    - 3단계 : 지정 번호로 전화 발신 
- 수면 패턴을 알아볼 수 있는 로그 기록 및 로그 확인
  - 일간, 주간, 월간으로 단계별 무호흡 횟수를 기록하여 확인

  
#### 2. 아두이노 프로그램의 기능

- 기류 센서
  - 바람의 양을 입력 받아 바람의 속도를 계산
  - 바람의 속도가 기준치 밑으로 떨어지면 시간 측정 (무호흡 상태)
  - 일정 시간이 지나면 위험도 레벨 송신
- 장력 센서
  - 고무줄의 늘어난 정도에 따라 저항 측정
  - 저항의 변화량이 정상 호흡 때보다 줄어들 경우 시간 측정 (무호흡 상태)
  - 일정 시간이 지나면 위험도 레벨 송신
- 블루투스 모듈
  - 어플과 블루투스 통신
  - 위험도 레벨을 어플에 송신
  - 시작/중지 신호를 수신

#### 3. 프로그램 사용 방법

- 아두이노 보드를 USB 포트에 연결한다.
- 마스크와 복대를 몸에 맞게 착용한다.
- 아두이노 보드의 리셋 버튼을 눌러 초기 상태로 돌려준다.
- 어플을 실행하여 '블루투스 연결' 버튼을 눌러 아두이노 블루투스 모듈과 연결한다. (모듈의 비밀번호는 '1234'로 설정되어있음)
- 연결이 완료되면 가운데의 시작 아이콘을 눌러 아두이노 보드를 동작시킨다.
- 호흡을 정상적으로 하다가 무호흡 상태로 일정시간 유지하며 알림을 확인한다.
- 어플의 중지 아이콘을 눌러 아두이노를 대기 상태로 전환하고 측정을 종료한다.
