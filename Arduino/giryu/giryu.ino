#include <SoftwareSerial.h>
// Wind MACRO
#define Alert 5000
#define Warning 10000
#define Emergency 15000

// Bluetooth global var
const int pinTx = 7;  // 블루투스 TX 연결 핀 번호
const int pinRx = 6;  // 블루투스 RX 연결 핀 번호
SoftwareSerial   bluetooth( pinTx, pinRx );

// Wind global var
const float zeroWindAdjustment =  .2; // negative numbers yield smaller wind speeds and vice versa.
int TMP_Therm_ADunits;  //temp termistor value from wind sensor
float RV_Wind_ADunits;    //RV output from wind sensor 
float RV_Wind_Volts;
unsigned long lastMillis;
int TempCtimes100;
float zeroWind_ADunits;
float zeroWind_volts;
float WindSpeed_MPH;

int inBoundW = 1;
int isCountingW = 0;
int validVibe = 0;
int validAlarm = 0;
int validCall = 0;
int noWind = 0;

String myString = "";

// Delta Time var
unsigned long startTime=0;
unsigned long curTime=0;

// Reset Function
void resetFunc()
{
  asm volatile ("jmp 0");
}
 
void  setup()
{
  bluetooth.begin(9600);  // 블루투스 통신 초기화 (속도= 9600 bps)
  Serial.begin(9600);
  while(1)
  {
    while(bluetooth.available())  //mySerial에 전송된 값이 있으면
    {
      char myChar = (char)bluetooth.read();  //mySerial int 값을 char 형식으로 변환
      myString+=myChar;   //수신되는 문자를 myString에 모두 붙임 (1바이트씩 전송되는 것을 연결)
      delay(10);           //수신 문자열 끊김 방지
    }
    if(!myString.equals(""))  //myString 값이 있다면
    {
      Serial.println("input value: "+myString); //시리얼모니터에 myString값 출력
      if(myString.equals("START"))
      {
        myString="";  //myString 변수값 초기화
        break;
      }
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
  TMP_Therm_ADunits = analogRead(A0);
  RV_Wind_ADunits = analogRead(A1);
  RV_Wind_Volts = (RV_Wind_ADunits *  0.0048828125);
  TempCtimes100 = (0.005 *((float)TMP_Therm_ADunits * (float)TMP_Therm_ADunits)) - (16.862 * (float)TMP_Therm_ADunits) + 9075.4;  
  zeroWind_ADunits = -0.0006*((float)TMP_Therm_ADunits * (float)TMP_Therm_ADunits) + 1.0727 * (float)TMP_Therm_ADunits + 47.172;  //  13.0C  553  482.39
  zeroWind_volts = (zeroWind_ADunits * 0.0048828125) - zeroWindAdjustment; 
  WindSpeed_MPH =  pow(((RV_Wind_Volts - zeroWind_volts) /.2300) , 2.7265);
  //Serial.print("  TMP volts ");
  //Serial.print(TMP_Therm_ADunits * 0.0048828125);
  //Serial.print(" RV volts ");
  //Serial.print((float)RV_Wind_Volts);
  //Serial.print("\t  TempC*100 ");
  //Serial.print(TempCtimes100 );
  //Serial.print("   ZeroWind volts ");
  //Serial.print(zeroWind_volts);
  //Serial.print("   WindSpeed MPH ");
  //Serial.println((float)WindSpeed_MPH);
  //bluetooth.println((float)WindSpeed_MPH);

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
  
  // Wind Check
  if(WindSpeed_MPH < 2 || isnan(WindSpeed_MPH))
  {
    inBoundW = 0;
  }
  else
  {
    inBoundW = 1;
  }

  if(!inBoundW)
    {
      if(isCountingW)
      {
        curTime = millis();
        if(curTime - startTime >= Alert && curTime - startTime < Warning)
        {
          if(validVibe==0)
          {
            bluetooth.println("LEVEL1");
            Serial.println("Vibration!!");
            validVibe=1;
            noWind = 1;
          }

        }
        else if(curTime - startTime >= Warning && curTime - startTime < Emergency)
        {
          if(validAlarm==0)
          {
            bluetooth.println("LEVEL2");
            Serial.println("Alarm!!");
            validAlarm=1;
            noWind = 1;
          }
        }
        else if(curTime - startTime >= Emergency)
        {
          if(validCall==0)
          {
            bluetooth.println("LEVEL3");
            Serial.println("Call!!");
            validCall=1;
            noWind = 1;
          }
        }
      }
      else
      {
        isCountingW = 1;
        startTime = millis();
      }
    }
    else
    {
      if(isCountingW)
      {
        isCountingW = 0;
        startTime = curTime = 0;
        validVibe = 0;
        validAlarm = 0;
        validCall = 0;
        noWind = 0;
      }
    }
}
