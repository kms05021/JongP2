#include <SoftwareSerial.h>
// 기류 센서 매크로
#define Alert 5000
#define Warning 10000
#define Emergency 15000
// 장력 고무줄 센서 매크로
#define BUFFERSIZE 50
#define TOLERANCE 0.988
#define ALERTTIME 5000
#define WARNINGTIME 10000
#define EMERGENCYTIME 15000

// 블루투스 모듈 전역 변수
const int pinTx = 7;  // 블루투스 TX 연결 핀 번호
const int pinRx = 6;  // 블루투스 RX 연결 핀 번호
SoftwareSerial   bluetooth( pinTx, pinRx );

// 기류 센서 전역 변수
const float zeroWindAdjustment =  .2; // 영점 보정 변수
int TMP_Therm_ADunits;                // 온도 측정 값
float RV_Wind_ADunits;                // RV 출력 값 (측정 값)
float RV_Wind_Volts;                  // 전압 단위 RV 출력 값
int TempCtimes100;                    // 섭씨 온도 변수
float zeroWind_ADunits;               // 영점 측정 값 (초기 값)
float zeroWind_volts;                 // 전압 단위 영점 측정 값
float WindSpeed_MPH;                  // 바람 속도

int inBoundW = 1;
int isCountingW = 0;
int validVibe = 0;
int validAlarm = 0;
int validCall = 0;
int noWind = 0;

String myString = "";

// 장력 고무줄 센서 전역 변수
int rubberValues[BUFFERSIZE] = {0,};
int lastIndex = 0;
int isCounting = 0;
unsigned long launchTime = 0;
unsigned long currTime = 0;
int validBandV = 0;
int validBandA = 0;
int validBandC = 0;
int lowMovement = 0;

// 델타 타임 변수
unsigned long startTime=0;
unsigned long curTime=0;

// 위험도 레벨 플래그 (루프 당 1회 출력 체크)
int flag1 = 0;
int flag2 = 0;
int flag3 = 0;

// 초기화 콜백
void resetFunc()
{
  asm volatile ("jmp 0");   // 처음 줄로 점프
}
 
void  setup()
{
  bluetooth.begin(9600);          // 블루투스 통신 초기화 (속도= 9600 bps)
  Serial.begin(9600);
  while(1)
  {
    while(bluetooth.available())  //mySerial에 전송된 값이 있으면
    {
      char myChar = (char)bluetooth.read();  //mySerial int 값을 char 형식으로 변환
      myString+=myChar;                      //수신되는 문자를 myString에 모두 붙임 (1바이트씩 전송되는 것을 연결)
      delay(10);                             //수신 문자열 끊김 방지
    }
    if(!myString.equals(""))                    //myString 값이 있다면
    {
      Serial.println("input value: "+myString); //시리얼모니터에 myString값 출력
      if(myString.equals("START"))              //START 버튼이 눌렸을 경우
      {
        myString="";                            //myString 변수값 초기화
        break;
      }
    }
    // 장력 고무줄 센서 세팅 (초기 BUFFERSIZE 값 만큼 저장)
    for(int i = 0; i < BUFFERSIZE; i++)
    {
    int val = analogRead(A5);
    rubberValues[i] = val;
    }

  }
}
 
 
void  loop()
{
  
  // 블루투스 수신 
  if ( bluetooth.available() ) 
  {
    Serial.print((char)bluetooth.read());
  }
  else
  {
    delay( 10 );
  }
  // 블루투스 송신
  if (Serial.available()) { 
    //시리얼 모니터에서 입력된 값을 송신
    char toSend = (char)Serial.read();
    bluetooth.print(toSend);
  }

  // 기류 센서 측정 및 연산
  // 센서로 부터 Raw Data 입력 받기
  TMP_Therm_ADunits = analogRead(A0);
  RV_Wind_ADunits = analogRead(A1);
  // 측정값 전압 단위로 변경
  RV_Wind_Volts = (RV_Wind_ADunits *  0.0048828125);
  // 온도 측정 및 초기 바람의 양 계산 (영점용)
  TempCtimes100 = (0.005 *((float)TMP_Therm_ADunits * (float)TMP_Therm_ADunits)) - (16.862 * (float)TMP_Therm_ADunits) + 9075.4;  
  zeroWind_ADunits = -0.0006*((float)TMP_Therm_ADunits * (float)TMP_Therm_ADunits) + 1.0727 * (float)TMP_Therm_ADunits + 47.172;
  zeroWind_volts = (zeroWind_ADunits * 0.0048828125) - zeroWindAdjustment;
  // 속도 값 계산
  WindSpeed_MPH =  pow(((RV_Wind_Volts - zeroWind_volts) /.2300) , 2.7265);

  // STOP Check
    while(bluetooth.available())  //mySerial에 전송된 값이 있으면
    {
      char myChar = (char)bluetooth.read();  //mySerial int 값을 char 형식으로 변환
      myString+=myChar;   //수신되는 문자를 myString에 모두 붙임 (1바이트씩 전송되는 것을 연결)
      delay(10);           //수신 문자열 끊김 방지
    }
    if(!myString.equals(""))  //myString 값이 있다면
    {
      Serial.println("input value: "+myString); //시리얼모니터에 myString값 출력
      if(myString.equals("STOP"))
      {
        myString="";  //myString 변수값 초기화
        resetFunc();
      }
    }
  
  // 기류 센서 검사
  if(WindSpeed_MPH < 2 || isnan(WindSpeed_MPH)) // 범위 밖의 값이 측정되거나 기준 값 미만이면
  {
    inBoundW = 0;                               // 바운더리 플래그를 0으로 세팅
  }
  else
  {
    inBoundW = 1;
  }

  if(!inBoundW)                                 // 바운더리 플래그가 0인 경우 (호흡이 기준 속도 미만일 경우)
    {
      if(isCountingW)                           // 카운팅 플래그가 1인 경우 (무호흡 상태가 유지되는 경우)
      {
        curTime = millis();                     // 현재 시간을 측정하여 시간 차 비교
        if(curTime - startTime >= Alert && curTime - startTime < Warning)
        {
          if(validVibe==0)                      // 진동 플래그 세팅
          {
            //bluetooth.println("LEVEL1");
            Serial.println("Vibration!!");
            validVibe=1;
            noWind = 1;
            delay(10);
          }

        }
        else if(curTime - startTime >= Warning && curTime - startTime < Emergency)
        {
          if(validAlarm==0)                      // 알람 플래그 세팅
          {
            //bluetooth.println("LEVEL2");
            Serial.println("Alarm!!");
            validAlarm=1;
            noWind = 2;
            delay(10);
          }
        }
        else if(curTime - startTime >= Emergency)
        {
          if(validCall==0)                      // 전화 플래그 세팅
          {
            //bluetooth.println("LEVEL3");
            Serial.println("Call!!");
            validCall=1;
            noWind = 3;
            delay(10);
          }
        }
      }
      else                    // 최초 무호흡 상태 돌입 시
      {
        isCountingW = 1;      // 카운팅 플래그 1로 세팅
        startTime = millis(); // 카운트 시작
      }
    }
    else                      // 바운더리 플래그가 0이 아닌 경우 (정상적인 호흡 상태일 경우)
    {
      if(isCountingW)         // 카운팅 플래그가 세팅 되어 있다면 모든 플래그 0으로 초기화
      {
        isCountingW = 0;
        startTime = curTime = 0;
        validVibe = 0;
        validAlarm = 0;
        validCall = 0;
        noWind = 0;
      }
    }

    // Rubber band Check
    int rubberVal = analogRead(A5);
   //Serial.print(" current: ");
   //Serial.println(rubberVal);
  
   rubberValues[lastIndex] = rubberVal;
   lastIndex = (lastIndex+1) % BUFFERSIZE;
   int maxVal = -1;
   int minVal = 9999;
   for(int i = 0; i < BUFFERSIZE; i++)
   { 
    if(rubberValues[i] > maxVal)
      maxVal = rubberValues[i];
    else if (rubberValues[i] < minVal)
      minVal = rubberValues[i];
   }
    int inBound = 0;
    if((float)minVal / maxVal > TOLERANCE)
      inBound = 1;
    else
      inBound = 0;
  
    if(isCounting)
    {
      if(inBound)
      {
        currTime = millis();
        if(currTime - launchTime >= ALERTTIME && currTime - launchTime < WARNINGTIME)
        {
          if(validBandV==0)
          {
            //bluetooth.println("LEVEL1");
            Serial.println("Band Vibration!!");
            validBandV=1;
            lowMovement = 1;
            delay(10);
          }

        }
        else if(currTime - launchTime >= WARNINGTIME && currTime - launchTime < EMERGENCYTIME)
        {
          if(validBandA==0)
          {
            //bluetooth.println("LEVEL2");
            Serial.println("Band Alarm!!");
            validBandA=1;
            lowMovement = 2;
            delay(10);
          }
        }
        else if(currTime - launchTime >= EMERGENCYTIME)
        {
          if(validBandC==0)
          {
            //bluetooth.println("LEVEL3");
            Serial.println("Band Call!!");
            validBandC=1;
            lowMovement=3;
            delay(10);
          }
        }
      }
      else
      {
          isCounting = 0;
          currTime = 0;
          validBandV = 0;
          validBandA = 0;
          validBandC = 0;
          lowMovement = 0;
      }
    }
    else
    {
      if(inBound)
    {
      isCounting = 1;
      launchTime = millis();  
    }
  }

  // 기류 센서와 장력 고무줄 센서 값 확인 후 레벨 전송
  if((noWind == 1 && lowMovement >= 1)||(noWind >= 1 && lowMovement == 1))
  {// 심각도 레벨 1인 경우
    if(flag1==0)
    {
      bluetooth.println("LEVEL1");
      flag1=1;
    }
  }
  else if((noWind == 2 && lowMovement >= 2)||(noWind >= 2 && lowMovement == 2))
  {// 심각도 레벨 2인 경우
    if(flag2==0)
    {
      bluetooth.println("LEVEL2");
      flag2=1;
    }
  }
  else if(noWind == 3 && lowMovement == 3)
  {// 심각도 레벨 3인 경우
    if(flag3==0)
    {
      bluetooth.println("LEVEL3");
      flag3=1;
    }
  }
  else
  {
    flag1 = 0;
    flag2 = 0;
    flag3 = 0;
  }
}
