#include <SoftwareSerial.h>
#define Alert 10000
#define Warning 20000
#define Emergency 30000

const int pinTx = 7;  // 블루투스 TX 연결 핀 번호
const int pinRx = 6;  // 블루투스 RX 연결 핀 번호
const float zeroWindAdjustment =  .2; // negative numbers yield smaller wind speeds and vice versa.
int TMP_Therm_ADunits;  //temp termistor value from wind sensor
float RV_Wind_ADunits;    //RV output from wind sensor 
float RV_Wind_Volts;
unsigned long lastMillis;
int TempCtimes100;
float zeroWind_ADunits;
float zeroWind_volts;
float WindSpeed_MPH;
SoftwareSerial   bluetooth( pinTx, pinRx );

int inBound = 1;
int isCounting = 0;
int validVibe = 0;
int validAlarm = 0;
int validCall = 0;

unsigned long startTime=0;
unsigned long curTime=0;

 
void  setup()
{
  bluetooth.begin(9600);  // 블루투스 통신 초기화 (속도= 9600 bps)
  Serial.begin(9600);
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
  Serial.print("   WindSpeed MPH ");
  Serial.println((float)WindSpeed_MPH);
  //bluetooth.println((float)WindSpeed_MPH);

  if(WindSpeed_MPH < 2 || isnan(WindSpeed_MPH))
  {
    inBound = 0;
  }
  else
  {
    inBound = 1;
  }

  if(!inBound)
    {
      if(isCounting)
      {
        curTime = millis();
        if(curTime - startTime >= Alert && curTime - startTime < Warning)
        {
          if(validVibe==0)
          {
            bluetooth.println("Vibration!!");
            validVibe=1;      
          }

        }
        else if(curTime - startTime >= Warning && curTime - startTime < Emergency)
        {
          if(validAlarm==0)
          {
            bluetooth.println("Alarm!!");
            validAlarm=1;          
          }
        }
        else if(curTime - startTime >= Emergency)
        {
          if(validCall==0)
          {
            bluetooth.println("Call!!");
            validCall=1;          
          }
        }
      }
      else
      {
        isCounting = 1;
        startTime = millis();
      }
    }
    else
    {
      if(isCounting)
      {
        isCounting = 0;
        startTime = curTime = 0;
        validVibe = 0;
        validAlarm = 0;
        validCall = 0;
      }
    }

}
